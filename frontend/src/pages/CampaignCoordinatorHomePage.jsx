import { useNavigate } from "react-router-dom";
import "./CampaignCoordinatorDashboard.css";

const stats = [
	{ label: "Total raised", value: "18,000$" },
	{ label: "Active campaigns", value: "13" },
	{ label: "Total donors", value: "840" },
	{ label: "Finished campaigns", value: "54" },
]

const topCampaigns = ["Item One", "Item Two", "Item Three"];
const recentActivity = ["Item One", "Item Two", "Item Three"];

const CampaignCoordinatorHomePage = () => {
	const navigate = useNavigate();

	return (
		<div className="campaign-content">
			<div className="campaign-header" style={{ marginTop: 20 }}>
				<div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 20 }}>
					<h1 className="campaign-page-title" style={{ marginBottom: 34 }}>Dashboard overview</h1>
					<button className="btn-primary" onClick={() => navigate('/campaign-coordinator')}>
						Back to dashboard
					</button>
				</div>
			</div>

			<section className="campaign-stats-grid">
				{stats.map((stat) => (
					<div key={stat.label} className="campaign-stat-card">
						<div className="campaign-stat-label">{stat.label}</div>
						<div className="campaign-stat-value">{stat.value}</div>
					</div>
				))}
			</section>

			<section className="campaign-panels-grid">
				<div className="campaign-panel">
					<h2>Top performing campaigns</h2>
					<div className="campaign-list-box">
						{topCampaigns.map((item) => <span key={item}>{item}</span>)}
					</div>
				</div>
				<div className="campaign-panel">
					<h2>Recent activity</h2>
					<div className="campaign-list-box">
						{recentActivity.map((item) => <span key={item}>{item}</span>)}
					</div>
				</div>
			</section>
		</div>
	);
};

export default CampaignCoordinatorHomePage;