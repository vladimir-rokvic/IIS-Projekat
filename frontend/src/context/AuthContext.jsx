import { createContext, useContext, useState } from "react";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(() => {
        const saved = localStorage.getItem("user");
        if (!saved) return null;

        const parsed = JSON.parse(saved);

        // Provjeri da li je token istekao
        try {
            const payload = JSON.parse(atob(parsed.token.split('.')[1]));
            if (payload.exp * 1000 < Date.now()) {
                localStorage.removeItem("user");
                return null;
            }
        } catch {
            localStorage.removeItem("user");
            return null;
        }

        return parsed;
    });

    const login = (userData) => {
        localStorage.setItem("user", JSON.stringify(userData));
        setUser(userData);
    };

    const logout = () => {
        localStorage.removeItem("user");
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
