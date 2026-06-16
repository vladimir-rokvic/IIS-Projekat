import { useNavigate } from "react-router-dom";
import "./CampaignCoordinatorDashboard.css";
import { useEffect, useState } from "react";
import api from "../api/axios";

const stats = [
	{ label: "Total raised", value: "18,000$" },
	{ label: "Active campaigns", value: "13" },
	{ label: "Total donors", value: "840" },
	{ label: "Finished campaigns", value: "54" },
]

const topCampaigns = ["Item One", "Item Two", "Item Three"];
const recentActivity = ["Item One", "Item Two", "Item Three"];

const CampaignCoordinatorHomePage = () => {
	const [dashboardData, setDashboardData] = useState(null);
	const [loading, setLoading] = useState(true);
	const navigate = useNavigate();

	const fetchDashboardData = async () => {
		try {
			const res = await api.get("/campaigns/coordinator-dashboard");
			setDashboardData(res.data);
		} catch (err) {
			console.log(err);
			setError("Failed to load dashboard data.");
		} finally {
			setLoading(false);
		}
    };
	useEffect(() => {
		fetchDashboardData();
	}, []);

	const moneyFormatter = new Intl.NumberFormat('en-US', {
		style: 'currency',
		currency: 'USD',
		minimumFractionDigits: 0,
	});

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

			{dashboardData && (
			<>
			<section className="campaign-stats-grid">
					<div className="campaign-stat-card">
						<div className="campaign-stat-label">Active campaigns</div>
						<div className="campaign-stat-value">{dashboardData.totalActiveCampaigns}</div>
					</div>
					<div className="campaign-stat-card">
						<div className="campaign-stat-label">Total raised</div>
						<div className="campaign-stat-value">{moneyFormatter.format(dashboardData.totalRaisedAmount)}</div>
					</div>
					<div className="campaign-stat-card">
						<div className="campaign-stat-label">Total donors</div>
						<div className="campaign-stat-value">{dashboardData.totalDonors}</div>
					</div>
					<div className="campaign-stat-card">
						<div className="campaign-stat-label">Finished campaigns</div>
						<div className="campaign-stat-value">{dashboardData.totalFinishedCampaigns}</div>
					</div>

			</section>
			<section className="campaign-panels-grid">
				<div className="campaign-panel">
					<h2>Top performing campaigns</h2>
					<div className="campaign-list-box">
						{dashboardData.topPerformingCampaigns.map((item) => <span key={item.id}>{item.name} - {moneyFormatter.format(item.raised)}</span>)}
					</div>
				</div>
				<div className="campaign-panel">
					<h2>Recent activity</h2>
					<div className="campaign-list-box">
						{dashboardData.recentActivity.map((item) => <span key={item.id}>{item}</span>)}
					</div>
				</div>
			</section>
			</>
			)}
		</div>
	);
};

export default CampaignCoordinatorHomePage;