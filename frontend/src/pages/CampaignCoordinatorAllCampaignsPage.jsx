import { useEffect, useState } from "react";
import api from "../api/axios";
import CampaignEditModal from "../components/CampaignEditModal";
import "./CampaignCoordinatorDashboard.css";
import { useNavigate } from "react-router-dom";

const formatDate = (dateValue) => {
	if (!dateValue) return "-";
	return new Date(dateValue).toLocaleDateString("en-GB");
};

const CampaignCoordinatorAllCampaignsPage = () => {
	const [campaigns, setCampaigns] = useState([]);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState("");
	const [selectedCampaign, setSelectedCampaign] = useState(null);
	const navigate = useNavigate();

	useEffect(() => {
		const fetchCampaigns = async () => {
			try {
				const res = await api.get("/campaigns");
				setCampaigns(res.data);
			} catch (err) {
				console.log(err);
				setError("Failed to load campaigns.");
			} finally {
				setLoading(false);
			}
		};

		fetchCampaigns();
	}, []);

	const openCampaignEditor = (campaign) => {
		setSelectedCampaign(campaign);
	};

	const handleSavedCampaign = (updatedCampaign) => {
		setCampaigns((current) => current.map((campaign) => (campaign.id === updatedCampaign.id ? updatedCampaign : campaign)));
		setSelectedCampaign(null);
	};

	const handleCardKeyDown = (event, campaign) => {
		if (event.key === "Enter" || event.key === " ") {
			event.preventDefault();
			openCampaignEditor(campaign);
		}
	};

	if (loading) {
		return (
			<div className="campaign-content">
				<h1 className="campaign-page-title" style={{ marginBottom: 34 }}>All campaigns</h1>
				<p className="donor-small-text">Loading campaigns...</p>
			</div>
		);
	}

	if (error) {
		return (
			<div className="campaign-content">
				<h1 className="campaign-page-title" style={{ marginBottom: 34 }}>All campaigns</h1>
				<p className="donor-small-text">{error}</p>
			</div>
		);
	}

	return (
		<div className="campaign-content">
			<div className="campaign-header" style={{ marginTop: 20 }}>
				<div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 20 }}>
			<h1 className="campaign-page-title" style={{ marginBottom: 34 }}>All campaigns</h1>
					<button className="btn-primary" onClick={() => navigate('/campaign-coordinator')}>
						Back to dashboard
					</button>
				</div>
			</div>
			{selectedCampaign && (
				<CampaignEditModal
					campaign={selectedCampaign}
					onClose={() => setSelectedCampaign(null)}
					onSaved={handleSavedCampaign}
				/>
			)}
			<section className="campaign-card-grid">
				{campaigns.map((campaign) => (
					<button
						key={campaign.id}
						type="button"
						className="campaign-card campaign-card-button"
						onClick={() => openCampaignEditor(campaign)}
						onKeyDown={(event) => handleCardKeyDown(event, campaign)}
					>
						<div className="campaign-card-header">
							<div className="campaign-card-title">{campaign.name}</div>
							<div className="campaign-status-pill">{campaign.status}</div>
						</div>
						<div className="campaign-card-text">
							{formatDate(campaign.startDate)} - {formatDate(campaign.endDate)}
						</div>
						<div className="campaign-card-text">Goal: ${campaign.goal ?? 0}</div>
						<div className="campaign-card-text">Project ID: {campaign.projectId ?? "-"}</div>
						<div className="campaign-card-footer">
							<span>Raised: -</span>
							<span>{campaign.status}</span>
						</div>
						<div className="campaign-progress-bar">
							<div className="campaign-progress-fill" style={{ width: "47%" }} />
						</div>
					</button>
				))}
			</section>
			{campaigns.length === 0 && <p className="donor-small-text">No campaigns found.</p>}
		</div>
	);
};

export default CampaignCoordinatorAllCampaignsPage;