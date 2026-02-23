import React, { useState } from 'react';
import { Shield, Eye, EyeOff, Github, Mail } from 'lucide-react';

export default function LoginForm() {
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-4">
      {/* Brand Header */}
      <div className="text-center mb-8">
        <div className="inline-flex items-center justify-center w-16 h-16 rounded-xl bg-slate-900 border border-blue-500/50 mb-4 shadow-[0_0_20px_rgba(59,130,246,0.3)]">
          <Shield className="text-blue-400" size={32} />
        </div>
        <h1 className="text-4xl font-extrabold text-white tracking-tight">Mini-CTI</h1>
        <p className="text-slate-400 text-sm mt-2">Cyber Threat Intelligence Platform</p>
      </div>

      {/* Glassmorphism Card */}
      <div className="w-full max-w-md bg-glass-bg backdrop-blur-xl border border-glass-border rounded-2xl p-8 shadow-2xl">
        <h2 className="text-2xl font-bold text-white mb-1">Sign In</h2>
        <p className="text-slate-400 text-sm mb-8">Access your threat intelligence dashboard</p>

        <form className="space-y-6">
          <div>
            <label className="block text-slate-300 text-sm font-medium mb-2">Email</label>
            <input 
              type="email" 
              placeholder="analyst@sentinel.io" 
              className="w-full bg-slate-950/50 border border-slate-800 rounded-lg px-4 py-3 text-white placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all"
            />
          </div>

          <div className="relative">
            <label className="block text-slate-300 text-sm font-medium mb-2">Password</label>
            <input 
              type={showPassword ? "text" : "password"} 
              placeholder="Enter your password" 
              className="w-full bg-slate-950/50 border border-slate-800 rounded-lg px-4 py-3 text-white placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-all"
            />
            <button 
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              className="absolute right-3 bottom-3 text-slate-500 hover:text-slate-300 transition-colors"
            >
              {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
            </button>
          </div>

          <button className="w-full bg-sky-500 hover:bg-sky-400 text-slate-950 font-bold py-3 rounded-lg transition-all shadow-lg shadow-sky-500/20 active:scale-[0.98]">
            Sign In
          </button>
        </form>

        <div className="relative my-8">
          <div className="absolute inset-0 flex items-center"><div className="w-full border-t border-slate-800"></div></div>
          <div className="relative flex justify-center text-xs uppercase"><span className="bg-[#0f172a] px-2 text-slate-500 font-bold">Or continue with</span></div>
        </div>

        <div className="space-y-3">
          <button className="flex items-center justify-center gap-3 w-full bg-slate-800/40 hover:bg-slate-800 border border-slate-700 text-white py-3 rounded-lg transition-all">
            <Github size={20} /> GitHub
          </button>
          <button className="flex items-center justify-center gap-3 w-full bg-slate-800/40 hover:bg-slate-800 border border-slate-700 text-white py-3 rounded-lg transition-all">
            <Mail size={20} /> Google
          </button>
        </div>

        <p className="text-center text-slate-400 text-sm mt-8">
          Don't have an account? <span className="text-sky-400 cursor-pointer hover:underline">Sign Up</span>
        </p>
      </div>

      <p className="mt-8 text-slate-600 text-[10px] uppercase tracking-[0.2em] font-medium">
        Authorized personnel only. All activity is monitored.
      </p>
    </div>
  );
}