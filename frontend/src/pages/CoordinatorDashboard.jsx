import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const CoordinatorDashboard = () => {
    const navigate = useNavigate();
    const { user, logout } = useAuth();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div className="dashboard-page">
            <header className="dashboard-header">
                <h1>Welcome to Our Humanitarian Organization</h1>
                <div className="user-info" onClick={handleLogout} title="Click to logout">
                    <div className="avatar" />
                    <span>{user?.name} {user?.surname}</span>
                </div>
            </header>

            <main className="dashboard-main">
                <div className="dashboard-card">
                    <h2>Our Projects</h2>
                    <p>See a full list of all projects in this organization.</p>
                    <button
                        className="btn-primary"
                        onClick={() => navigate('/projects')}
                    >
                        See all projects
                    </button>
                </div>
				<br />
                <div className="dashboard-card">
                    <h2>See tasks</h2>
                    <p>See a full list of all tasks outside the projects in this organization.</p>
                    <button
                        className="btn-primary"
                        onClick={() => navigate('/tasks')}
                    >
                        See all tasks
                    </button>
                </div>
            </main>
        </div>
    );
};

export default CoordinatorDashboard;
