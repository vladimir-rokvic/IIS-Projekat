import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import ReportModal from "../components/ReportModal";
import "./Dashboard.css";

const CampaignCoordinatorDashboard = () => {
    const navigate = useNavigate();
    const { user, logout } = useAuth();
    const [showReportModal, setShowReportModal] = useState(false);

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
                    <p>View your campaign coordinator dashboard.</p>
                    <button className="btn-primary" onClick={() => navigate('/campaign-coordinator/home')}>
                        Go Home
                    </button>
                </div>
                <br />
                <div className="dashboard-card">
                    <h2>Campaigns</h2>
                    <p>Browse all active fundraising campaigns.</p>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <button className="btn-primary" onClick={() => navigate('/campaign-coordinator/campaigns')}>
                            See all campaigns
                        </button>
                        <button className="btn-primary" onClick={() => navigate('/campaign-coordinator/create-campaign')}>
                            Create new campaign
                        </button>
                    </div>
                </div>
                <br />
                <div className="dashboard-card">
                    <h2>Statistics</h2>
                    <p>View analytics and reports for your campaigns.</p>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <button className="btn-primary" onClick={() => navigate('/campaign-coordinator/statistics')}>
                            See statistics
                        </button>
                        <button className="btn-primary" onClick={() => setShowReportModal(true)}>
                            Generate report
                        </button>
                    </div>
                </div>
                <br />
                <div className="dashboard-card">
                    <h2>Documents</h2>
                    <p>View and manage return documents for your campaigns.</p>
                    <button className="btn-primary" onClick={() => navigate('/campaign-coordinator/return-documents')}>
                        See documents
                    </button>
                </div>
            </div>

            {showReportModal && (
                <ReportModal onClose={() => setShowReportModal(false)} />
            )}
        </div>
    );
};

export default CampaignCoordinatorDashboard;