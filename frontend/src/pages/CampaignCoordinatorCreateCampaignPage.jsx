import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./CampaignCoordinatorDashboard.css";

const CampaignCoordinatorCreateCampaignPage = () => {
	const navigate = useNavigate();
	const [name, setName] = useState("");
	const [description, setDescription] = useState("");
	const [goal, setGoal] = useState("");
	const [startDate, setStartDate] = useState("");
	const [endDate, setEndDate] = useState("");
	const [error, setError] = useState("");
	const [saving, setSaving] = useState(false);

	const handleCreate = async () => {
		setError("");

		if (!name || !description || !goal || !startDate || !endDate) {
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
			});
			navigate("/campaign-coordinator/all-campaigns");
		} catch (err) {
			console.log(err);
			setError("Failed to create campaign.");
		} finally {
			setSaving(false);
		}
	};

	return (
		<div className="campaign-content">
			<h1 className="campaign-page-title" style={{ marginBottom: 22 }}>Create a new campaign</h1>
			<section className="campaign-form-card">
				<div className="campaign-form-header">
					<button className="campaign-recommend-btn" type="button">Recommend</button>
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
					<div />
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
					<button className="campaign-form-secondary" type="button" onClick={() => navigate("/campaign-coordinator/all-campaigns")}>
						Cancel
					</button>
				</div>
			</section>
		</div>
	);
};

export default CampaignCoordinatorCreateCampaignPage;