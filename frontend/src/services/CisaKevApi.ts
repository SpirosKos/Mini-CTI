import API_BASE_URL from "./api";

export interface CisaKev{
    id: number;
    cveID: string;
    vendorProject: string;
    product: string;
    vulnerabilityName: string;
    dateAdded: string;
    shortDescription: string;
    requiredAction: string;

    knownRansomwareCampaignUse: string;
    notes: string;
}

export interface PageResponse<T>{
    content: T[];
    pageable: {
        pageNumber: number;
        pageSize: number;
    };
    totalPages: number;
    totalElements: number;
    last: boolean;
    first: boolean;
    numberOfElements: number;
    size: number;
    number: number;
    empty: boolean;
}


export const cisaKevApi = {
    // Get Paginated Vulnerabilities
    getAll: async(
        page: number = 0,
        size: number = 20,
        sort: string = 'dateAdded,desc'
    ): Promise<PageResponse<CisaKev>> => {
        const token = localStorage.getItem('token');
        const response = await fetch(`${API_BASE_URL}/cisa-kev?page=${page}&size=${size}&sort=${sort}`, {
            headers:{
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if(!response.ok) {
            throw new Error('Failed to fetch vulnerabilities')
        }

        return response.json();
    },

    triggerUpdate: async (): Promise<{ status: string; message: string }> => {
        const token = localStorage.getItem('token');
        
        if (!token) {
            throw new Error('No authentication token found');
        }

        const url = `${API_BASE_URL}/cisa-kev/update`;
        console.log('🌐 Triggering CISA update:', url);
        
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        console.log('📊 Update response status:', response.status);

        if (!response.ok) {
            const errorText = await response.text();
            console.error('❌ Update error:', errorText);
            throw new Error(`Update failed: ${response.status}`);
        }

        const result = await response.json();
        console.log('✅ Update result:', result);
        
        return result;
    }
}