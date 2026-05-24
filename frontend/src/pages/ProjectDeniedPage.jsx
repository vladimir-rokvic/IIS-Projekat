import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import "./Dashboard.css";

const ProjectDeniedPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [project, setProject] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api.get(`/projekti/${id}`)
            .then(res => setProject(res.data))
            .catch(() => {})
            .finally(() => setLoading(false));
    }, [id]);

    if (loading) return <div className="loading-text">Loading...</div>;
    if (!project) return <div className="loading-text">Project not found.</div>;

    return (
        <div className="create-page">
            <button className="create-back-btn" onClick={() => navigate('/projects')}>
                ← Back to Projects
            </button>

            <h1 className="create-title">Project details</h1>
            <p className="create-subtitle" style={{ color: '#c0392b', fontWeight: 500 }}>
                This project has been denied.
            </p>

            {/* ── Basic information ─────────────────────── */}
            <div className="form-section disabled">
                <div className="form-section-header">
                    <h3>Basic information</h3>
                    <div>
                        <span style={{ fontSize: '0.88rem', fontWeight: 500 }}>Coordinator &nbsp;</span>
                        <span className="coordinator-display">
                            {project.koordinatorIme} {project.koordinatorPrezime}
                        </span>
                    </div>
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Project name</label>
                    <input type="text" value={project.naziv} readOnly className="readonly-field" />
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Description</label>
                    <textarea value={project.opis} readOnly rows={3} className="readonly-field" />
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Project goals</label>
                    <textarea value={project.ciljevi} readOnly rows={3} className="readonly-field" />
                </div>

                <div className="form-row">
                    <div className="form-field">
                        <label>Deadlines</label>
                        <div style={{ display: 'flex', gap: 10 }}>
                            <input type="text" value={project.rokPocetak || ''} readOnly className="readonly-field" />
                            <input type="text" value={project.rokKraj || ''} readOnly className="readonly-field" />
                        </div>
                    </div>
                    <div className="status-field">
                        <label>Status</label>
                        <div className="status-display" style={{ color: '#721c24', fontWeight: 600 }}>
                            Denied
                        </div>
                    </div>
                </div>

                {/* Razlog odbijanja — uvijek prikazan */}
                <div className="form-field" style={{ marginTop: 14 }}>
                    <label style={{ color: '#c0392b' }}>Reason for denial</label>
                    <textarea
                        value={project.razlog || ''}
                        readOnly
                        rows={4}
                        className="readonly-field"
                        style={{ borderColor: '#f5c6cb' }}
                    />
                </div>

                <div className="form-field" style={{ marginTop: 14 }}>
                    <label>Assistant coordinators</label>
                    <div className="status-display">
                        {project.pomocniKoordinatoriIds?.length > 0
                            ? `${project.pomocniKoordinatoriIds.length} coordinator(s)`
                            : 'None'}
                    </div>
                </div>
            </div>

            {/* ── Additional information ────────────────── */}
            <div className="form-section disabled">
                <h3 style={{ marginBottom: 16 }}>Additional information</h3>
                <div className="form-row">
                    <div className="form-field">
                        <label>Target group</label>
                        <input type="text" value={project.ciljnaGrupa || ''} readOnly className="readonly-field" placeholder="Not specified" />
                    </div>
                    <div className="form-field">
                        <label>Geographic location</label>
                        <input type="text" value={project.geografskaLokacija || ''} readOnly className="readonly-field" placeholder="Not specified" />
                    </div>
                </div>
                <div className="form-field" style={{ marginTop: 14 }}>
                    <label>Project document</label>
                    {project.dokumentIme
                        ? <a href={`http://localhost:8080/api/projekti/${id}/dokument`} target="_blank" rel="noreferrer" style={{ fontSize: '0.88rem', color: '#1a7a9a' }}>{project.dokumentIme}</a>
                        : <span style={{ fontSize: '0.88rem', color: '#888' }}>No document</span>
                    }
                </div>
            </div>

            {/* ── KPI — disabled ────────────────────────── */}
            <div className="form-section disabled">
                <h3 style={{ marginBottom: 8 }}>KPI configuration</h3>
                <p style={{ fontSize: '0.82rem', color: '#666', marginBottom: 16 }}>
                    KPI can be configured after the project is approved.
                </p>
                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>KPI description</label>
                    <textarea disabled rows={3} placeholder="Not available" />
                </div>
                <div className="form-field">
                    <label>Measurement Frequency</label>
                    <select disabled><option>Not available</option></select>
                </div>
            </div>
        </div>
    );
};

export default ProjectDeniedPage;
