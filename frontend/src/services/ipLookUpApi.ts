import API_BASE_URL from "./api";

export async function lookUpApi(ipAddress: string) {
    const token = localStorage.getItem('token');

    const response = await fetch(`${API_BASE_URL}/ip-lookup/${ipAddress}`,{
        method: `GET`,
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    } );

    if(!response.ok) {
        throw new Error('IP lookup failed');
    }

    return await response.json();
}

export interface IpLookUpResult {
    ipAddress: string;
    country: string;
    asOwner: string;
    malicious: number;
    suspicious: number;
    harmless: number;
    undetected: number;
    reputation: number;
    lastAnalysisDate: string;
}