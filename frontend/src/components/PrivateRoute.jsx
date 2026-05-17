import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

// Zaštićena ruta — ako nije ulogovan, šalje na /login
// allowedRoles npr. ["COORDINATOR"] ili ["MANAGER"] ili null (bilo ko)
const PrivateRoute = ({ children, allowedRoles }) => {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    if (allowedRoles && !allowedRoles.includes(user.role)) {
        // Ulogovan ali nema pravo — šalje na svoju početnu stranicu
        if (user.role === "MANAGER") return <Navigate to="/manager" replace />;
        return <Navigate to="/" replace />;
    }

    return children;
};

export default PrivateRoute;
