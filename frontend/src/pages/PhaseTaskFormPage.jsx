import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

/**
 * Kreiranje / editovanje taska unutar faze projekta.
 * Ako postoji taskId u URL-u → edit mod, inače → create mod.
 */
const PhaseTaskFormPage = () => {
    const { id: projectId, phaseId, taskId } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();
    const isEdit = Boolean(taskId);

    const [allVolunteers, setAllVolunteers] = useState([]);
    const [loading, setLoading] = useState(true);

    // Form state
    const [naziv, setNaziv] = useState("");
    const [opis, setOpis] = useState("");
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [selectedVolunteers, setSelectedVolunteers] = useState([]);

    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        Promise.all([
            api.get(`/volunteer`),
            isEdit ? api.get(`/tasks/${taskId}`) : Promise.resolve({ data: null }),
        ]).then(([volRes, taskRes]) => {
            setAllVolunteers(volRes.data || []);

            if (isEdit && taskRes.data) {
                const t = taskRes.data;
                setNaziv(t.name || "");
                setOpis(t.description || "");
                setStartDate(t.startDate || "");
                setEndDate(t.endDate || "");
                setSelectedVolunteers(t.volunteer ? [t.volunteer.id] : []);
            }
        }).catch(() => {})
          .finally(() => setLoading(false));
    }, [taskId, isEdit]);

    const toggleVolunteer = (volunteerId) => {
        // Backend trenutno podržava samo jednog volontera po tasku
        setSelectedVolunteers(prev =>
            prev.includes(volunteerId) ? [] : [volunteerId]
        );
    };

    const handleSave = async () => {
        setError("");
        if (!naziv.trim()) { setError("Task name is required."); return; }
        if (!opis.trim()) { setError("Description is required."); return; }
        if (!startDate) { setError("Start date is required."); return; }
        if (!endDate) { setError("End date is required."); return; }
        if (selectedVolunteers.length === 0) { setError("At least one volunteer is required."); return; }

        setSubmitting(true);
        try {
            if (isEdit) {
                await api.put(`/tasks/${taskId}`, {
                    name: naziv,
                    description: opis,
                    startDate,
                    endDate,
                    volunteerId: selectedVolunteers[0] ?? null,
                    requiredSkills: [],
                });
            } else {
                await api.post(`/tasks`, {
                    name: naziv,
                    description: opis,
                    startDate,
                    endDate,
                    volunteerId: selectedVolunteers[0] ?? null,
                    coordinatorId: user?.id,
                    phaseId: Number(phaseId),
                    requiredSkills: [],
                });
            }

            navigate(`/projects/${projectId}/phases/${phaseId}/edit`);
        } catch (e) {
            const data = e.response?.data;
            const msg = typeof data === "string"
                ? data
                : data?.message || data?.error || "Error saving task.";
            setError(msg);
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <div className="loading-text">Loading...</div>;

    return (
        <div className="create-page">
            <button
                className="create-back-btn"
                onClick={() => navigate(`/projects/${projectId}/phases/${phaseId}/edit`)}
            >
                ← Back to Phase
            </button>

            <h1 className="create-title">{isEdit ? "Edit task" : "Create a new task"}</h1>
            <p className="create-subtitle">
                {isEdit
                    ? "Edit the task information below."
                    : "Fill in the required information to create a new task."}
            </p>

            <div className="form-section">
                <h3 style={{ marginBottom: 16 }}>Basic information</h3>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Task name *</label>
                    <input
                        type="text"
                        placeholder="Enter the name of the task"
                        value={naziv}
                        onChange={e => setNaziv(e.target.value)}
                    />
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Description *</label>
                    <textarea
                        placeholder="Enter a short description of the task"
                        value={opis}
                        onChange={e => setOpis(e.target.value)}
                        rows={3}
                    />
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Deadlines *</label>
                    <div style={{ display: "flex", gap: 10 }}>
                        <input
                            type="date"
                            placeholder="Start date"
                            value={startDate}
                            onChange={e => setStartDate(e.target.value)}
                            style={{ flex: 1 }}
                        />
                        <input
                            type="date"
                            placeholder="End date"
                            value={endDate}
                            onChange={e => setEndDate(e.target.value)}
                            style={{ flex: 1 }}
                        />
                    </div>
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Volunteers *</label>
                    {allVolunteers.length === 0 ? (
                        <p style={{ fontSize: "0.85rem", color: "#777" }}>No volunteers available.</p>
                    ) : (
                        <select
                            value=""
                            onChange={e => {
                                const val = Number(e.target.value);
                                if (val) toggleVolunteer(val);
                            }}
                            style={{ padding: "8px 12px", backgroundColor: "#e0e0e0", border: "1px solid #bbb", borderRadius: 6, fontSize: "0.88rem" }}
                        >
                            <option value="">Select volunteers</option>
                            {allVolunteers.map(v => (
                                <option key={v.id} value={v.id}>
                                    {v.name} {v.surname}
                                </option>
                            ))}
                        </select>
                    )}

                    {selectedVolunteers.length > 0 && (
                        <div style={{ display: "flex", flexWrap: "wrap", gap: 6, marginTop: 8 }}>
                            {selectedVolunteers.map(volId => {
                                const vol = allVolunteers.find(v => v.id === volId);
                                return vol ? (
                                    <span
                                        key={volId}
                                        style={{
                                            display: "inline-flex",
                                            alignItems: "center",
                                            gap: 5,
                                            padding: "3px 10px",
                                            backgroundColor: "#d4edda",
                                            border: "1px solid #aaddbb",
                                            borderRadius: 6,
                                            fontSize: "0.82rem",
                                        }}
                                    >
                                        {vol.name} {vol.surname}
                                        <button
                                            onClick={() => setSelectedVolunteers(prev => prev.filter(v => v !== volId))}
                                            style={{ background: "none", border: "none", cursor: "pointer", fontWeight: 700, fontSize: "0.85rem", color: "#555", padding: 0, lineHeight: 1 }}
                                        >
                                            ×
                                        </button>
                                    </span>
                                ) : null;
                            })}
                        </div>
                    )}
                </div>
            </div>

            {error && <p className="error-text" style={{ marginBottom: 8 }}>{error}</p>}

            <div className="form-actions">
                <button className="btn-cancel" onClick={() => navigate(`/projects/${projectId}/phases/${phaseId}/edit`)}>
                    Cancel
                </button>
                <button className="btn-save" onClick={handleSave} disabled={submitting}>
                    {submitting ? "Saving..." : "Save"}
                </button>
            </div>
        </div>
    );
};

export default PhaseTaskFormPage;
