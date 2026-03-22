import { useState, useEffect } from "react";
import { cisaKevApi, CisaKev, PageResponse } from "../services/CisaKevApi";
import { Database, AlertCircle, Loader2, ChevronLeft, ChevronRight, RefreshCw, Download } from 'lucide-react';

export const CisaKevDashboard = () => {
    const [vulnerabilities, setVulnerabilities] = useState<CisaKev[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [updating, setUpdating] = useState(false);
    const [updateMessage, setUpdateMessage] = useState<string | null>(null);
    
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [pageSize] = useState(20);
    const [isAdmin, setIsAdmin] = useState(false);

    useEffect(() => {
        // Check if user is admin
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                const authorities = payload.authorities || payload.roles || [];
                setIsAdmin(
                    authorities.includes('ROLE_ADMIN') || 
                    authorities.includes('ADMIN')
                );
            } catch (e) {
                console.error('Failed to decode token', e);
            }
        }
    }, []);

    useEffect(() => {
        loadVulnerabilities(currentPage);
    }, [currentPage]);

    // ✅ Refresh from DATABASE (fast, safe, all users)
    const loadVulnerabilities = async (page: number) => {
        try {
            setLoading(true);
            console.log('🔄 Loading from DATABASE, page:', page);
            
            const data = await cisaKevApi.getAll(page, pageSize);
            
            setVulnerabilities(data.content || []);
            setTotalPages(data.totalPages || 0);
            setTotalElements(data.totalElements || 0);
            setCurrentPage(data.number || 0);
            setError(null);
            
            console.log('✅ Loaded from DB:', data.content?.length, 'items');
        } catch(err) {
            setError("Failed to load vulnerabilities");
            console.error('❌ Error loading from DB:', err);
            setVulnerabilities([]);
        } finally {
            setLoading(false);
        }
    };

    // ✅ Update from CISA API (slow, admin only)
    const handleCisaUpdate = async () => {
        if (!window.confirm('Fetch latest data from CISA? This takes 10-30 seconds.')) {
            return;
        }

        try {
            setUpdating(true);
            setUpdateMessage(null);
            console.log('🌐 Fetching from CISA API...');
            
            const result = await cisaKevApi.triggerUpdate();
            
            setUpdateMessage(result.message || 'Update completed');
            console.log('✅ CISA update completed');
            
            // Reload from DB after CISA update
            setTimeout(() => {
                loadVulnerabilities(currentPage);
            }, 1000);
            
        } catch (err) {
            const errorMsg = 'Update failed: ' + (err instanceof Error ? err.message : 'Unknown error');
            setUpdateMessage(errorMsg);
            console.error('❌ CISA update failed:', err);
        } finally {
            setUpdating(false);
            setTimeout(() => setUpdateMessage(null), 5000);
        }
    };

    const handlePreviousPage = () => {
        if (currentPage > 0) {
            setCurrentPage(prev => prev - 1);
        }
    };

    const handleNextPage = () => {
        if (currentPage < totalPages - 1) {
            setCurrentPage(prev => prev + 1);
        }
    };

    const handlePageClick = (page: number) => {
        setCurrentPage(page);
    };

    // Loading state
    if (loading && vulnerabilities.length === 0) {
        return (
            <div className="flex flex-col items-center justify-center py-20">
                <Loader2 className="animate-spin text-brand-blue mb-4" size={48} />
                <p className="text-slate-400 text-lg">Loading vulnerabilities...</p>
            </div>
        );
    }

    // Error state
    if (error && vulnerabilities.length === 0) {
        return (
            <div className="flex flex-col items-center justify-center py-20">
                <AlertCircle className="text-red-400 mb-4" size={48} />
                <p className="text-red-400 text-lg font-medium">{error}</p>
                <button 
                    onClick={() => loadVulnerabilities(currentPage)}
                    className="mt-4 px-6 py-2 bg-brand-blue hover:bg-sky-400 text-slate-950 font-bold rounded-xl transition-all"
                >
                    Retry
                </button>
            </div>
        );
    }

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h2 className="text-3xl font-bold text-white flex items-center gap-3">
                        <Database className="text-brand-blue" size={32} />
                        CISA Known Exploited Vulnerabilities
                    </h2>
                    <p className="text-slate-400 mt-2">
                        Total: <span className="text-brand-blue font-bold">{totalElements}</span> vulnerabilities
                        {totalPages > 0 && (
                            <>
                                {' • '}
                                Page <span className="text-brand-blue font-bold">{currentPage + 1}</span> of{' '}
                                <span className="text-brand-blue font-bold">{totalPages}</span>
                            </>
                        )}
                    </p>
                </div>
                
                {/* Buttons */}
                <div className="flex items-center gap-3">
                    {/* ✅ Refresh from DB (All Users) */}
                    <button 
                        onClick={() => loadVulnerabilities(currentPage)}
                        className="flex items-center gap-2 px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 font-medium rounded-xl transition-all disabled:opacity-50"
                        disabled={loading || updating}
                        title="Reload current page from database"
                    >
                        <RefreshCw size={18} className={loading ? 'animate-spin' : ''} />
                        {loading ? 'Loading...' : 'Refresh'}
                    </button>

                    {/* ✅ Update from CISA (Admin Only) */}
                    {isAdmin && (
                        <button 
                            onClick={handleCisaUpdate}
                            className="flex items-center gap-2 px-4 py-2 bg-orange-600 hover:bg-orange-500 text-white font-bold rounded-xl transition-all disabled:opacity-50"
                            disabled={updating || loading}
                            title="Fetch latest data from CISA API (slow)"
                        >
                            <Download size={18} className={updating ? 'animate-bounce' : ''} />
                            {updating ? 'Updating from CISA...' : 'Update from CISA'}
                        </button>
                    )}
                </div>
            </div>

            {/* Success/Error Message */}
            {updateMessage && (
                <div className={`p-4 rounded-xl border animate-in fade-in ${
                    updateMessage.toLowerCase().includes('fail') || updateMessage.toLowerCase().includes('error')
                        ? 'bg-red-900/20 border-red-500 text-red-400'
                        : 'bg-green-900/20 border-green-500 text-green-400'
                }`}>
                    <p className="font-medium">{updateMessage}</p>
                </div>
            )}

            {/* Vulnerabilities List */}
            {vulnerabilities.length > 0 ? (
                <div className="space-y-4">
                    {vulnerabilities.map(vuln => (
                        <div 
                            key={vuln.id} 
                            className="bg-slate-900/50 border border-slate-800 rounded-2xl p-6 hover:border-brand-blue/30 transition-all"
                        >
                            {/* ... your existing vulnerability card ... */}
                            <div className="flex items-start justify-between mb-4">
                                <h3 className="text-xl font-bold text-red-400 font-mono">
                                    {vuln.cveID}
                                </h3>
                                <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                                    vuln.knownRansomwareCampaignUse === 'Known' 
                                        ? 'bg-red-500/20 text-red-400' 
                                        : 'bg-slate-700 text-slate-400'
                                }`}>
                                    {vuln.knownRansomwareCampaignUse === 'Known' ? '⚠️ Ransomware' : 'Not Known'}
                                </span>
                            </div>

                            <h4 className="text-lg font-semibold text-white mb-3">
                                {vuln.vulnerabilityName}
                            </h4>

                            <div className="grid grid-cols-2 gap-4 mb-4">
                                <div>
                                    <p className="text-slate-500 text-sm font-medium">Vendor</p>
                                    <p className="text-slate-200 font-medium">{vuln.vendorProject}</p>
                                </div>
                                <div>
                                    <p className="text-slate-500 text-sm font-medium">Product</p>
                                    <p className="text-slate-200 font-medium">{vuln.product}</p>
                                </div>
                                <div>
                                    <p className="text-slate-500 text-sm font-medium">Date Added</p>
                                    <p className="text-slate-200 font-medium">{vuln.dateAdded}</p>
                                </div>
                            </div>

                            <div className="mb-3">
                                <p className="text-slate-500 text-sm font-medium mb-1">Description</p>
                                <p className="text-slate-300 text-sm leading-relaxed">
                                    {vuln.shortDescription}
                                </p>
                            </div>

                            <div className="mb-3">
                                <p className="text-slate-500 text-sm font-medium mb-1">Required Action</p>
                                <p className="text-orange-400 text-sm font-medium">
                                    {vuln.requiredAction}
                                </p>
                            </div>

                            {vuln.notes && vuln.notes.trim() !== '' && (
                                <div className="mt-3 pt-3 border-t border-slate-800">
                                    <p className="text-slate-500 text-sm font-medium mb-1">Notes</p>
                                    <p className="text-slate-400 text-sm italic">
                                        {vuln.notes}
                                    </p>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            ) : (
                <div className="text-center py-20">
                    <Database className="mx-auto text-slate-700 mb-4" size={64} />
                    <p className="text-slate-500 text-lg">No vulnerabilities found</p>
                    {isAdmin && (
                        <p className="text-slate-600 text-sm mt-2">
                            Click "Update from CISA" to fetch the latest data
                        </p>
                    )}
                </div>
            )}

            {/* Pagination */}
            {totalPages > 1 && (
                <div className="flex items-center justify-between pt-6 border-t border-slate-800">
                    <button
                        onClick={handlePreviousPage}
                        disabled={currentPage === 0}
                        className={`flex items-center gap-2 px-4 py-2 rounded-xl font-medium transition-all ${
                            currentPage === 0
                                ? 'bg-slate-800 text-slate-600 cursor-not-allowed'
                                : 'bg-slate-800 text-slate-200 hover:bg-slate-700'
                        }`}
                    >
                        <ChevronLeft size={20} />
                        Previous
                    </button>

                    <div className="flex items-center gap-2">
                        {currentPage > 2 && (
                            <>
                                <button
                                    onClick={() => handlePageClick(0)}
                                    className="px-3 py-1 rounded-lg bg-slate-800 text-slate-200 hover:bg-slate-700 transition-all"
                                >
                                    1
                                </button>
                                {currentPage > 3 && <span className="text-slate-600">...</span>}
                            </>
                        )}

                        {Array.from({ length: totalPages }, (_, i) => i)
                            .filter(page => page >= currentPage - 2 && page <= currentPage + 2)
                            .map(page => (
                                <button
                                    key={page}
                                    onClick={() => handlePageClick(page)}
                                    className={`px-3 py-1 rounded-lg font-medium transition-all ${
                                        page === currentPage
                                            ? 'bg-brand-blue text-slate-950'
                                            : 'bg-slate-800 text-slate-200 hover:bg-slate-700'
                                    }`}
                                >
                                    {page + 1}
                                </button>
                            ))}

                        {currentPage < totalPages - 3 && (
                            <>
                                {currentPage < totalPages - 4 && <span className="text-slate-600">...</span>}
                                <button
                                    onClick={() => handlePageClick(totalPages - 1)}
                                    className="px-3 py-1 rounded-lg bg-slate-800 text-slate-200 hover:bg-slate-700 transition-all"
                                >
                                    {totalPages}
                                </button>
                            </>
                        )}
                    </div>

                    <button
                        onClick={handleNextPage}
                        disabled={currentPage === totalPages - 1}
                        className={`flex items-center gap-2 px-4 py-2 rounded-xl font-medium transition-all ${
                            currentPage === totalPages - 1
                                ? 'bg-slate-800 text-slate-600 cursor-not-allowed'
                                : 'bg-slate-800 text-slate-200 hover:bg-slate-700'
                        }`}
                    >
                        Next
                        <ChevronRight size={20} />
                    </button>
                </div>
            )}
        </div>
    );
}