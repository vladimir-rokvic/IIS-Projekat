import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import "./Dashboard.css";

const REQUEST_STATUS_LABEL = {
    NA_CEKANJU: "Pending",
    ODOBREN: "Approved",
    DELIMICNO_ODOBREN: "Partially approved",
    ODBIJEN: "Rejected",
};

const REQUEST_STATUS_COLOR = {
    NA_CEKANJU: "#856404",
    ODOBREN: "#155724",
    DELIMICNO_ODOBREN: "#0c5460",
    ODBIJEN: "#721c24",
};

const ManagerFundingRequestDetailPage = () => {
    const { projectId } = useParams();
    const navigate = useNavigate();

    const [project, setProject] = useState(null);
    const [requests, setRequests] = useState([]);
    const [orgFunds, setOrgFunds] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Koji zahtev je trenutno otvoren za odluku
    const [activeRequestId, setActiveRequestId] = useState(null);
    const [decisionStatus, setDecisionStatus] = useState('');
    const [partialAmount, setPartialAmount] = useState('');
    const [decisionReason, setDecisionReason] = useState('');
    const [decisionError, setDecisionError] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);

    const loadData = () => {
        return Promise.all([
            api.get(`/projekti/${projectId}`),
            api.get(`/zahtevi-za-sredstvima/projekat/${projectId}`),
            // Uzmi stanje opštih sredstava iz prvog NA_CEKANJU zahteva (server ga šalje)
            api.get(`/zahtevi-za-sredstvima/na-cekanju`),
        ]).then(([projRes, reqRes, pendingRes]) => {
            setProject(projRes.data);
            setRequests(reqRes.data);
            // Izvuci orgFunds iz odgovora menadžerskog endpointa (samo za pending zahteve)
            const forThisProject = pendingRes.data.find(r => r.projectId === parseInt(projectId));
            if (forThisProject) {
                setOrgFunds(forThisProject.trenutnoOpstiIznos);
            }
        });
    };

    useEffect(() => {
        loadData()
            .catch(() => setError("Failed to load data."))
            .finally(() => setLoading(false));
    }, [projectId]);

    const handleOpenDecision = (req) => {
        setActiveRequestId(req.id);
        setDecisionStatus('');
        setPartialAmount('');
        setDecisionReason('');
        setDecisionError('');
    };

    const handleCloseDecision = () => {
        setActiveRequestId(null);
        setDecisionStatus('');
        setPartialAmount('');
        setDecisionReason('');
        setDecisionError('');
    };

    const handleSubmitDecision = async () => {
        setDecisionError('');

        if (!decisionStatus) {
            setDecisionError('Please select a decision.');
            return;
        }
        if (decisionStatus === 'DELIMICNO_ODOBREN') {
            const amt = parseFloat(partialAmount);
            const req = requests.find(r => r.id === activeRequestId);
            if (!partialAmount || isNaN(amt) || amt <= 0) {
                setDecisionError('Please enter a valid approved amount.');
                return;
            }
            if (amt >= req.zahtevanIznos) {
                setDecisionError('For partial approval, the amount must be less than the requested amount. Use "Approve" for the full amount.');
                return;
            }
        }
        if ((decisionStatus === 'DELIMICNO_ODOBREN' || decisionStatus === 'ODBIJEN') && !decisionReason.trim()) {
            setDecisionError('A reason is required for this decision.');
            return;
        }

        setSubmitting(true);
        try {
            await api.put(`/zahtevi-za-sredstvima/${activeRequestId}/odluka`, {
                status: decisionStatus,
                odobrenIznos: decisionStatus === 'DELIMICNO_ODOBREN' ? parseFloat(partialAmount) : null,
                razlogOdluke: decisionReason || null,
            });
            handleCloseDecision();
            setShowSuccess(true);
            await loadData();
        } catch (e) {
            setDecisionError(e.response?.data?.message || 'Error saving decision.');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <div className="loading-text">Loading...</div>;
    if (!project) return <div className="loading-text">Project not found.</div>;

    const activeReq = requests.find(r => r.id === activeRequestId);
    const pendingRequests = requests.filter(r => r.status === 'NA_CEKANJU');
    const resolvedRequests = requests.filter(r => r.status !== 'NA_CEKANJU');

    return (
        <div className="create-page">
            {/* Success modal */}
            {showSuccess && (
                <div className="modal-overlay">
                    <div className="modal-box">
                        <p>Decision saved successfully!</p>
                        <button className="btn-primary" onClick={() => setShowSuccess(false)}>OK</button>
                    </div>
                </div>
            )}

            {/* Decision modal */}
            {activeRequestId && activeReq && (
                <div className="modal-overlay">
                    <div className="modal-box" style={{ maxWidth: 480, width: '90%', textAlign: 'left' }}>
                        <h3 style={{ marginBottom: 4 }}>Review funding request</h3>
                        <p style={{ fontSize: '0.82rem', color: '#555', marginBottom: 16 }}>
                            Project: <strong>{project.naziv}</strong> &nbsp;·&nbsp;
                            Coordinator: {activeReq.koordinatorIme} {activeReq.koordinatorPrezime}
                        </p>

                        {/* Info o zahtevu */}
                        <div style={{ backgroundColor: '#f8f9fa', borderRadius: 8, padding: '12px 16px', marginBottom: 16 }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                                <span style={{ fontSize: '0.82rem', color: '#555' }}>Requested amount</span>
                                <span style={{ fontWeight: 700, fontSize: '1rem' }}>
                                    {activeReq.zahtevanIznos.toLocaleString('sr-RS', { minimumFractionDigits: 2 })} RSD
                                </span>
                            </div>
                            <div style={{ fontSize: '0.82rem', color: '#555', marginBottom: 4 }}>Reason:</div>
                            <div style={{ fontSize: '0.88rem', marginBottom: 12 }}>{activeReq.razlogZahteva}</div>

                            {orgFunds != null && (
                                <div style={{ borderTop: '1px solid #dee2e6', paddingTop: 10 }}>
                                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                                        <span style={{ fontSize: '0.82rem', color: '#555' }}>Organization general funds</span>
                                        <span style={{ fontWeight: 600 }}>
                                            {orgFunds.toLocaleString('sr-RS', { minimumFractionDigits: 2 })} RSD
                                        </span>
                                    </div>
                                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                        <span style={{ fontSize: '0.82rem', color: '#555' }}>Remaining if fully approved</span>
                                        <span style={{
                                            fontWeight: 600,
                                            color: (orgFunds - activeReq.zahtevanIznos) >= 0 ? '#155724' : '#721c24',
                                        }}>
                                            {(orgFunds - activeReq.zahtevanIznos).toLocaleString('sr-RS', { minimumFractionDigits: 2 })} RSD
                                        </span>
                                    </div>
                                </div>
                            )}
                        </div>

                        {/* Izbor odluke */}
                        <div style={{ marginBottom: 12 }}>
                            <label style={{ fontSize: '0.85rem', fontWeight: 600, display: 'block', marginBottom: 6 }}>
                                Decision *
                            </label>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                                {[
                                    { value: 'ODOBREN', label: '✓ Approve full amount', color: '#155724', bg: '#d4edda' },
                                    { value: 'DELIMICNO_ODOBREN', label: '◑ Partially approve', color: '#0c5460', bg: '#d1ecf1' },
                                    { value: 'ODBIJEN', label: '✕ Reject', color: '#721c24', bg: '#f8d7da' },
                                ].map(opt => (
                                    <label key={opt.value} style={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: 10,
                                        padding: '8px 12px',
                                        borderRadius: 6,
                                        border: `2px solid ${decisionStatus === opt.value ? opt.color : '#dee2e6'}`,
                                        backgroundColor: decisionStatus === opt.value ? opt.bg : '#fff',
                                        cursor: 'pointer',
                                        transition: 'all 0.15s',
                                    }}>
                                        <input
                                            type="radio"
                                            name="decisionStatus"
                                            value={opt.value}
                                            checked={decisionStatus === opt.value}
                                            onChange={() => { setDecisionStatus(opt.value); setDecisionError(''); }}
                                            style={{ accentColor: opt.color }}
                                        />
                                        <span style={{ fontWeight: 500, color: opt.color, fontSize: '0.88rem' }}>
                                            {opt.label}
                                        </span>
                                    </label>
                                ))}
                            </div>
                        </div>

                        {/* Iznos za delimično odobrenje */}
                        {decisionStatus === 'DELIMICNO_ODOBREN' && (
                            <div style={{ marginBottom: 12 }}>
                                <label style={{ fontSize: '0.85rem', fontWeight: 600, display: 'block', marginBottom: 6 }}>
                                    Approved amount (RSD) *
                                </label>
                                <input
                                    type="number"
                                    min="1"
                                    step="0.01"
                                    placeholder={`Max: ${activeReq.zahtevanIznos - 0.01}`}
                                    value={partialAmount}
                                    onChange={e => setPartialAmount(e.target.value)}
                                    style={{ width: '100%', padding: '8px 12px', borderRadius: 6, border: '1px solid #bbb', fontSize: '0.88rem', boxSizing: 'border-box' }}
                                />
                            </div>
                        )}

                        {/* Razlog (obavezan za delimično i odbijanje) */}
                        {(decisionStatus === 'DELIMICNO_ODOBREN' || decisionStatus === 'ODBIJEN') && (
                            <div style={{ marginBottom: 12 }}>
                                <label style={{ fontSize: '0.85rem', fontWeight: 600, display: 'block', marginBottom: 6 }}>
                                    Reason *
                                </label>
                                <textarea
                                    placeholder="Explain your decision..."
                                    value={decisionReason}
                                    onChange={e => setDecisionReason(e.target.value)}
                                    rows={3}
                                    style={{ width: '100%', padding: '8px 12px', borderRadius: 6, border: '1px solid #bbb', fontSize: '0.88rem', resize: 'vertical', boxSizing: 'border-box' }}
                                />
                            </div>
                        )}

                        {decisionError && (
                            <p className="error-text" style={{ marginBottom: 8 }}>{decisionError}</p>
                        )}

                        <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end', marginTop: 8 }}>
                            <button className="btn-cancel" onClick={handleCloseDecision}>Cancel</button>
                            <button className="btn-save" onClick={handleSubmitDecision} disabled={submitting}>
                                {submitting ? 'Saving...' : 'Confirm decision'}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <button className="create-back-btn" onClick={() => navigate('/manager/funding')}>
                ← Back to Funding requests
            </button>

            <h1 className="create-title">Funding requests</h1>
            <p className="create-subtitle">
                Project: <strong>{project.naziv}</strong> &nbsp;·&nbsp;
                Coordinator: {project.koordinatorIme} {project.koordinatorPrezime}
            </p>

            {/* Stanje opštih sredstava */}
            {orgFunds != null && (
                <div style={{
                    backgroundColor: '#e3f2fd',
                    border: '1px solid #90caf9',
                    borderRadius: 8,
                    padding: '12px 20px',
                    marginBottom: 24,
                    display: 'flex',
                    alignItems: 'center',
                    gap: 12,
                }}>
                    <span style={{ fontSize: '1.3rem' }}>🏦</span>
                    <div>
                        <div style={{ fontSize: '0.78rem', color: '#0d47a1', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                            Organization general funds
                        </div>
                        <div style={{ fontSize: '1.2rem', fontWeight: 700, color: '#0d47a1' }}>
                            {orgFunds.toLocaleString('sr-RS', { minimumFractionDigits: 2 })} RSD
                        </div>
                    </div>
                </div>
            )}

            {error && <p className="error-text" style={{ marginBottom: 16 }}>{error}</p>}

            {/* Zahtevi na čekanju */}
            {pendingRequests.length > 0 && (
                <div className="form-section">
                    <h3 style={{ marginBottom: 16 }}>
                        Pending requests
                        <span style={{
                            marginLeft: 10, backgroundColor: '#fff3cd', color: '#856404',
                            fontSize: '0.78rem', fontWeight: 600, padding: '2px 10px',
                            borderRadius: 12, border: '1px solid #ffc107',
                        }}>
                            {pendingRequests.length}
                        </span>
                    </h3>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                        {pendingRequests.map(req => (
                            <div key={req.id} style={{
                                border: '1px solid #ffc107',
                                borderRadius: 8,
                                padding: '14px 18px',
                                backgroundColor: '#fffdf0',
                            }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 8 }}>
                                    <div>
                                        <div style={{ fontWeight: 700, fontSize: '1.05rem', marginBottom: 4 }}>
                                            {req.zahtevanIznos.toLocaleString('sr-RS', { minimumFractionDigits: 2 })} RSD
                                        </div>
                                        <div style={{ fontSize: '0.8rem', color: '#555', marginBottom: 8 }}>
                                            Submitted: {new Date(req.datumZahteva).toLocaleDateString('sr-RS')} &nbsp;·&nbsp;
                                            By: {req.koordinatorIme} {req.koordinatorPrezime}
                                        </div>
                                        <div style={{ fontSize: '0.88rem' }}>
                                            <strong>Reason:</strong> {req.razlogZahteva}
                                        </div>
                                    </div>
                                    <button
                                        className="btn-primary"
                                        onClick={() => handleOpenDecision(req)}
                                        style={{ whiteSpace: 'nowrap' }}
                                    >
                                        Make decision
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}

            {/* Rešeni zahtevi */}
            {resolvedRequests.length > 0 && (
                <div className="form-section">
                    <h3 style={{ marginBottom: 16 }}>Resolved requests</h3>

                    <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
                        <thead>
                            <tr style={{ backgroundColor: '#f5f5f5' }}>
                                <th style={thStyle}>Date</th>
                                <th style={{ ...thStyle, textAlign: 'right' }}>Requested</th>
                                <th style={{ ...thStyle, textAlign: 'right' }}>Approved</th>
                                <th style={thStyle}>Status</th>
                                <th style={thStyle}>Reason</th>
                                <th style={thStyle}>Decision date</th>
                            </tr>
                        </thead>
                        <tbody>
                            {resolvedRequests.map(req => (
                                <tr key={req.id} style={{ borderBottom: '1px solid #eee' }}>
                                    <td style={{ ...tdStyle, fontSize: '0.8rem', color: '#555' }}>
                                        {new Date(req.datumZahteva).toLocaleDateString('sr-RS')}
                                    </td>
                                    <td style={{ ...tdStyle, textAlign: 'right' }}>
                                        {req.zahtevanIznos.toLocaleString('sr-RS', { minimumFractionDigits: 2 })}
                                    </td>
                                    <td style={{ ...tdStyle, textAlign: 'right' }}>
                                        {req.odobrenIznos != null
                                            ? req.odobrenIznos.toLocaleString('sr-RS', { minimumFractionDigits: 2 })
                                            : '—'}
                                    </td>
                                    <td style={tdStyle}>
                                        <span style={{
                                            fontWeight: 600,
                                            color: REQUEST_STATUS_COLOR[req.status] || '#333',
                                            fontSize: '0.82rem',
                                        }}>
                                            {REQUEST_STATUS_LABEL[req.status] || req.status}
                                        </span>
                                    </td>
                                    <td style={{ ...tdStyle, fontSize: '0.8rem', color: '#666' }}>
                                        {req.razlogOdluke || '—'}
                                    </td>
                                    <td style={{ ...tdStyle, fontSize: '0.8rem', color: '#555' }}>
                                        {req.datumOdluke
                                            ? new Date(req.datumOdluke).toLocaleDateString('sr-RS')
                                            : '—'}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {!loading && requests.length === 0 && (
                <div className="form-section">
                    <p style={{ color: '#888', fontSize: '0.88rem' }}>No funding requests for this project.</p>
                </div>
            )}
        </div>
    );
};

const thStyle = {
    padding: '8px 12px',
    textAlign: 'left',
    fontWeight: 600,
    fontSize: '0.8rem',
    color: '#555',
    borderBottom: '2px solid #e0e0e0',
};

const tdStyle = {
    padding: '8px 12px',
    verticalAlign: 'top',
};

export default ManagerFundingRequestDetailPage;
