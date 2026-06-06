import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import "./Dashboard.css";

const ProjectInformationPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [project, setProject] = useState(null);
    const [phases, setPhases] = useState([]);
    const [kpi, setKpi] = useState(null);
    const [loading, setLoading] = useState(true);

    const fetchData = () => {
        Promise.all([
            api.get(`/projekti/${id}`),
            api.get(`/projekti/${id}/faze`),
            api.get(`/projekti/${id}/kpi`).catch(() => ({ data: null })),
        ]).then(([projectRes, fazePRes, kpiRes]) => {
            setProject(projectRes.data);
            setPhases(fazePRes.data || []);
            setKpi(kpiRes.data);
        }).catch(() => {})
          .finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchData();
    }, [id]);

    if (loading) return <div className="loading-text">Loading...</div>;
    if (!project) return <div className="loading-text">Project not found.</div>;

    return (
        <div className="create-page">
            <button className="create-back-btn" onClick={() => navigate("/projects")}>
                ← Back to Projects
            </button>

            <h1 className="create-title">Project information</h1>
            <p className="create-subtitle">Here you can access all the information about the project</p>

            {/* Basic information */}
            <div className="form-section">
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                    <div>
                        <h3 style={{ marginBottom: 12 }}>Basic information</h3>
                        <div className="info-row"><span className="info-label">Project name</span><span>{project.naziv}</span></div>
                        <div className="info-row"><span className="info-label">Status</span>
                            <span className={`status-badge status-${project.status}`}>
                                {project.status === "ODOBREN" ? "Accepted" : project.status}
                            </span>
                        </div>
                        <div className="info-row"><span className="info-label">Start date</span><span>{project.rokPocetak}</span></div>
                        <div className="info-row"><span className="info-label">End date</span><span>{project.rokKraj}</span></div>
                    </div>
                    <button
                        className="btn-primary"
                        style={{ marginTop: 0, alignSelf: "flex-start" }}
                        onClick={() => navigate(`/projects/${id}/accepted`)}
                    >
                        Details
                    </button>
                </div>
            </div>

            {/* Project phases */}
            <div className="form-section">
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
                    <h3>Project phases</h3>
                    <button
                        className="btn-primary"
                        style={{ marginTop: 0 }}
                        onClick={() => navigate(`/projects/${id}/phases/new`)}
                    >
                        Add phase +
                    </button>
                </div>

                {/* Overlap setting */}
                <div style={{ display: "flex", alignItems: "center", gap: 16, marginBottom: 14, fontSize: "0.88rem" }}>
                    <span>Can the phases overlap?</span>
                    <label style={{ display: "flex", alignItems: "center", gap: 4, cursor: "default" }}>
                        <input type="radio" readOnly checked={project.fazeMoguDaSePreklapaju === true} onChange={() => {}} /> Yes
                    </label>
                    <label style={{ display: "flex", alignItems: "center", gap: 4, cursor: "default" }}>
                        <input type="radio" readOnly checked={project.fazeMoguDaSePreklapaju === false} onChange={() => {}} /> No
                    </label>
                </div>

                {phases.length === 0 && (
                    <p style={{ fontSize: "0.88rem", color: "#777" }}>No phases defined yet.</p>
                )}

                {phases.map((phase, index) => (
                    <div key={phase.id} className="phase-card">
                        <div style={{ flex: 1 }}>
                            <div className="info-row">
                                <span className="info-label" style={{ fontWeight: 700 }}>Phase {index + 1}:</span>
                                <span>{phase.naziv}</span>
                            </div>
                            <div className="info-row"><span className="info-label">Goals</span><span>{phase.ciljevi || "—"}</span></div>
                            <div className="info-row"><span className="info-label">Start date</span><span>{phase.rokPocetak}</span></div>
                            <div className="info-row"><span className="info-label">End date</span><span>{phase.rokKraj}</span></div>
                            <div className="info-row">
                                <span className="info-label">Volunteers needed</span>
                                <span>{phase.brojVolontera}</span>
                            </div>
                            {phase.potrebneVestine?.length > 0 && (
                                <div className="info-row">
                                    <span className="info-label">Skills</span>
                                    <span>{phase.potrebneVestine.map(v => v.name).join(", ")}</span>
                                </div>
                            )}
                            {phase.pomocniKoordinatoriImena?.length > 0 && (
                                <div className="info-row">
                                    <span className="info-label">Coordinators</span>
                                    <span>{phase.pomocniKoordinatoriImena.join(", ")}</span>
                                </div>
                            )}
                        </div>
                        <button
                            className="btn-primary"
                            style={{ marginTop: 0, alignSelf: "flex-start", whiteSpace: "nowrap" }}
                            onClick={() => navigate(`/projects/${id}/phases/${phase.id}/edit`)}
                        >
                            Edit
                        </button>
                    </div>
                ))}
            </div>

            {/* Project KPI */}
            <div className="form-section">
                <h3 style={{ marginBottom: 14 }}>Project KPI</h3>
                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>KPI description</label>
                    <textarea
                        value={kpi?.opis || ""}
                        readOnly
                        rows={3}
                        className="readonly-field"
                        placeholder="Short KPI description of the project"
                    />
                </div>
                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Measurement Frequency</label>
                    <input
                        type="text"
                        value={kpi?.intervalMerenja || ""}
                        readOnly
                        className="readonly-field"
                        placeholder="Frequency"
                    />
                </div>
                <div className="form-field">
                    <label>Current KPI</label>
                    <input
                        type="text"
                        value={kpi?.currentKpi || ""}
                        readOnly
                        className="readonly-field"
                        placeholder="KPI"
                    />
                </div>
            </div>
        </div>
    );
};

export default ProjectInformationPage;
