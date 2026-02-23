import React, { useState, useEffect } from 'react';
import {
  Globe, ShieldCheck, ShieldAlert, AlertTriangle, Loader2,
  MapPin, Building2, Network, Clock, ChevronRight, ExternalLink,
  Activity, Eye, Lock, Unlock
} from 'lucide-react';

// --- Mock data generator (replace API calls with real ones later) ---
function getMockIpData(ip: string) {
  const seeds: Record<string, any> = {
    '8.8.8.8': {
      ip: '8.8.8.8',
      reputation: 'clean',
      score: 0,
      country: 'United States',
      countryCode: 'US',
      city: 'Mountain View',
      isp: 'Google LLC',
      org: 'GOOGLE',
      asn: 'AS15169',
      network: '8.8.8.0/24',
      type: 'DNS Resolver',
      lastSeen: '2 hours ago',
      totalScans: 94,
      malicious: 0,
      suspicious: 0,
      harmless: 90,
      undetected: 4,
      tags: ['CDN', 'Legitimate', 'DNS'],
      engines: [
        { name: 'Cloudforce One', result: 'clean' },
        { name: 'ArcSight Threat Intel', result: 'clean' },
        { name: 'Criminal IP', result: 'clean' },
        { name: 'Emerging Threats', result: 'clean' },
        { name: 'Maltiverse', result: 'clean' },
        { name: 'Pulsedive', result: 'clean' },
        { name: 'Scumware.org', result: 'clean' },
        { name: 'Spur.us', result: 'clean' },
      ],
    },
  };

  if (seeds[ip]) return seeds[ip];

  // Generate deterministic-ish mock for unknown IPs
  const octets = ip.split('.').map(Number);
  const seed = octets.reduce((a, b) => a + b, 0);
  const isMalicious = seed % 5 === 0;
  const isSuspicious = seed % 3 === 0 && !isMalicious;

  const countries = ['Russia', 'China', 'Germany', 'Netherlands', 'Brazil', 'Iran', 'Ukraine', 'France'];
  const isps = ['Hetzner Online GmbH', 'DigitalOcean LLC', 'Linode LLC', 'OVH SAS', 'Alibaba Cloud', 'Amazon.com'];
  const countryIdx = seed % countries.length;

  return {
    ip,
    reputation: isMalicious ? 'malicious' : isSuspicious ? 'suspicious' : 'clean',
    score: isMalicious ? Math.floor(seed % 40) + 15 : isSuspicious ? Math.floor(seed % 10) + 3 : 0,
    country: countries[countryIdx],
    countryCode: countries[countryIdx].slice(0, 2).toUpperCase(),
    city: 'Unknown',
    isp: isps[seed % isps.length],
    org: isps[seed % isps.length].split(' ')[0].toUpperCase(),
    asn: `AS${10000 + (seed * 37) % 50000}`,
    network: `${octets[0]}.${octets[1]}.0.0/16`,
    type: isMalicious ? 'Threat Actor' : 'Hosting Provider',
    lastSeen: `${(seed % 24) + 1} hours ago`,
    totalScans: 70 + (seed % 30),
    malicious: isMalicious ? Math.floor(seed % 20) + 5 : 0,
    suspicious: isSuspicious ? Math.floor(seed % 5) + 1 : 0,
    harmless: isMalicious ? 30 + (seed % 20) : 60 + (seed % 30),
    undetected: 5 + (seed % 10),
    tags: isMalicious
      ? ['Botnet', 'C2 Server', 'Malware']
      : isSuspicious
      ? ['Proxy', 'VPN', 'Tor Exit']
      : ['Hosting', 'Cloud'],
    engines: [
      { name: 'Cloudforce One', result: isMalicious ? 'malicious' : 'clean' },
      { name: 'ArcSight Threat Intel', result: isMalicious ? 'malicious' : isSuspicious ? 'suspicious' : 'clean' },
      { name: 'Criminal IP', result: isMalicious ? 'malicious' : 'clean' },
      { name: 'Emerging Threats', result: isSuspicious ? 'suspicious' : 'clean' },
      { name: 'Maltiverse', result: isMalicious ? 'malicious' : 'clean' },
      { name: 'Pulsedive', result: isMalicious ? 'malicious' : isSuspicious ? 'suspicious' : 'clean' },
      { name: 'Scumware.org', result: 'clean' },
      { name: 'Spur.us', result: isSuspicious ? 'suspicious' : 'clean' },
    ],
  };
}

