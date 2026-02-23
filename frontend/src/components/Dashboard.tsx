import React, { useState } from 'react';
import IpSearch from './IpSearch';
import { 
  LayoutDashboard, 
  ShieldAlert, 
  Database, 
  Activity, 
  Search, 
  LogOut, 
  FileWarning, 
  Globe,
  ChevronRight
} from 'lucide-react';

export default function Dashboard({ onLogout }: { onLogout: () => void }) {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [activeIp, setActiveIp] = useState('');

  // Called from the overview IP card — navigates to ip-search tab with the IP
  const handleIpSearch = (ip: string) => {
    setActiveIp(ip);
    setActiveTab('ip-search');
  };

  return (
    <div className="min-h-screen bg-[#020617] text-slate-200 flex">
      {/* Sidebar */}
      <aside className="w-64 border-r border-slate-800 bg-slate-900/50 p-6 flex flex-col fixed h-full">
        <div className="flex items-center gap-3 mb-10">
          <ShieldAlert className="text-brand-blue" size={28} />
          <span className="text-xl font-bold text-white tracking-tight">Mini-CTI</span>
        </div>

        <nav className="space-y-2 flex-1">
          <button
            onClick={() => setActiveTab('dashboard')}
            className={`w-full flex items-center gap-3 p-3 rounded-lg transition-all ${
              activeTab === 'dashboard' ? 'bg-brand-blue/10 text-brand-blue font-bold' : 'hover:bg-slate-800 text-slate-400'
            }`}
          >
            <LayoutDashboard size={20} /> Dashboard
          </button>

          <button
            onClick={() => setActiveTab('ip-search')}
            className={`w-full flex items-center gap-3 p-3 rounded-lg transition-all ${
              activeTab === 'ip-search' ? 'bg-brand-blue/10 text-brand-blue font-bold' : 'hover:bg-slate-800 text-slate-400'
            }`}
          >
            <Globe size={20} /> IP Lookup
          </button>

          <button
            onClick={() => setActiveTab('cve')}
            className={`w-full flex items-center gap-3 p-3 rounded-lg transition-all ${
              activeTab === 'cve' ? 'bg-brand-blue/10 text-brand-blue font-bold' : 'hover:bg-slate-800 text-slate-400'
            }`}
          >
            <FileWarning size={20} /> CVE Search
          </button>

          <button
            onClick={() => setActiveTab('cisa')}
            className={`w-full flex items-center gap-3 p-3 rounded-lg transition-all ${
              activeTab === 'cisa' ? 'bg-brand-blue/10 text-brand-blue font-bold' : 'hover:bg-slate-800 text-slate-400'
            }`}
          >
            <Database size={20} /> CISA KEV
          </button>
        </nav>

        <button
          type="button"
          onClick={onLogout}
          className="flex items-center gap-3 p-3 text-slate-500 hover:text-red-400 transition-colors mt-auto border-t border-slate-800 pt-4 cursor-pointer group"
        >
          <LogOut size={20} className="group-hover:scale-110 transition-transform" />
          <span className="font-bold">Logout</span>
        </button>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 ml-64 p-8 overflow-y-auto">
        {activeTab === 'dashboard' && <OverviewContent onIpSearch={handleIpSearch} />}
        {activeTab === 'ip-search' && <IpSearch initialIp={activeIp} />}
        {activeTab === 'cve' && <CveSearchContent />}
        {activeTab === 'cisa' && <CisaKevContent />}
      </main>
    </div>
  );
}

