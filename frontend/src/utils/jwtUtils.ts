
export interface JwtPayload {
  sub: string;
  role: string;        
  exp: number;                    // Expiration timestamp
  iat?: number;                   // Issued at timestamp
}

/**
 * Decode JWT token and return payload.
 * Returns null if token is invalid or expired.
 */
export const decodeToken = (token: string): JwtPayload | null => {
  try {
    const base64Url = token.split('.')[1];
    if (!base64Url) {
      console.error('Invalid token format');
      return null;
    }

    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );

    const payload: JwtPayload = JSON.parse(jsonPayload);
    
    // Check expiration
    if (payload.exp && Date.now() >= payload.exp * 1000) {
      console.warn('Token expired');
      return null;
    }

    return payload;
  } catch (error) {
    console.error('Failed to decode token:', error);
    return null;
  }
};

/**
 * Get token from localStorage.
 */
export const getToken = (): string | null => {
  return localStorage.getItem('token');
};

/**
 * Save token to localStorage.
 */
export const saveToken = (token: string): void => {
  localStorage.setItem('token', token);
};

/**
 * Remove token from localStorage.
 */
export const removeToken = (): void => {
  localStorage.removeItem('token');
};

/**
 * Check if user has ADMIN role.
 */
export const isAdmin = (): boolean => {
  const token = getToken();
  if (!token) return false;

  const payload = decodeToken(token);
  if (!payload) return false;

  return payload.role === 'ROLE_ADMIN';
};

/**
 * Check if user has specific role.
 */
export const hasRole = (roleName: string): boolean => {
  const token = getToken();
  if (!token) return false;

  const payload = decodeToken(token);
  if (!payload) return false;
  
  return payload.role === roleName;
};


/**
 * Get User's role from token
 */
export const getUserRole = (): string | null => {
    const token = getToken();
    if(!token) return null;

    const payload = decodeToken(token);
    return payload?.role || null;
}

/**
 * Get user email/username from token.
 */
export const getUserEmail = (): string | null => {
  const token = getToken();
  if (!token) return null;

  const payload = decodeToken(token);
  return payload?.sub || null;
};

/**
 * Check if token is valid (exists and not expired).
 */
export const isTokenValid = (): boolean => {
  const token = getToken();
  if (!token) return false;

  const payload = decodeToken(token);
  return payload !== null;
};