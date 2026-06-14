import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const DonorDashboard = () => {
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

			<div className="dashboard-main">
				<div className="dashboard-card">
					<h2>Home</h2>
					<p>View your donor dashboard.</p>
					<button className="btn-primary" onClick={() => navigate('/donor/home')}>
						Go Home
					</button>
				</div>
				<br />
				<div className="dashboard-card">
					<h2>Campaigns</h2>
					<p>Browse all active fundraising campaigns.</p>
					<button className="btn-primary" onClick={() => navigate('/donor/campaigns')}>
						See all campaigns
					</button>
				</div>
				<br />
				<div className="dashboard-card">
					<h2>Projects</h2>
					<p>Explore projects supported by this organization.</p>
					<button className="btn-primary" onClick={() => navigate('/donor/projects')}>
						See all projects
					</button>
				</div>
				<br />
				<div className="dashboard-card">
					<h2>Profile</h2>
					<p>View and update your donor profile.</p>
					<button className="btn-primary" onClick={() => navigate('/donor/profile')}>
						My Profile
					</button>
				</div>
			</div>
		</div>
    );
};

export default DonorDashboard;
