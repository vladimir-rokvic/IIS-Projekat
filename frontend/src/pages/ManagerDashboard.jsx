import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const ManagerDashboard = () => {
    const navigate = useNavigate();
    const { user, logout } = useAuth();

    return (
        <div>
            <header className="dashboard-header">
                <h1>Welcome to Our Humanitarian Organization</h1>
                <div className="user-info" onClick={() => { logout(); navigate('/login'); }} title="Logout">
                    <div className="avatar" />
                    <span>{user?.name} {user?.surname}</span>
                </div>
            </header>

            <div className="manager-dashboard">
                <h1>Manager Dashboard</h1>
                <p>Here you will see projects ready for approval.</p>
                {/* TODO: lista projekata sa statusom SPREMAN_ZA_ODOBRENJE */}
            </div>
        </div>
    );
};

export default ManagerDashboard;
