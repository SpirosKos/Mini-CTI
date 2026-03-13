import React, { useState, useEffect } from 'react';
import {
  Globe, ShieldCheck, ShieldAlert, AlertTriangle, Loader2,
  MapPin, Building2, Network, Clock, ChevronRight, ExternalLink,
  Activity, Eye, Lock, Unlock
} from 'lucide-react';
import { lookUpApi, IpLookUpResult } from '../services/ipLookUpApi';
import { getReputation, calculateScore, getTags, formatDate} from '../utils/ipHelpers';

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


// --- Main IpSearch Component ---
interface IpSearchProps {
  initialIp?: string;
}

const IpSearch: React.FC<IpSearchProps> = ({ initialIp = '' }) => {
  const [query, setQuery] = useState(initialIp);
  const [results, setResults] = useState<any>(null);
  const [loading, setLoading] = useState(false);


  // Auto-search if initialIp is provided
  useEffect(() => {
    if (initialIp) {
      runSearch(initialIp);
    }
  }, [initialIp]);

  const isValidIp = (ip: string) =>
    /^(\d{1,3}\.){3}\d{1,3}$/.test(ip) && ip.split('.').every((o) => parseInt(o) <= 255);

  const runSearch = async (ip: string) => {
    if (!isValidIp(ip)) return;

    setLoading(true);
    // setResults(null);
    // setSearchedIp(ip);

    try {
      
      // API Call
      const data = await lookUpApi(ip);
      console.log('API Response:', data);

      setResults({
        ip: data.ipAddress,
        reputation: getReputation(data.malicious, data.suspicious),
        score: calculateScore(data.malicious, data.suspicious, data.harmless),
        country: data.country || 'Uknown',
        countryCode: data.country ? data.country.slice(0, 2).toUpperCase() : 'XX',
        city: 'N/A',
        isp: data.asOwner || "Uknown",
        org: data.asOwner ? data.asOwner.split(' ')[0] : 'Uknown',
        asn: 'N/A',
        network: 'N/A',
        type: 'N/A',
        lastSeen: formatDate(data.lastAnalysisDate),
        totalScans: data.malicious + data.suspicious + data.harmless + data.undetected,
        malicious: data.malicious,
        suspicious: data.suspicious,
        harmless: data.harmless,
        undetected: data.undetected,
        tags: getTags(data.malicious, data.suspicious, data.reputation),
        // engines: [],
      });
    } catch (error) {
      console.error('API Error:', error);
      alert("Failed to fetch IP data");
    } finally {
      setLoading(false);
    }
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

          {/* External Links */}
          <div className="bg-slate-900/50 border border-slate-800 rounded-2xl p-5">
            <h3 className="text-sm font-bold text-slate-400 uppercase tracking-widest mb-3">Investigate Further</h3>
            <div className="flex flex-wrap gap-3">
              {[
                { label: 'VirusTotal', url: `https://www.virustotal.com/gui/ip-address/${results.ip}` },
                { label: 'AbuseIPDB', url: `https://www.abuseipdb.com/check/${results.ip}` },
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
      
    </div>
  );
};

export default IpSearch;
