import { useEffect, useState } from "react";
import api from "../api/axios";

const toDateInputValue = (dateValue) => {
	if (!dateValue) return "";
	return String(dateValue).slice(0, 10);
};

const buildInitialState = (campaign) => ({
	name: campaign?.name ?? "",
	goal: campaign?.goal ?? "",
	startDate: toDateInputValue(campaign?.startDate),
	endDate: toDateInputValue(campaign?.endDate),
	description: campaign?.description ?? "",
	status: campaign?.status ?? "PLANNED",
	projectId: campaign?.projectId ? String(campaign.projectId) : "",
});

const CampaignEditModal = ({ campaign, onClose, onSaved }) => {
	const [projects, setProjects] = useState([]);
	const [projectsLoading, setProjectsLoading] = useState(true);
	const [formState, setFormState] = useState(() => buildInitialState(campaign));
	const [modalError, setModalError] = useState("");
	const [saving, setSaving] = useState(false);

	useEffect(() => {
		setFormState(buildInitialState(campaign));
		setModalError("");
	}, [campaign]);

	useEffect(() => {
		const fetchProjects = async () => {
			try {
				const res = await api.get("/projekti/sve");
				setProjects(res.data);
			} catch (err) {
				console.log(err);
				setProjects([]);
			} finally {
				setProjectsLoading(false);
			}
		};

		fetchProjects();
	}, []);

	if (!campaign) {
		return null;
	}

	const handleFieldChange = (field, value) => {
		setFormState((current) => ({ ...current, [field]: value }));
	};

	const handleClose = () => {
		if (!saving) {
			onClose();
		}
	};

	const handleSave = async () => {
		setModalError("");

		if (!formState.name || !formState.goal || !formState.startDate || !formState.endDate || !formState.description || !formState.status) {
			setModalError("All standard campaign fields are required.");
			return;
		}

		try {
			setSaving(true);
			const payload = {
				name: formState.name,
				goal: Number(formState.goal),
				startDate: formState.startDate,
				endDate: formState.endDate,
				description: formState.description,
				status: formState.status,
				projectId: formState.projectId ? Number(formState.projectId) : null,
			};

			const res = await api.put(`/campaigns/${campaign.id}`, payload);
			onSaved(res.data);
			onClose();
		} catch (err) {
			console.log(err);
			setModalError("Failed to update campaign.");
		} finally {
			setSaving(false);
		}
	};

	return (
		<div className="campaign-modal-overlay" role="presentation" onClick={handleClose}>
			<div className="campaign-modal" role="dialog" aria-modal="true" aria-labelledby="campaign-modal-title" onClick={(event) => event.stopPropagation()}>
				<section className="campaign-form-card campaign-modal-card">
					<div className="campaign-modal-header">
						<div className="campaign-modal-header-content">
							<h2 id="campaign-modal-title" className="campaign-form-title" style={{ marginBottom: 8 }}>Edit campaign</h2>
							<p className="campaign-modal-subtitle">Review the campaign details and adjust the project link if needed.</p>
						</div>
					</div>
					<div className="campaign-form-grid">
						<div className="campaign-form-field campaign-form-wide">
							<label>Campaign name</label>
							<input type="text" placeholder="Campaign name" value={formState.name} onChange={(event) => handleFieldChange("name", event.target.value)} />
						</div>
						<div className="campaign-form-field campaign-form-wide">
							<label>Description</label>
							<textarea placeholder="Campaign description" value={formState.description} onChange={(event) => handleFieldChange("description", event.target.value)} />
						</div>
						<div className="campaign-form-field">
							<label>Funding goal</label>
							<input type="number" placeholder="0" value={formState.goal} onChange={(event) => handleFieldChange("goal", event.target.value)} />
						</div>
						<div className="campaign-form-field">
							<label>Status</label>
							<select value={formState.status} onChange={(event) => handleFieldChange("status", event.target.value)}>
								<option value="PLANNED">PLANNED</option>
								<option value="ACTIVE">ACTIVE</option>
								<option value="FINISHED">FINISHED</option>
							</select>
						</div>
						<div className="campaign-form-field">
							<label>Start date</label>
							<input type="date" value={formState.startDate} onChange={(event) => handleFieldChange("startDate", event.target.value)} />
						</div>
						<div className="campaign-form-field">
							<label>End date</label>
							<input type="date" value={formState.endDate} onChange={(event) => handleFieldChange("endDate", event.target.value)} />
						</div>
						<div className="campaign-form-field campaign-form-wide">
							<label>Linked project</label>
							<select value={formState.projectId} onChange={(event) => handleFieldChange("projectId", event.target.value)}>
								<option value="">No project linked</option>
								{projectsLoading ? (
									<option value="">Loading projects...</option>
								) : (
									projects.map((project) => (
										<option key={project.id} value={project.id}>
											{project.naziv ?? `Project ${project.id}`}
										</option>
									))
								)}
							</select>
						</div>
					</div>
					{modalError && <p className="donor-small-text" style={{ marginTop: 18, color: "#b42318" }}>{modalError}</p>}
					<div className="campaign-form-actions">
						<button className="campaign-form-primary" type="button" onClick={handleSave} disabled={saving}>
							{saving ? "Saving..." : "Save changes"}
						</button>
						<button className="campaign-form-secondary" type="button" onClick={handleClose} disabled={saving}>
							Cancel
						</button>
					</div>
				</section>
			</div>
		</div>
	);
};

export default CampaignEditModal;