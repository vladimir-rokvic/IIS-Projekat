import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./CampaignCoordinatorDashboard.css";
import RecommendationsModal from "../components/RecommendationModal";

const CampaignCoordinatorCreateCampaignPage = () => {
	const navigate = useNavigate();
	const [name, setName] = useState("");
	const [description, setDescription] = useState("");
	const [goal, setGoal] = useState("");
	const [startDate, setStartDate] = useState("");
	const [endDate, setEndDate] = useState("");
	const [category, setCategory] = useState("");
	const [error, setError] = useState("");
	const [saving, setSaving] = useState(false);
	const [status, setStatus] = useState("form");

	const [showRecommendations, setShowRecommendations] = useState(false);
	const [recommendations, setRecommendations] = useState([]);

	const handleRecommend = async () => {
    	const res = await api.get("/campaigns/recommend");
    	setRecommendations(res.data);
    	setShowRecommendations(true);
	};

	const handleCreate = async () => {
		setError("");

		if (!name || !description || !goal || !startDate || !endDate || !category) {
			setError("All fields are required.");
			return;
		}

		try {
			setSaving(true);
			await api.post("/campaigns", {
				name,
				goal: Number(goal),
				startDate,
				endDate,
				description,
				status: "PLANNED",
				projectId: null,
				category: category
			});
			setStatus("success");
			setTimeout(() => {
				navigate("/campaign-coordinator/campaigns");
			}, 2000);
		} catch (err) {
			console.log(err);
			setError("Failed to create campaign.");
		} finally {
			setSaving(false);
		}
	};

	return (
		<div className="campaign-content">
			<div className="campaign-header" style={{ marginTop: 20 }}>
				<div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 20 }}>
					<h1 className="campaign-page-title" style={{ marginBottom: 34 }}>Create a new campaign</h1>
					<button className="btn-primary" onClick={() => navigate('/campaign-coordinator')}>
						Back to dashboard
					</button>
				</div>
			</div>
			<section className="campaign-form-card">
				<div className="campaign-form-header">
					<button className="campaign-recommend-btn" type="button" onClick={handleRecommend}>
						Recommend
					</button>
				</div>
				<div className="campaign-form-grid">
					<div className="campaign-form-field campaign-form-wide">
						<label>Campaign name</label>
						<input type="text" placeholder="Campaign name" value={name} onChange={(e) => setName(e.target.value)} />
					</div>
					<div className="campaign-form-field campaign-form-wide">
						<label>Description</label>
						<textarea placeholder="Campaign description" value={description} onChange={(e) => setDescription(e.target.value)} />
					</div>
					<div className="campaign-form-field">
						<label>Funding goal</label>
						<input type="number" placeholder="0" value={goal} onChange={(e) => setGoal(e.target.value)} />
					</div>
					<div className="campaign-form-field">
						<label>Category</label>
						<select value={category} onChange={(e) => setCategory(e.target.value)}>
							<option value="EDUCATION">Education</option>
							<option value="FOOD_AID">Food Aid</option>
							<option value="MEDICAL">Medical</option>
							<option value="SHELTER">Shelter</option>
							<option value="DISASTER_RELIEF">Disaster Relief</option>
							<option value="COMMUNITY_SUPPORT">Community Support</option>
						</select>
					</div>
					<div className="campaign-form-field">
						<label>Start date</label>
						<input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
					</div>
					<div className="campaign-form-field">
						<label>End date</label>
						<input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
					</div>
				</div>
				{error && <p className="donor-small-text" style={{ marginTop: 18, color: "#b42318" }}>{error}</p>}
				<div className="campaign-form-actions">
					<button className="campaign-form-primary" type="button" onClick={handleCreate} disabled={saving}>
						{saving ? "Creating..." : "Create campaign"}
					</button>
					<button className="campaign-form-secondary" type="button" onClick={() => navigate("/campaign-coordinator")}>
						Cancel
					</button>
				</div>
			</section>
			{status === "success" && (
				<div className="campaign-success-overlay">
					<div className="campaign-success-modal">
						<h2>Campaign Created</h2>
						<p>
							Campaign <strong>{name}</strong> has been successfully created!
						</p>
					</div>
				</div>
			)}

			{showRecommendations && (
				<RecommendationsModal
					recommendations={recommendations}
					onClose={() => setShowRecommendations(false)}
					onClick={(rec) => {
						setGoal(rec.recommendedGoal);
						setCategory(rec.recommendedCategory);
						setStartDate(new Date().toISOString().split("T")[0]);
						const endDate = new Date();
						endDate.setDate(endDate.getDate() + rec.recommendedDurationDays);
						setEndDate(endDate.toISOString().split("T")[0]);
						setShowRecommendations(false);
					}}
				/>
			)}
		</div>
	);
};

export default CampaignCoordinatorCreateCampaignPage;