// --- Verdict Badge ---
function VerdictBadge({ reputation }: { reputation: string }) {
  const config = {
    clean: { icon: ShieldCheck, color: 'text-emerald-400', bg: 'bg-emerald-400/10 border-emerald-400/30', label: 'Clean' },
    suspicious: { icon: AlertTriangle, color: 'text-yellow-400', bg: 'bg-yellow-400/10 border-yellow-400/30', label: 'Suspicious' },
    malicious: { icon: ShieldAlert, color: 'text-red-400', bg: 'bg-red-400/10 border-red-400/30', label: 'Malicious' },
  }[reputation] ?? { icon: ShieldCheck, color: 'text-slate-400', bg: 'bg-slate-400/10 border-slate-400/30', label: 'Unknown' };

  const Icon = config.icon;
  return (
    <span className={`inline-flex items-center gap-2 px-3 py-1.5 rounded-full border text-sm font-bold ${config.color} ${config.bg}`}>
      <Icon size={16} /> {config.label}
    </span>
  );
}

// --- Engine Result Row ---
function EngineRow({ name, result }: { name: string; result: string }) {
  const colorMap: Record<string, string> = {
    malicious: 'text-red-400',
    suspicious: 'text-yellow-400',
    clean: 'text-emerald-400',
  };
  const dotMap: Record<string, string> = {
    malicious: 'bg-red-400',
    suspicious: 'bg-yellow-400',
    clean: 'bg-emerald-400',
  };
  return (
    <div className="flex items-center justify-between py-2.5 border-b border-slate-800/60 last:border-0">
      <span className="text-slate-300 text-sm font-mono">{name}</span>
      <span className={`flex items-center gap-2 text-sm font-bold capitalize ${colorMap[result] ?? 'text-slate-400'}`}>
        <span className={`w-2 h-2 rounded-full ${dotMap[result] ?? 'bg-slate-400'}`} />
        {result}
      </span>
    </div>
  );
}

// --- Main IpSearch Component ---
interface IpSearchProps {
  initialIp?: string;
}

