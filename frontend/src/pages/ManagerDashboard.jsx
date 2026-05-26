import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const ManagerDashboard = () => {
    const navigate = useNavigate();
    const { user, logout } = useAuth();

	const registerVolunteerClick = () => {
		navigate("/manager/registerVolunteer");
	}

    return (
        <div className="dashboard-page">
            <header className="dashboard-header">
                <div>
                    <h1>{user?.organizationName || "Name of the organization"}</h1>
                    <p style={{ fontSize: '0.9rem', color: '#555', marginTop: 2 }}>Text about the organization</p>
                </div>
                <div className="user-info" onClick={() => { logout(); navigate('/login'); }} title="Logout">
                    <div className="avatar" />
                    <span>{user?.name} {user?.surname}</span>
                </div>
            </header>

            <main className="dashboard-main">
                <div className="dashboard-card">
                    <h2>Our Projects</h2>
                    <p>See a full list of all projects in this organization.</p>
                    <button className="btn-primary" onClick={() => navigate('/manager/projects')}>
                        See all projects
                    </button>
                </div>

                <div className="dashboard-card" style={{ marginTop: 16 }}>
                    <h2>Reports</h2>
                    <p>See a full list of all reports in this organization.</p>
                    <button className="btn-primary" onClick={() => navigate('/manager/reports')}>
                        See all reports
                    </button>
                </div>

                <div className="dashboard-card" style={{ marginTop: 16 }}>
                    <h2>Funding requests</h2>
                    <p>See a full list of all funding requests in this organization.</p>
                    <button className="btn-primary" onClick={() => navigate('/manager/funding')}>
                        See all funding applications
                    </button>
                </div>

                <div className="dashboard-card" style={{ marginTop: 16 }}>
                    <h2>Create new distribution location</h2>
                    <p>Create a new distribution location so that it may be used for future distributions.</p>
                    <button className="btn-primary" onClick={() => navigate('/manager/distributionlocation')}>
                        Create
                    </button>
                </div>
            </main>
        </div>
    );
};

export default ManagerDashboard;
