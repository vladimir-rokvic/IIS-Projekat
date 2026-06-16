import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import "./Dashboard.css";

const FREQUENCY_OPTIONS = [
    { value: "", label: "Select frequency" },
    { value: "WEEKLY", label: "Weekly" },
    { value: "MONTHLY", label: "Monthly" },
    { value: "QUARTERLY", label: "Quarterly" },
];

const ProjectAcceptedPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [project, setProject] = useState(null);
    const [loading, setLoading] = useState(true);

    // KPI polja
    const [kpiDescription, setKpiDescription] = useState('');
    const [kpiFrequency, setKpiFrequency] = useState('');

    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [showSuccess, setShowSuccess] = useState(false);

    useEffect(() => {
        // Učitaj projekat i KPI paralelno
        Promise.all([
            api.get(`/projekti/${id}`),
            api.get(`/projekti/${id}/kpi`).catch(() => ({ data: null })),
        ]).then(([projektRes, kpiRes]) => {
            setProject(projektRes.data);
            if (kpiRes.data) {
                setKpiDescription(kpiRes.data.opis || '');
                setKpiFrequency(kpiRes.data.intervalMerenja || '');
            }
        }).catch(() => {})
          .finally(() => setLoading(false));
    }, [id]);

    const handleSave = async () => {
        setError('');
        if (!kpiDescription.trim()) { setError('KPI description is required.'); return; }
        if (!kpiFrequency) { setError('Please select measurement frequency.'); return; }

        setSubmitting(true);
        try {
            await api.put(`/projekti/${id}/kpi`, {
                opis: kpiDescription,
                intervalMerenja: kpiFrequency,
            });
            setShowSuccess(true);
        } catch (e) {
            setError(e.response?.data?.message || 'Error saving KPI.');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <div className="loading-text">Loading...</div>;
    if (!project) return <div className="loading-text">Project not found.</div>;

    return (
        <div className="create-page">
            {showSuccess && (
                <div className="modal-overlay">
                    <div className="modal-box">
                        <p>KPI successfully saved!</p>
                        <button className="btn-primary" onClick={() => setShowSuccess(false)}>OK</button>
                    </div>
                </div>
            )}

            <button className="create-back-btn" onClick={() => navigate('/projects')}>
                ← Back to Projects
            </button>

            <h1 className="create-title">Project details</h1>
            <p className="create-subtitle" style={{ color: '#155724', fontWeight: 500 }}>
                This project has been accepted.
            </p>

            {/* Basic information — sve disabled */}
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
                        <div className="status-display" style={{ color: '#155724', fontWeight: 600 }}>
                            Accepted
                        </div>
                    </div>
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

            {/* Additional information — disabled*/}
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
            </div>

            {/* KPI - editabilno */}
            <div className="form-section">
                <h3 style={{ marginBottom: 8 }}>KPI configuration</h3>
                <p style={{ fontSize: '0.82rem', color: '#555', marginBottom: 16 }}>
                    Configure the KPI for this project.
                </p>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>KPI description *</label>
                    <textarea
                        placeholder="Enter a short KPI description of the project"
                        value={kpiDescription}
                        onChange={e => setKpiDescription(e.target.value)}
                        rows={3}
                    />
                </div>

                <div className="form-field">
                    <label>Measurement Frequency *</label>
                    <select
                        value={kpiFrequency}
                        onChange={e => setKpiFrequency(e.target.value)}
                        style={{ padding: '8px 12px', backgroundColor: '#e0e0e0', border: '1px solid #bbb', borderRadius: 6, fontSize: '0.88rem' }}
                    >
                        {FREQUENCY_OPTIONS.map(opt => (
                            <option key={opt.value} value={opt.value}>{opt.label}</option>
                        ))}
                    </select>
                </div>
            </div>

            {error && <p className="error-text" style={{ marginBottom: 8 }}>{error}</p>}
            <div className="form-actions">
                <button className="btn-cancel" onClick={() => navigate('/projects')}>Cancel</button>
                <button className="btn-save" onClick={handleSave} disabled={submitting}>
                    {submitting ? 'Saving...' : 'Save'}
                </button>
            </div>
        </div>
    );
};

export default ProjectAcceptedPage;
