const API_BASE_URL = "http://localhost:8080/api/v1"

export default API_BASE_URL;

export async function registerUser(email: string, password: string) {
    const response = await fetch(`${API_BASE_URL}/register`,{
        method: 'POST',
        headers: {'Content-Type' : 'application/json' },
        body: JSON.stringify({ email, password})
    });

    const data = await response.json().catch(() => ({}))

    if(!response.ok) {

        // const errorData = await response.json().catch(() => ({}));

        const errorMessage = data.description || data.message || "Registration failed";


        throw new Error(errorMessage);
    }

    return data;
}

export async function loginUser(email: string, password: string) {
    const response = await fetch(`${API_BASE_URL}/users/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
    });

    const data = await response.json().catch(() => ({}))

    // const text = await response.text();  // ← Get as text first

    if (!response.ok) {

        const errorMessage = data.description || data.message || "Invalid credentials"
        throw new Error(errorMessage);
    }

    // const data = JSON.parse(text);  // ← Parse manually
    if(data.token){
        localStorage.setItem('token', data.token);
    }
    return data;
}