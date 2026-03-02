const API_BASE_URL = "http://localhost:8080/api/v1"

export default API_BASE_URL;

export async function registerUser(email: string, password: string) {
    const response = await fetch(`${API_BASE_URL}/register`,{
        method: 'POST',
        headers: {
            'Content-Type' : 'application/json'
        },
        body: JSON.stringify({ email, password})
    });

    if(!response.ok) {
        throw new Error('Registration failed');
    }

    return await response.json();
}

export async function loginUser(email: string, password: string) {
    const response = await fetch(`${API_BASE_URL}/users/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email, password })
    });

    const text = await response.text();  // ← Get as text first

    if (!response.ok) {
        throw new Error('Login failed');
    }

    const data = JSON.parse(text);  // ← Parse manually
    localStorage.setItem('token', data.token);
    return data;
}