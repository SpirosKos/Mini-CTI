import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import IpSearch from './IpSearch';
import { CisaKevDashboard } from './CisaKevDashboard';
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
import { IpLookUpResult, lookUpApi } from '../services/ipLookUpApi';

type DashboardProps = {
  onLogout: () => void;
  activeTab: string; 
};

export default function Dashboard({ onLogout, activeTab }: DashboardProps) {
  const navigate = useNavigate();
  const { ip } = useParams(); // <-- Grabs "8.8.8.8" from the URL

  // When the user searches from the overview screen, we just change the URL!
  const handleIpSearch = (searchIp: string) => {
    if (searchIp.trim()) {
      navigate(`/ip-lookup/${searchIp.trim()}`); 
    }
  };


// export default function Dashboard({ onLogout, activeTab }: DashboardProps) {
//   const navigate = useNavigate();
//   const {ip} = useParams();
//   const [activeIp, setActiveIp] = useState('');
//   const [ipResult, setIpResult] = useState<IpLookUpResult | null>(null);
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState('');
    
//   const handleIpSearch = (searchIp: string) => {
//     setActiveIp(ip);
    
//     // Tell the router to change the URL to the IP Lookup tab
//     navigate('/ip-lookup'); 

//     //Call API
//     setLoading(true);
//     setError('');

//     try {
//       const result = await lookUpApi(ip);
//       setIpResult(result);
//       console.log('Result:', result);
//     } catch (err) {
//       setError(err instanceof Error ? err.message : "Failed to lookup IP");
//       console.error("Error:", err)
//     } finally {
//       setLoading(false);
//     }
//   };

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
            onClick={() => navigate('/dashboard')}
            className={`w-full flex items-center gap-3 p-3 rounded-lg transition-all ${
              activeTab === 'home' ? 'bg-brand-blue/10 text-brand-blue font-bold' : 'hover:bg-slate-800 text-slate-400'
            }`}
          >
            <LayoutDashboard size={20} /> Dashboard
          </button>

          <button
            onClick={() => navigate('/ip-lookup')}
            className={`w-full flex items-center gap-3 p-3 rounded-lg transition-all ${
              activeTab === 'ip-lookup' ? 'bg-brand-blue/10 text-brand-blue font-bold' : 'hover:bg-slate-800 text-slate-400'
            }`}
          >
            <Globe size={20} /> IP Lookup
          </button>

          {/* Commented out as in your original file */}
          {/* <button
            onClick={() => navigate('/cve')}
            className={`w-full flex items-center gap-3 p-3 rounded-lg transition-all ${
              activeTab === 'cve' ? 'bg-brand-blue/10 text-brand-blue font-bold' : 'hover:bg-slate-800 text-slate-400'
            }`}
          >
            <FileWarning size={20} /> CVE Search
          </button> */}

          <button
            onClick={() => navigate('/cisa-kev')}
            className={`w-full flex items-center gap-3 p-3 rounded-lg transition-all ${
              activeTab === 'cisa-kev' ? 'bg-brand-blue/10 text-brand-blue font-bold' : 'hover:bg-slate-800 text-slate-400'
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
        {activeTab === 'home' && <OverviewContent onIpSearch={handleIpSearch} />}
        {activeTab === 'ip-lookup' && <IpSearch initialIp={ip || ''} />}
        {/* {activeTab === 'cve' && <CveSearchContent />} */}
        {activeTab === 'cisa-kev' && <CisaKevContent />}
      </main>
    </div>
  );
}

// --- Sub-Component: Overview ---
function OverviewContent({ onIpSearch }: { onIpSearch: (ip: string) => void }) {
  const [ipInput, setIpInput] = useState('');
  const [cveInput, setCveInput] = useState(''); // Kept here in case you re-enable it later

  return (
    <div className="animate-in fade-in duration-500">
      <header className="mb-10">
        <h2 className="text-3xl font-bold text-white flex justify-center">Threat Intelligence Center</h2>
        <p className="text-slate-400 flex justify-center">Search global indicators and vulnerability databases</p>
      </header>

      {/* Live Threat Map Section */}
      <div className="mb-12 rounded-3xl overflow-hidden border border-slate-800 bg-slate-900/40 relative shadow-2xl">
        <div className="relative w-full pb-[45%] min-h-[400px]"> 
          <iframe width="900" height="865" 
            src="https://cybermap.kaspersky.com/en/widget/dynamic/dark" 
            title="Live Cyber Threat Map"
            className="absolute top-0 left-0 w-full h-full border-0 pointer-events-auto"
            sandbox="allow-scripts allow-same-origin allow-popups"
          />
        </div>
        <div className="absolute inset-0 pointer-events-none shadow-[inset_0_0_40px_rgba(2,6,23,0.8)]" />
      </div>

      {/* Dual Search Grid */}
      <div className="flex justify-center mb-12">
        {/* IP Search Card */}
        <div className="w-full max-w-2xl bg-slate-900/40 border border-slate-800 p-8 rounded-3xl backdrop-blur-sm hover:border-brand-blue/30 transition-all group ">
          <div className="flex items-center gap-3 mb-6">
            <div className="p-3 bg-brand-blue/10 rounded-2xl group-hover:bg-brand-blue/20 transition-colors">
              <Globe className="text-brand-blue" size={24} />
            </div>
            <h3 className="text-2xl font-bold text-white">IP LookUp</h3>
          </div>
          <p className="text-slate-400 text-m mb-6">Check reputation and internal feeds.</p>
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
      <CisaKevDashboard />
    </div>
  );
}