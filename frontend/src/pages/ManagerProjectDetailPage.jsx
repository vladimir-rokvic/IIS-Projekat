import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import "./Dashboard.css";

const STATUS_OPTIONS = [
    { value: "ODOBREN", label: "Accepted" },
    { value: "NEOPHODNA_IZMENA", label: "Necessary change" },
    { value: "ODBIJEN", label: "Denied" },
];

const statusLabel = {
    U_PRIPREMI: "In preparation",
    SPREMAN_ZA_ODOBRENJE: "Ready for acceptance",
    ODOBREN: "Accepted",
    NEOPHODNA_IZMENA: "Necessary change",
    ODBIJEN: "Denied",
};

const ManagerProjectDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [project, setProject] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Odluka menadžera
    const [selectedStatus, setSelectedStatus] = useState('');
    const [razlog, setRazlog] = useState('');
    const [submitting, setSubmitting] = useState(false);

    // Modali
    const [showSuccessModal, setShowSuccessModal] = useState(false);
    const [showCancelModal, setShowCancelModal] = useState(false);

    // Prati je li projekat editabilan za menadžera (samo SPREMAN_ZA_ODOBRENJE)
    const canDecide = project?.status === 'SPREMAN_ZA_ODOBRENJE';
    const needsReason = selectedStatus === 'NEOPHODNA_IZMENA' || selectedStatus === 'ODBIJEN';

    useEffect(() => {
        api.get(`/projekti/${id}`)
            .then(res => {
                setProject(res.data);
                setSelectedStatus(res.data.status === 'SPREMAN_ZA_ODOBRENJE' ? '' : res.data.status);
                setRazlog(res.data.razlog || '');
            })
            .catch(() => setError("Failed to load project."))
            .finally(() => setLoading(false));
    }, [id]);

    const handleSave = async () => {
        if (!selectedStatus) { setError('Please select a status.'); return; }
        if (needsReason && !razlog.trim()) { setError('Reason is required for this status.'); return; }

        setError('');
        setSubmitting(true);
        try {
            await api.put(`/projekti/${id}/odluka`, {
                status: selectedStatus,
                razlog: needsReason ? razlog : null,
            });
            setShowSuccessModal(true);
        } catch (e) {
            setError(e.response?.data?.message || 'Error saving decision.');
        } finally {
            setSubmitting(false);
        }
    };

    const handleSuccessOk = () => {
        setShowSuccessModal(false);
        navigate('/manager/projects');
    };

    const handleCancel = () => {
        setShowCancelModal(true);
    };

    if (loading) return <div className="loading-text">Loading project...</div>;
    if (!project) return <div className="loading-text">Project not found.</div>;

    return (
        <div className="create-page">
            {/* Modali */}
            {showSuccessModal && (
                <div className="modal-overlay">
                    <div className="modal-box">
                        <p>The project is<br />successfully updated!</p>
                        <button className="btn-primary" onClick={handleSuccessOk}>OK</button>
                    </div>
                </div>
            )}

            {showCancelModal && (
                <div className="modal-overlay">
                    <div className="modal-box">
                        <p>Are you sure you<br />want to discard<br />changes?</p>
                        <div style={{ display: 'flex', gap: 12, justifyContent: 'center', marginTop: 8 }}>
                            <button className="btn-cancel" onClick={() => setShowCancelModal(false)}>No</button>
                            <button className="btn-save" onClick={() => navigate('/manager/projects')}>Yes</button>
                        </div>
                    </div>
                </div>
            )}

            <button className="create-back-btn" onClick={() => navigate('/manager/projects')}>
                ← Back to Projects
            </button>

            <h1 className="create-title">Project information</h1>
            <p className="create-subtitle">See all the information about selected project</p>

            {/* Basic information */}
            <div className="form-section">
                <div className="form-section-header">
                    <h3>Basic information</h3>
                    <div>
                        <span style={{ fontSize: '0.88rem', fontWeight: 500 }}>Coordinator &nbsp;</span>
                        <span className="coordinator-display">
                            {project.koordinatorIme} {project.koordinatorPrezime}
                        </span>
                    </div>
                </div>

                {/* Sva polja su read-only za menadžera */}
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
                            <input type="text" value={project.rokPocetak} readOnly className="readonly-field" />
                            <input type="text" value={project.rokKraj} readOnly className="readonly-field" />
                        </div>
                    </div>

                    <div className="status-field">
                        <label>Status</label>
                        {canDecide ? (
                            // Menadžer može da bira status
                            <select
                                value={selectedStatus}
                                onChange={e => { setSelectedStatus(e.target.value); setRazlog(''); setError(''); }}
                                style={{ padding: '8px 12px', backgroundColor: '#e0e0e0', border: '1px solid #bbb', borderRadius: 6, fontSize: '0.88rem', minWidth: 160 }}
                            >
                                <option value="">Ready for acceptance</option>
                                {STATUS_OPTIONS.map(opt => (
                                    <option key={opt.value} value={opt.value}>{opt.label}</option>
                                ))}
                            </select>
                        ) : (
                            <div className="status-display">{statusLabel[project.status] || project.status}</div>
                        )}
                    </div>
                </div>

                {/* Razlog - pojavljuje se samo kad je odabran NEOPHODNA_IZMENA ili ODBIJEN */}
                {(needsReason || (project.razlog && !canDecide)) && (
                    <div className="form-field" style={{ marginTop: 14 }}>
                        <label>Reason</label>
                        {canDecide ? (
                            <textarea
                                placeholder="Describe the reason for the status Necessary change/Denied"
                                value={razlog}
                                onChange={e => setRazlog(e.target.value)}
                                rows={4}
                            />
                        ) : (
                            <textarea value={project.razlog || ''} readOnly rows={4} className="readonly-field" />
                        )}
                    </div>
                )}

                <div className="form-field" style={{ marginTop: 14 }}>
                    <label>Assistant coordinators</label>
                    <div className="status-display" style={{ minWidth: 'unset' }}>
                        {project.pomocniKoordinatoriIds?.length > 0
                            ? `${project.pomocniKoordinatoriIds.length} coordinator(s)`
                            : 'None'}
                    </div>
                </div>
            </div>

            {/* Additional information */}
            <div className="form-section">
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
                    <label>Project document upload</label>
                    {project.dokumentIme ? (
                        <a
                            href={`http://localhost:8080/api/projekti/${id}/dokument`}
                            target="_blank"
                            rel="noreferrer"
                            style={{ fontSize: '0.88rem', color: '#1a7a9a' }}
                        >
                            {project.dokumentIme}
                        </a>
                    ) : (
                        <span style={{ fontSize: '0.88rem', color: '#888' }}>No document</span>
                    )}
                </div>
            </div>

            {/* Actions */}
            {error && <p className="error-text" style={{ marginBottom: 8 }}>{error}</p>}
            {canDecide && (
                <div className="form-actions">
                    <button className="btn-cancel" onClick={handleCancel}>Cancel</button>
                    <button className="btn-save" onClick={handleSave} disabled={submitting}>
                        {submitting ? 'Saving...' : 'Save'}
                    </button>
                </div>
            )}
        </div>
    );
};

export default ManagerProjectDetailPage;
