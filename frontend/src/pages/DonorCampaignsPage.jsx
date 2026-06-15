import "./DonorDashboard.css";
import { useNavigate } from "react-router-dom";

const campaigns = [
	{
		title: "Campaign name",
		description:
			"Campaign description: lorem ipsum is simply dummy text of the printing and typesetting industry.",
		raised: "Raised: $$",
		goal: "Goal: $$",
		progress: 64,
	},
	{
		title: "Campaign name",
		description:
			"Campaign description: lorem ipsum is simply dummy text of the printing and typesetting industry.",
		raised: "Raised: $$",
		goal: "Goal: $$",
		progress: 72,
	},
	{
		title: "Campaign name",
		description:
			"Campaign description: lorem ipsum is simply dummy text of the printing and typesetting industry.",
		raised: "Raised: $$",
		goal: "Goal: $$",
		progress: 48,
	},
	{
		title: "Campaign name",
		description:
			"Campaign description: lorem ipsum is simply dummy text of the printing and typesetting industry.",
		raised: "Raised: $$",
		goal: "Goal: $$",
		progress: 54,
	},
	{
		title: "Campaign name",
		description:
			"Campaign description: lorem ipsum is simply dummy text of the printing and typesetting industry.",
		raised: "Raised: $$",
		goal: "Goal: $$",
		progress: 83,
	},
	{
		title: "Campaign name",
		description:
			"Campaign description: lorem ipsum is simply dummy text of the printing and typesetting industry.",
		raised: "Raised: $$",
		goal: "Goal: $$",
		progress: 41,
	},
];

const DonorCampaignsPage = () => {
	const navigate = useNavigate();
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
				{campaigns.map((campaign) => (
					<article key={campaign.title + campaign.progress} className="donor-campaign-card donor-panel">
						<h3>{campaign.title}</h3>
						<p className="donor-small-text">{campaign.description}</p>
						<div className="donor-campaign-progress">
							<span>{campaign.raised}</span>
							<span>{campaign.goal}</span>
						</div>
						<div className="donor-progress-bar">
							<div className="donor-progress-fill" style={{ width: `${campaign.progress}%` }} />
						</div>
						<button className="donor-btn" type="button">Donate</button>
					</article>
				))}
			</section>
		</div>
	);
};

export default DonorCampaignsPage;