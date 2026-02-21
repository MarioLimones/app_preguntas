import { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/client';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Check local storage on load
        const token = localStorage.getItem('token'); // or basic auth creds
        // Simple basic auth storage for prototype
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            const parsedUser = JSON.parse(storedUser);
            setUser(parsedUser);
            // Set default header
            if (parsedUser.auth) {
                api.defaults.headers.common['Authorization'] = parsedUser.auth;
            }
        }
        setLoading(false);
    }, []);

    const login = async (username, password) => {
        // Basic Auth Header
        const authHeader = 'Basic ' + btoa(username + ':' + password);
        try {
            // Test credentials with login endpoint
            const response = await api.post('/autenticacion/login', { username, password });
            const userData = { ...response.data, auth: authHeader };

            setUser(userData);
            localStorage.setItem('user', JSON.stringify(userData));
            api.defaults.headers.common['Authorization'] = authHeader;
            return userData;
        } catch (error) {
            console.error("Login failed", error);
            throw error;
        }
    };

    const register = async (username, password) => {
        try {
            await api.post('/autenticacion/register', { username, password });
            // Logic: auto login after register? Or redirect to login? Let's auto login.
            return login(username, password);
        } catch (error) {
            console.error("Register failed", error);
            throw error;
        }
    };

    const logout = () => {
        setUser(null);
        localStorage.removeItem('user');
        delete api.defaults.headers.common['Authorization'];
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};