const IpSearch: React.FC<IpSearchProps> = ({ initialIp = '' }) => {
  const [query, setQuery] = useState(initialIp);
  const [results, setResults] = useState<ReturnType<typeof getMockIpData> | null>(null);
  const [loading, setLoading] = useState(false);
  const [searchedIp, setSearchedIp] = useState('');

  // Auto-search if initialIp is provided
  useEffect(() => {
    if (initialIp) {
      runSearch(initialIp);
    }
  }, [initialIp]);

  const isValidIp = (ip: string) =>
    /^(\d{1,3}\.){3}\d{1,3}$/.test(ip) && ip.split('.').every((o) => parseInt(o) <= 255);

  const runSearch = (ip: string) => {
    if (!isValidIp(ip)) return;
    setLoading(true);
    setResults(null);
    setSearchedIp(ip);

    // Simulate network delay
    setTimeout(() => {
      setResults(getMockIpData(ip));
      setLoading(false);
    }, 900);
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    runSearch(query.trim());
  };

  const scoreColor = (score: number) =>
    score === 0 ? 'text-emerald-400' : score < 10 ? 'text-yellow-400' : 'text-red-400';

  return (
    <div className="animate-in fade-in duration-500 max-w-4xl">
      {/* Header */}
      <header className="mb-8">
        <h2 className="text-3xl font-bold text-white">IP Lookup</h2>
        <p className="text-slate-400 mt-1">Reputation check across multiple threat intelligence feeds</p>
      </header>

      {/* Search Bar */}
      <form onSubmit={handleSearch} className="flex gap-3 mb-8">
        <div className="relative flex-1">
          <Globe className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" size={20} />
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Enter IP address (e.g. 1.1.1.1)..."
            className="w-full bg-slate-950 border border-slate-800 rounded-2xl py-4 pl-12 pr-4 text-white font-mono outline-none focus:ring-2 focus:ring-brand-blue/50 transition-all"
          />
        </div>
        <button
          type="submit"
          disabled={loading}
          className="px-6 py-4 bg-sky-500 hover:bg-sky-400 disabled:opacity-50 disabled:cursor-not-allowed text-slate-950 font-bold rounded-2xl transition-all flex items-center gap-2"
        >
          {loading ? <Loader2 size={20} className="animate-spin" /> : <><Activity size={20} /> Analyze</>}
        </button>
      </form>

      {/* Loading State */}
      {loading && (
        <div className="bg-slate-900/50 border border-slate-800 rounded-2xl p-12 text-center">
          <Loader2 className="mx-auto text-sky-400 animate-spin mb-4" size={40} />
          <p className="text-slate-400 font-mono">Querying threat intelligence feeds for <span className="text-white">{searchedIp}</span>...</p>
          <div className="mt-4 flex justify-center gap-2 text-xs text-slate-600">
            {['VirusTotal', 'AbuseIPDB', 'Shodan', 'Criminal IP', 'Pulsedive'].map((f) => (
              <span key={f} className="bg-slate-800 px-2 py-1 rounded-full">{f}</span>
            ))}
          </div>
        </div>
      )}

      {/* Results */}
      {results && !loading && (
        <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">

          {/* Top Summary Card */}
          <div className={`border rounded-2xl p-6 ${
            results.reputation === 'malicious' ? 'bg-red-950/20 border-red-500/30' :
            results.reputation === 'suspicious' ? 'bg-yellow-950/20 border-yellow-500/30' :
            'bg-emerald-950/20 border-emerald-500/30'
          }`}>
            <div className="flex flex-wrap items-start justify-between gap-4">
              <div>
                <div className="flex items-center gap-3 mb-2">
                  <span className="text-2xl font-bold font-mono text-white">{results.ip}</span>
                  <VerdictBadge reputation={results.reputation} />
                </div>
                <div className="flex flex-wrap gap-2 mt-3">
                  {results.tags.map((tag: string) => (
                    <span key={tag} className="bg-slate-800 text-slate-300 text-xs px-2.5 py-1 rounded-full font-mono">{tag}</span>
                  ))}
                </div>
              </div>
              <div className="text-right">
                <p className="text-slate-400 text-xs uppercase tracking-widest mb-1">Detection Score</p>
                <p className={`text-5xl font-extrabold ${scoreColor(results.score)}`}>
                  {results.score}<span className="text-2xl text-slate-500">/100</span>
                </p>
              </div>
            </div>
          </div>

          {/* Info Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Geo / ASN Card */}
            <div className="bg-slate-900/50 border border-slate-800 rounded-2xl p-6">
              <h3 className="text-sm font-bold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
                <MapPin size={14} /> Network Info
              </h3>
              <div className="space-y-3">
                {[
                  { label: 'ISP / Org', value: results.isp },
                  { label: 'ASN', value: results.asn },
                  { label: 'Network', value: results.network },
                  { label: 'Location', value: `${results.city}, ${results.country}` },
                  { label: 'Type', value: results.type },
                ].map(({ label, value }) => (
                  <div key={label} className="flex justify-between text-sm">
                    <span className="text-slate-500 font-medium">{label}</span>
                    <span className="text-slate-200 font-mono">{value}</span>
                  </div>
                ))}
              </div>
            </div>

            {/* Scan Stats Card */}
            <div className="bg-slate-900/50 border border-slate-800 rounded-2xl p-6">
              <h3 className="text-sm font-bold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
                <Eye size={14} /> Scan Results
              </h3>
              <div className="space-y-3">
                {[
                  { label: 'Malicious', value: results.malicious, color: 'text-red-400' },
                  { label: 'Suspicious', value: results.suspicious, color: 'text-yellow-400' },
                  { label: 'Harmless', value: results.harmless, color: 'text-emerald-400' },
                  { label: 'Undetected', value: results.undetected, color: 'text-slate-400' },
                  { label: 'Total Engines', value: results.totalScans, color: 'text-sky-400' },
                ].map(({ label, value, color }) => (
                  <div key={label} className="flex justify-between items-center text-sm">
                    <span className="text-slate-500 font-medium">{label}</span>
                    <span className={`font-bold font-mono ${color}`}>{value}</span>
                  </div>
                ))}
              </div>
              {/* Progress bar */}
              <div className="mt-4 h-2 bg-slate-800 rounded-full overflow-hidden flex">
                <div className="h-full bg-red-500" style={{ width: `${(results.malicious / results.totalScans) * 100}%` }} />
                <div className="h-full bg-yellow-500" style={{ width: `${(results.suspicious / results.totalScans) * 100}%` }} />
                <div className="h-full bg-emerald-500" style={{ width: `${(results.harmless / results.totalScans) * 100}%` }} />
                <div className="h-full bg-slate-600" style={{ width: `${(results.undetected / results.totalScans) * 100}%` }} />
              </div>
              <div className="mt-2 flex items-center gap-1 text-slate-500 text-xs">
                <Clock size={12} /> Last scan: {results.lastSeen}
              </div>
            </div>
          </div>

          {/* Engine Breakdown */}
          <div className="bg-slate-900/50 border border-slate-800 rounded-2xl p-6">
            <h3 className="text-sm font-bold text-slate-400 uppercase tracking-widest mb-4 flex items-center gap-2">
              <Network size={14} /> Engine Breakdown
            </h3>
            <div>
              {results.engines.map((engine: { name: string; result: string }) => (
                <EngineRow key={engine.name} name={engine.name} result={engine.result} />
              ))}
            </div>
          </div>

          {/* External Links */}
          <div className="bg-slate-900/50 border border-slate-800 rounded-2xl p-5">
            <h3 className="text-sm font-bold text-slate-400 uppercase tracking-widest mb-3">Investigate Further</h3>
            <div className="flex flex-wrap gap-3">
              {[
                { label: 'VirusTotal', url: `https://www.virustotal.com/gui/ip-address/${results.ip}` },
                { label: 'AbuseIPDB', url: `https://www.abuseipdb.com/check/${results.ip}` },
                { label: 'Shodan', url: `https://www.shodan.io/host/${results.ip}` },
                { label: 'ipapi', url: `https://ipapi.co/${results.ip}` },
              ].map(({ label, url }) => (
                <a
                  key={label}
                  href={url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex items-center gap-2 bg-slate-800 hover:bg-slate-700 text-slate-300 text-sm px-4 py-2 rounded-xl transition-all font-mono"
                >
                  <ExternalLink size={14} /> {label}
                </a>
              ))}
            </div>
          </div>

        </div>
      )}

      {/* Empty State */}
      {!results && !loading && (
        <div className="bg-slate-900/30 border border-slate-800/50 rounded-2xl p-16 text-center">
          <Globe className="mx-auto text-slate-700 mb-4" size={48} />
          <p className="text-slate-500 font-mono">Enter an IP address to begin analysis</p>
          <p className="text-slate-600 text-sm mt-2">Try: 8.8.8.8 · 1.1.1.1 · 192.168.1.1</p>
        </div>
      )}
    </div>
  );
};

export default IpSearch;
