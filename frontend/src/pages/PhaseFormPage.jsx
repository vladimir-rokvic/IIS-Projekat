import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import "./Dashboard.css";

/**
 * Koristi se i za kreiranje nove faze i za editovanje postojeće.
 * Ako postoji phaseId u URL-u → edit mod, inače → create mod.
 */
const PhaseFormPage = () => {
    const { id: projectId, phaseId } = useParams();
    const navigate = useNavigate();
    const isEdit = Boolean(phaseId);

    const [project, setProject] = useState(null);
    const [allSkillTypes, setAllSkillTypes] = useState([]);
    const [allCoordinators, setAllCoordinators] = useState([]);
    const [loading, setLoading] = useState(true);
    const [tasks, setTasks] = useState([]);
    const [zavrsena, setZavrsena] = useState(false);
    const [finishing, setFinishing] = useState(false);
    const [finishError, setFinishError] = useState("");

    // Form state
    const [naziv, setNaziv] = useState("");
    const [ciljevi, setCiljevi] = useState("");
    const [rokPocetak, setRokPocetak] = useState("");
    const [rokKraj, setRokKraj] = useState("");
    const [brojVolontera, setBrojVolontera] = useState("");
    const [selectedSkills, setSelectedSkills] = useState([]);           // List<Long> - ID-evi SkillType
    const [selectedCoordinators, setSelectedCoordinators] = useState([]); // List<Long> - ID-evi Employee
    const [redosled, setRedosled] = useState("");

    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");
    const [recommendations, setRecommendations] = useState(null); // { preporuceni, trazenBrojVolontera, poruka }

    useEffect(() => {
        Promise.all([
            api.get(`/projekti/${projectId}`),
            api.get(`/skill-types`),
            api.get(`/projekti/koordinatori`),
            isEdit ? api.get(`/projekti/${projectId}/faze`) : Promise.resolve({ data: [] }),
        ]).then(([projRes, skillRes, coordRes, fazeRes]) => {
            setProject(projRes.data);
            setAllSkillTypes(skillRes.data || []);
            setAllCoordinators(coordRes.data || []);

            if (isEdit) {
                const faza = (fazeRes.data || []).find(f => String(f.id) === String(phaseId));
                if (faza) {
                    setNaziv(faza.naziv || "");
                    setCiljevi(faza.ciljevi || "");
                    setRokPocetak(faza.rokPocetak || "");
                    setRokKraj(faza.rokKraj || "");
                    setBrojVolontera(faza.brojVolontera ?? "");
                    setRedosled(faza.redosled ?? "");
                    setSelectedSkills(faza.potrebneVestine?.map(v => v.id) || []);
                    setSelectedCoordinators(faza.pomocniKoordinatoriIds || []);
                    setTasks(faza.taskovi || []);
                    setZavrsena(faza.zavrsena || false);
                }

                // Učitaj preporuke volontera za ovu fazu
                api.get(`/faze/${phaseId}/preporuke-volontera`)
                    .then(recRes => setRecommendations(recRes.data))
                    .catch(() => setRecommendations(null));
            }
        }).catch(() => {})
            .finally(() => setLoading(false));
    }, [projectId, phaseId, isEdit]);

    const toggleSkill = (skillId) => {
        setSelectedSkills(prev =>
            prev.includes(skillId) ? prev.filter(s => s !== skillId) : [...prev, skillId]
        );
    };

    const toggleCoordinator = (coordId) => {
        setSelectedCoordinators(prev =>
            prev.includes(coordId) ? prev.filter(c => c !== coordId) : [...prev, coordId]
        );
    };

    const handleSave = async () => {
        setError("");
        if (!naziv.trim()) { setError("Phase name is required."); return; }
        if (!rokPocetak) { setError("Start date is required."); return; }
        if (!rokKraj) { setError("End date is required."); return; }
        if (!brojVolontera || Number(brojVolontera) < 1) { setError("Number of volunteers must be at least 1."); return; }

        setSubmitting(true);
        let savedPhaseId = null;
        try {
            if (isEdit) {
                // Ažuriraj samo ovu fazu — taskovi, zavrsena i ID ostaju netaknuti
                await api.put(`/faze/${phaseId}`, {
                    naziv,
                    ciljevi,
                    rokPocetak,
                    rokKraj,
                    brojVolontera: Number(brojVolontera),
                    potrebneVestineIds: selectedSkills,
                    redosled: Number(redosled) || undefined,
                });

                // Postavi pomoćne koordinatore za ovu fazu
                await api.put(`/faze/${phaseId}/pomocni-koordinatori`, {
                    pomocniKoordinatoriIds: selectedCoordinators,
                });

                savedPhaseId = Number(phaseId);
            } else {
                const response = await api.post(
                    `/projekti/${projectId}/nova-faza`,
                    {
                        faza: {
                            naziv,
                            ciljevi,
                            rokPocetak,
                            rokKraj,
                            brojVolontera: Number(brojVolontera),
                            potrebneVestineIds: selectedSkills,
                        }
                    }
                );

                savedPhaseId = response.data.id;

                await api.put(
                    `/faze/${savedPhaseId}/pomocni-koordinatori`,
                    {
                        pomocniKoordinatoriIds: selectedCoordinators,
                    }
                );
            }

            // Učitaj preporuke volontera za sačuvanu fazu na osnovu
            // potrebnih veština i dostupnosti u periodu trajanja faze
            if (savedPhaseId) {
                try {
                    const recRes = await api.get(`/faze/${savedPhaseId}/preporuke-volontera`);
                    setRecommendations(recRes.data);
                } catch {
                    setRecommendations(null);
                }

                if (!isEdit) {
                    navigate(`/projects/${projectId}/phases/${savedPhaseId}/edit`, { replace: true });
                }
                // U edit modu ID se nije promenio, nema potrebe za navigacijom
            } else {
                navigate(`/projects/${projectId}/info`);
            }
        } catch (e) {
            const data = e.response?.data;
            const msg = typeof data === "string"
                ? data
                : data?.message || data?.error || "Error saving phase.";
            setError(msg);
        } finally {
            setSubmitting(false);
        }
    };

    const handleFinishPhase = async () => {
        setFinishError("");
        setFinishing(true);
        try {
            await api.put(`/faze/${phaseId}/zavrsi`);
            setZavrsena(true);
        } catch (e) {
            const data = e.response?.data;
            const msg = typeof data === "string"
                ? data
                : data?.message || data?.error || "Error marking phase as finished.";
            setFinishError(msg);
        } finally {
            setFinishing(false);
        }
    };

    // Samo pomoćni koordinatori projekta mogu biti dodeljeni kao koordinatori faze
    const projectAssistantCoordinatorIds = project?.pomocniKoordinatoriIds || [];
    const availableCoordinators = allCoordinators.filter(c =>
        projectAssistantCoordinatorIds.includes(c.id)
    );

    if (loading) return <div className="loading-text">Loading...</div>;

    return (
        <div className="create-page">
            <button className="create-back-btn" onClick={() => navigate(`/projects/${projectId}/info`)}>
                ← Back to Project
            </button>

            <h1 className="create-title">{isEdit ? "Edit phase" : "Create a new phase"}</h1>
            <p className="create-subtitle">
                {isEdit
                    ? "Edit the phase information below."
                    : "Fill in the required information to create a new phase."}
            </p>

            <div className="form-section">
                <h3 style={{ marginBottom: 16 }}>Basic information</h3>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Phase name *</label>
                    <input
                        type="text"
                        placeholder="Enter the name of the phase"
                        value={naziv}
                        onChange={e => setNaziv(e.target.value)}
                    />
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Goals *</label>
                    <textarea
                        placeholder="Enter the goals of this phase"
                        value={ciljevi}
                        onChange={e => setCiljevi(e.target.value)}
                        rows={3}
                    />
                </div>

                <div className="form-row" style={{ marginBottom: 14 }}>
                    <div className="form-field">
                        <label>Deadlines *</label>
                        <div style={{ display: "flex", gap: 10 }}>
                            <input
                                type="date"
                                placeholder="Start date"
                                value={rokPocetak}
                                onChange={e => setRokPocetak(e.target.value)}
                                style={{ flex: 1 }}
                            />
                            <input
                                type="date"
                                placeholder="End date"
                                value={rokKraj}
                                onChange={e => setRokKraj(e.target.value)}
                                style={{ flex: 1 }}
                            />
                        </div>
                    </div>
                    <div className="form-field">
                        <label>Volunteers needed *</label>
                        <input
                            type="number"
                            min="1"
                            placeholder="Number of volunteers"
                            value={brojVolontera}
                            onChange={e => setBrojVolontera(e.target.value)}
                        />
                    </div>
                </div>

                {/* Skills */}
                {allSkillTypes.length > 0 && (
                    <div className="form-field" style={{ marginBottom: 14 }}>
                        <label>Required skills</label>
                        <div style={{ display: "flex", flexWrap: "wrap", gap: 8, marginTop: 4 }}>
                            {allSkillTypes.map(skill => (
                                <label
                                    key={skill.id}
                                    style={{
                                        display: "flex",
                                        alignItems: "center",
                                        gap: 5,
                                        padding: "4px 10px",
                                        borderRadius: 6,
                                        border: "1px solid #bbb",
                                        backgroundColor: selectedSkills.includes(skill.id) ? "#369FBC" : "#e0e0e0",
                                        color: selectedSkills.includes(skill.id) ? "#000" : "#333",
                                        cursor: "pointer",
                                        fontSize: "0.85rem",
                                        fontWeight: selectedSkills.includes(skill.id) ? 600 : 400,
                                        transition: "background-color 0.15s",
                                    }}
                                >
                                    <input
                                        type="checkbox"
                                        style={{ display: "none" }}
                                        checked={selectedSkills.includes(skill.id)}
                                        onChange={() => toggleSkill(skill.id)}
                                    />
                                    {skill.name}
                                </label>
                            ))}
                        </div>
                    </div>
                )}

                {/* Recommended volunteers */}
                {isEdit && recommendations && (
                    <div className="form-field" style={{ marginBottom: 14 }}>
                        <label>Recommended volunteers</label>

                        {recommendations.poruka && (
                            <p className="error-text" style={{ marginTop: 6, marginBottom: 6 }}>
                                {recommendations.poruka}
                            </p>
                        )}

                        {(!recommendations.preporuceni || recommendations.preporuceni.length === 0) ? (
                            <p style={{ fontSize: "0.88rem", color: "#777", marginTop: 6 }}>
                                No available volunteers match the required skills for this phase.
                            </p>
                        ) : (
                            <ol style={{ marginTop: 6, paddingLeft: 22 }}>
                                {recommendations.preporuceni.map(vol => (
                                    <li
                                        key={vol.id}
                                        style={{
                                            marginBottom: 4,
                                            fontSize: "0.9rem",
                                            ...(vol.pinned ? {
                                                backgroundColor: "#d4edda",
                                                borderRadius: 4,
                                                padding: "2px 6px",
                                                listStylePosition: "inside",
                                            } : {}),
                                        }}
                                    >
                                        {vol.name} {vol.surname}
                                        {" "}
                                        <span style={{ color: "#777" }}>
                                            ({vol.matchedSkillNames?.join(", ") || "no skill match"} — {vol.matchedSkillsCount}/{vol.totalRequiredSkills})
                                        </span>
                                        {vol.pinned && (
                                            <span style={{
                                                marginLeft: 8,
                                                fontSize: "0.78rem",
                                                fontWeight: 600,
                                                color: "#155724",
                                            }}>
                                                Assigned to a task in this phase
                                            </span>
                                        )}
                                    </li>
                                ))}
                            </ol>
                        )}
                    </div>
                )}

                {/* Phase coordinator */}
                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Phase coordinator(s)</label>
                    {availableCoordinators.length === 0 ? (
                        <p style={{ fontSize: "0.85rem", color: "#777" }}>
                            No assistant coordinators have been added to this project yet.
                        </p>
                    ) : (
                        <select
                            value=""
                            onChange={e => {
                                const val = Number(e.target.value);
                                if (val && !selectedCoordinators.includes(val)) {
                                    setSelectedCoordinators(prev => [...prev, val]);
                                }
                            }}
                            style={{ padding: "8px 12px", backgroundColor: "#e0e0e0", border: "1px solid #bbb", borderRadius: 6, fontSize: "0.88rem" }}
                        >
                            <option value="">Select coordinator</option>
                            {availableCoordinators.map(coord => (
                                <option key={coord.id} value={coord.id}>
                                    {coord.ime} {coord.prezime}
                                </option>
                            ))}
                        </select>
                    )}

                    {/* Izabrani koordinatori */}
                    {selectedCoordinators.length > 0 && (
                        <div style={{ display: "flex", flexWrap: "wrap", gap: 6, marginTop: 8 }}>
                            {selectedCoordinators.map(coordId => {
                                const coord = allCoordinators.find(c => c.id === coordId);
                                return coord ? (
                                    <span
                                        key={coordId}
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
                                        {coord.ime} {coord.prezime}
                                        <button
                                            onClick={() => setSelectedCoordinators(prev => prev.filter(c => c !== coordId))}
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

            {isEdit && (
                <div className="form-section">
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
                        <h3 style={{ margin: 0 }}>Tasks</h3>
                        <div style={{ display: "flex", gap: 8 }}>
                            {zavrsena ? (
                                <span className="status-badge" style={{ backgroundColor: "#d4edda", color: "#155724" }}>
                                    Phase finished
                                </span>
                            ) : (
                                <button
                                    className="btn-primary"
                                    style={{ marginTop: 0 }}
                                    onClick={handleFinishPhase}
                                    disabled={finishing}
                                >
                                    {finishing ? "Saving..." : "Mark phase as finished"}
                                </button>
                            )}
                            <button
                                className="btn-primary"
                                style={{ marginTop: 0 }}
                                onClick={() => navigate(`/projects/${projectId}/phases/${phaseId}/tasks/new`)}
                            >
                                Add task +
                            </button>
                        </div>
                    </div>

                    {finishError && <p className="error-text" style={{ marginBottom: 8 }}>{finishError}</p>}

                    {tasks.length === 0 ? (
                        <p style={{ fontSize: "0.88rem", color: "#777" }}>No tasks added yet.</p>
                    ) : (
                        tasks.map((task, index) => (
                            <div key={task.id} className="phase-card">
                                <div style={{ flex: 1 }}>
                                    <div className="info-row">
                                        <span className="info-label" style={{ fontWeight: 700 }}>Task {index + 1}:</span>
                                        <span>{task.name}</span>
                                    </div>
                                    <div className="info-row"><span className="info-label">Description</span><span>{task.description || "—"}</span></div>
                                    <div className="info-row"><span className="info-label">Start date</span><span>{task.startDate}</span></div>
                                    <div className="info-row"><span className="info-label">End date</span><span>{task.endDate}</span></div>
                                    {task.volunteer && (
                                        <div className="info-row">
                                            <span className="info-label">Volunteer</span>
                                            <span>{task.volunteer.name} {task.volunteer.surname}</span>
                                        </div>
                                    )}
                                </div>
                                <button
                                    className="btn-primary"
                                    style={{ marginTop: 0, alignSelf: "flex-start", whiteSpace: "nowrap" }}
                                    onClick={() => navigate(`/projects/${projectId}/phases/${phaseId}/tasks/${task.id}/edit`)}
                                >
                                    Edit
                                </button>
                            </div>
                        ))
                    )}
                </div>
            )}

            {error && <p className="error-text" style={{ marginBottom: 8 }}>{error}</p>}

            <div className="form-actions">
                <button className="btn-cancel" onClick={() => navigate(`/projects/${projectId}/info`)}>
                    Cancel
                </button>
                <button className="btn-save" onClick={handleSave} disabled={submitting}>
                    {submitting ? "Saving..." : "Save"}
                </button>
            </div>
        </div>
    );
};

export default PhaseFormPage;
