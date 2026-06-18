	import "./DonorDashboard.css";
	import "./CampaignCoordinatorDashboard.css";
	import DonateModal from "../components/DonateCampaignModal";
	import { useNavigate } from "react-router-dom";
	import { useState, useEffect } from "react";
	import api from "../api/axios";


	const DonorCampaignsPage = () => {
		const [campaigns, setCampaigns] = useState([]);
		const [loading, setLoading] = useState(true);
		const [error, setError] = useState("");
		const [donatingCampaign, setDonatingCampaign] = useState(null);
		const navigate = useNavigate();
		const user = JSON.parse(localStorage.getItem("user"));

		const getColor = (percent) => {
			const hue = (Math.max(0, Math.min(100, percent)) / 100) * 120;
			return `hsl(${hue}, 100%, 50%)`;
		};

		const fetchCampaigns = async () => {
			try {
				const res = await api.get("/campaigns/active");
				setCampaigns(res.data);
			} catch (err) {
				console.log(err);
				setError("Failed to load campaigns.");
			} finally {
				setLoading(false);
			}
		};

		useEffect(() => {	
			fetchCampaigns();
		}, []);

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
			<div className="donor-content">
				<div className="donor-header" style={{marginTop: 20}}>
					<div style={{display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 20}}>
						<h1 className="donor-page-title" style={{marginBottom: 0}}>Explore campaigns</h1>
						<button className="btn-primary" onClick={() => navigate('/donor')}>
							Back to dashboard
						</button>
					</div>
				</div>
				<section className="donor-campaign-grid">
					{campaigns.map((campaign) => {
						const progress = campaign.raised / campaign.goal * 100;
						return (
							<article key={campaign.id} className="donor-campaign-card donor-panel">
								<h3>{campaign.name}</h3>
								<div style={{ overflow: 'auto', maxHeight: 100}}>
									<p className="donor-small-text">{campaign.description}</p>
								</div>
								<div className="donor-campaign-progress" style={{ marginTop: 'auto'}}>
									<span>{campaign.raised}</span>
									<span>{campaign.goal}</span>
								</div>
								<div className="donor-progress-bar">
									<div className="donor-progress-fill" style={{ width: `${progress}%`, backgroundColor: getColor(progress) }} />
								</div>
								<button className="donor-btn" type="button" onClick={() => setDonatingCampaign(campaign)}>
									Donate
								</button>
							</article>
						);
					})}
				</section>
				{donatingCampaign && (
				<DonateModal
					campaign={donatingCampaign}
					donorId={user.id}
					onClose={() => { setDonatingCampaign(null); fetchCampaigns(); }}
					onDonated={() => {
						// optionally refetch or update state here
					}}
				/>
			)}
			</div>
		);
	};

	export default DonorCampaignsPage;

	/*
		{
			"id": 1,
			"name": "Test 2",
			"goal": 2000.0,
			"startDate": "2026-06-10",
			"endDate": "2026-07-20",
			"description": "Test ",
			"status": "PLANNED",
			"category": "FOOD_AID",
			"projectId": null
		}
			*/