// --- Sub-Component: Overview ---
function OverviewContent({ onIpSearch }: { onIpSearch: (ip: string) => void }) {
  const [ipInput, setIpInput] = useState('');
  const [cveInput, setCveInput] = useState('');

  return (
    <div className="animate-in fade-in duration-500">
      <header className="mb-10">
        <h2 className="text-3xl font-bold text-white">Threat Intelligence Center</h2>
        <p className="text-slate-400">Search global indicators and vulnerability databases</p>
      </header>

      {/* Dual Search Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-12">

        {/* IP Search Card */}
        <div className="bg-slate-900/40 border border-slate-800 p-8 rounded-3xl backdrop-blur-sm hover:border-brand-blue/30 transition-all group">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-3 bg-brand-blue/10 rounded-2xl group-hover:bg-brand-blue/20 transition-colors">
              <Globe className="text-brand-blue" size={24} />
            </div>
            <h3 className="text-xl font-bold text-white">IP LookUp</h3>
          </div>
          <p className="text-slate-400 text-sm mb-6">Check reputation and internal feeds.</p>
          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" size={20} />
            <input
              type="text"
              value={ipInput}
              onChange={(e) => setIpInput(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && ipInput.trim() && onIpSearch(ipInput.trim())}
              placeholder="Enter IP (e.g. 8.8.8.8)..."
              className="w-full bg-slate-950 border border-slate-800 rounded-2xl py-4 pl-12 pr-32 outline-none focus:ring-2 focus:ring-brand-blue/50 text-white font-mono"
            />
            <button
              onClick={() => ipInput.trim() && onIpSearch(ipInput.trim())}
              className="absolute right-3 top-1/2 -translate-y-1/2 flex items-center gap-1.5 bg-sky-500 hover:bg-sky-400 text-slate-950 text-sm font-bold px-4 py-2 rounded-xl transition-all"
            >
              Analyze <ChevronRight size={16} />
            </button>
          </div>
        </div>

        {/* CVE Search Card */}
        <div className="bg-slate-900/40 border border-slate-800 p-8 rounded-3xl backdrop-blur-sm hover:border-orange-500/30 transition-all group">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-3 bg-orange-500/10 rounded-2xl group-hover:bg-orange-500/20 transition-colors">
              <FileWarning className="text-orange-500" size={24} />
            </div>
            <h3 className="text-xl font-bold text-white">Vulnerability Search</h3>
          </div>
          <p className="text-slate-400 text-sm mb-6">Query the NVD for CVE details, severity scores, and patches.</p>
          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" size={20} />
            <input
              type="text"
              value={cveInput}
              onChange={(e) => setCveInput(e.target.value)}
              placeholder="Enter CVE (e.g. CVE-2024-0001)..."
              className="w-full bg-slate-950 border border-slate-800 rounded-2xl py-4 pl-12 pr-4 outline-none focus:ring-2 focus:ring-orange-500/40 text-white font-mono"
            />
          </div>
        </div>
      </div>

      {/* Quick Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl">
          <p className="text-slate-400 text-sm font-medium">Total Indicators</p>
          <p className="text-3xl font-bold mt-2 text-blue-400">12,842</p>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl">
          <p className="text-slate-400 text-sm font-medium">High Severity</p>
          <p className="text-3xl font-bold mt-2 text-red-400">154</p>
        </div>
        <div className="bg-slate-900 border border-slate-800 p-6 rounded-2xl">
          <p className="text-slate-400 text-sm font-medium">Active Scans</p>
          <p className="text-3xl font-bold mt-2 text-emerald-400">28</p>
        </div>
      </div>
    </div>
  );
}

// --- Sub-Component: CVE Search Page ---
function CveSearchContent() {
  return (
    <div className="animate-in fade-in slide-in-from-left-4 duration-500">
      <header className="mb-10">
        <h2 className="text-3xl font-bold text-white">Full CVE Database</h2>
        <p className="text-slate-400 font-medium">Deep query across the National Vulnerability Database</p>
      </header>
      <div className="bg-slate-900/50 border border-slate-800 p-12 rounded-2xl text-center">
        <p className="text-slate-500 font-mono">Advanced filtering and export options will appear here.</p>
      </div>
    </div>
  );
}

// --- Sub-Component: CISA KEV Content ---
function CisaKevContent() {
  return (
    <div className="animate-in fade-in duration-500">
      <h2 className="text-3xl font-bold text-white mb-2">CISA KEV Catalog</h2>
      <p className="text-slate-400 mb-10">Known Exploited Vulnerabilities</p>
      <div className="bg-slate-900 border border-slate-800 p-12 rounded-2xl text-center">
        <Database className="mx-auto text-slate-800 mb-4" size={48} />
        <p className="text-slate-500">Connect to Spring Boot backend to sync the live JSON feed.</p>
      </div>
    </div>
  );
}
