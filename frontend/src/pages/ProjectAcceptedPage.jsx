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

const ProjectAcceptedPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [project, setProject] = useState(null);
    const [loading, setLoading] = useState(true);

    // KPI
    const [kpiDescription, setKpiDescription] = useState('');
    const [kpiFrequency, setKpiFrequency] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [kpiError, setKpiError] = useState('');
    const [showKpiSuccess, setShowKpiSuccess] = useState(false);

    // Resursi projekta
    const [resources, setResources] = useState([]);
    const [resourcesLoading, setResourcesLoading] = useState(true);

    // Zahtev za sredstvima
    const [fundingAmount, setFundingAmount] = useState('');
    const [fundingReason, setFundingReason] = useState('');
    const [fundingSubmitting, setFundingSubmitting] = useState(false);
    const [fundingError, setFundingError] = useState('');
    const [showFundingSuccess, setShowFundingSuccess] = useState(false);

    // Istorija zahteva
    const [fundingRequests, setFundingRequests] = useState([]);
    const [requestsLoading, setRequestsLoading] = useState(true);

    useEffect(() => {
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

        // Učitaj resurse projekta
        api.get(`/resursi/projekat/${id}`)
            .then(res => setResources(res.data))
            .catch(() => setResources([]))
            .finally(() => setResourcesLoading(false));

        // Učitaj istoriju zahteva za ovaj projekat
        api.get(`/zahtevi-za-sredstvima/projekat/${id}`)
            .then(res => setFundingRequests(res.data))
            .catch(() => setFundingRequests([]))
            .finally(() => setRequestsLoading(false));
    }, [id]);

    const handleSaveKpi = async () => {
        setKpiError('');
        if (!kpiDescription.trim()) { setKpiError('KPI description is required.'); return; }
        if (!kpiFrequency) { setKpiError('Please select measurement frequency.'); return; }

        setSubmitting(true);
        try {
            await api.put(`/projekti/${id}/kpi`, {
                opis: kpiDescription,
                intervalMerenja: kpiFrequency,
            });
            setShowKpiSuccess(true);
        } catch (e) {
            setKpiError(e.response?.data?.message || 'Error saving KPI.');
        } finally {
            setSubmitting(false);
        }
    };

    const handleSendFundingRequest = async () => {
        setFundingError('');
        const amount = parseFloat(fundingAmount);
        if (!fundingAmount || isNaN(amount) || amount <= 0) {
            setFundingError('Please enter a valid positive amount.');
            return;
        }
        if (!fundingReason.trim()) {
            setFundingError('Please provide a reason for the request.');
            return;
        }

        setFundingSubmitting(true);
        try {
            await api.post('/zahtevi-za-sredstvima', {
                projectId: parseInt(id),
                zahtevanIznos: amount,
                razlogZahteva: fundingReason,
            });
            setFundingAmount('');
            setFundingReason('');
            setShowFundingSuccess(true);
            // Osvježi istoriju zahteva
            const res = await api.get(`/zahtevi-za-sredstvima/projekat/${id}`);
            setFundingRequests(res.data);
        } catch (e) {
            setFundingError(e.response?.data?.message || 'Error sending request.');
        } finally {
            setFundingSubmitting(false);
        }
    };

    if (loading) return <div className="loading-text">Loading...</div>;
    if (!project) return <div className="loading-text">Project not found.</div>;

    const totalAvailable = resources.reduce((sum, r) => sum + (r.dostupnoSredstava || 0), 0);
    const totalAllocated = resources.reduce((sum, r) => sum + (r.ukupnoSredstava || 0), 0);

    return (
        <div className="create-page">
            {/* KPI success modal */}
            {showKpiSuccess && (
                <div className="modal-overlay">
                    <div className="modal-box">
                        <p>KPI successfully saved!</p>
                        <button className="btn-primary" onClick={() => setShowKpiSuccess(false)}>OK</button>
                    </div>
                </div>
            )}

            {/* Funding request success modal */}
            {showFundingSuccess && (
                <div className="modal-overlay">
                    <div className="modal-box">
                        <p>Funding request sent successfully!<br />The manager will review it shortly.</p>
                        <button className="btn-primary" onClick={() => setShowFundingSuccess(false)}>OK</button>
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

            {/* Basic information */}
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

            {/* Additional information */}
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

            {/* ── RESURSI PROJEKTA ── */}
            <div className="form-section">
                <h3 style={{ marginBottom: 8 }}>Project resources</h3>
                <p style={{ fontSize: '0.82rem', color: '#555', marginBottom: 16 }}>
                    Dedicated funds allocated to this project.
                </p>

                {resourcesLoading ? (
                    <p className="loading-text" style={{ fontSize: '0.85rem' }}>Loading resources...</p>
                ) : resources.length === 0 ? (
                    <p style={{ fontSize: '0.85rem', color: '#888' }}>No dedicated resources allocated yet.</p>
                ) : (
                    <>
                        {/* Summary */}
                        <div style={{
                            display: 'flex', gap: 16, marginBottom: 16, flexWrap: 'wrap'
                        }}>
                            <div style={summaryCardStyle('#e8f5e9', '#155724')}>
                                <div style={{ fontSize: '0.75rem', marginBottom: 4 }}>Total allocated</div>
                                <div style={{ fontSize: '1.1rem', fontWeight: 700 }}>
                                    {totalAllocated.toLocaleString('sr-RS', { minimumFractionDigits: 2 })} RSD
                                </div>
                            </div>
                            <div style={summaryCardStyle('#e3f2fd', '#0d47a1')}>
                                <div style={{ fontSize: '0.75rem', marginBottom: 4 }}>Available</div>
                                <div style={{ fontSize: '1.1rem', fontWeight: 700 }}>
                                    {totalAvailable.toLocaleString('sr-RS', { minimumFractionDigits: 2 })} RSD
                                </div>
                            </div>
                            <div style={summaryCardStyle('#fce4ec', '#880e4f')}>
                                <div style={{ fontSize: '0.75rem', marginBottom: 4 }}>Spent</div>
                                <div style={{ fontSize: '1.1rem', fontWeight: 700 }}>
                                    {(totalAllocated - totalAvailable).toLocaleString('sr-RS', { minimumFractionDigits: 2 })} RSD
                                </div>
                            </div>
                        </div>

                        {/* Tabela resursa */}
                        <table style={tableStyle}>
                            <thead>
                            <tr style={{ backgroundColor: '#f5f5f5' }}>
                                <th style={thStyle}>Resource</th>
                                <th style={{ ...thStyle, textAlign: 'right' }}>Total</th>
                                <th style={{ ...thStyle, textAlign: 'right' }}>Available</th>
                                <th style={{ ...thStyle, textAlign: 'right' }}>Spent</th>
                                <th style={thStyle}>Notes</th>
                            </tr>
                            </thead>
                            <tbody>
                            {resources.map(r => (
                                <tr key={r.id} style={{ borderBottom: '1px solid #eee' }}>
                                    <td style={tdStyle}>{r.naziv}</td>
                                    <td style={{ ...tdStyle, textAlign: 'right' }}>
                                        {r.ukupnoSredstava.toLocaleString('sr-RS', { minimumFractionDigits: 2 })}
                                    </td>
                                    <td style={{ ...tdStyle, textAlign: 'right', color: r.dostupnoSredstava > 0 ? '#155724' : '#721c24', fontWeight: 600 }}>
                                        {r.dostupnoSredstava.toLocaleString('sr-RS', { minimumFractionDigits: 2 })}
                                    </td>
                                    <td style={{ ...tdStyle, textAlign: 'right' }}>
                                        {r.potrosenoSredstava.toLocaleString('sr-RS', { minimumFractionDigits: 2 })}
                                    </td>
                                    <td style={{ ...tdStyle, color: '#888', fontSize: '0.8rem' }}>{r.opis || '—'}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </>
                )}
            </div>

            {/* ── ZAHTEV ZA DODATNA SREDSTVA ── */}
            <div className="form-section">
                <h3 style={{ marginBottom: 8 }}>Request additional funds</h3>
                <p style={{ fontSize: '0.82rem', color: '#555', marginBottom: 16 }}>
                    If dedicated resources are insufficient, you can request additional funds from the organization's
                    general donations. The manager will review and decide on your request.
                </p>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Requested amount (RSD) *</label>
                    <input
                        type="number"
                        min="1"
                        step="0.01"
                        placeholder="e.g. 50000"
                        value={fundingAmount}
                        onChange={e => setFundingAmount(e.target.value)}
                        style={{ maxWidth: 240 }}
                    />
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Reason *</label>
                    <textarea
                        placeholder="Describe why these additional funds are necessary..."
                        value={fundingReason}
                        onChange={e => setFundingReason(e.target.value)}
                        rows={4}
                    />
                </div>

                {fundingError && (
                    <p className="error-text" style={{ marginBottom: 8 }}>{fundingError}</p>
                )}

                <button
                    className="btn-save"
                    onClick={handleSendFundingRequest}
                    disabled={fundingSubmitting}
                    style={{ marginTop: 4 }}
                >
                    {fundingSubmitting ? 'Sending...' : 'Send request'}
                </button>
            </div>

            {/* ── ISTORIJA ZAHTEVA ── */}
            {!requestsLoading && fundingRequests.length > 0 && (
                <div className="form-section">
                    <h3 style={{ marginBottom: 12 }}>Funding request history</h3>
                    <table style={tableStyle}>
                        <thead>
                        <tr style={{ backgroundColor: '#f5f5f5' }}>
                            <th style={thStyle}>Date</th>
                            <th style={{ ...thStyle, textAlign: 'right' }}>Requested</th>
                            <th style={{ ...thStyle, textAlign: 'right' }}>Approved</th>
                            <th style={thStyle}>Status</th>
                            <th style={thStyle}>Manager's note</th>
                        </tr>
                        </thead>
                        <tbody>
                        {fundingRequests.map(req => (
                            <tr key={req.id} style={{ borderBottom: '1px solid #eee' }}>
                                <td style={{ ...tdStyle, fontSize: '0.82rem', color: '#555' }}>
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
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}

            {/* KPI */}
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

            {kpiError && <p className="error-text" style={{ marginBottom: 8 }}>{kpiError}</p>}
            <div className="form-actions">
                <button className="btn-cancel" onClick={() => navigate('/projects')}>Cancel</button>
                <button className="btn-save" onClick={handleSaveKpi} disabled={submitting}>
                    {submitting ? 'Saving...' : 'Save KPI'}
                </button>
            </div>
        </div>
    );
};

// ── Style helpers ──

const summaryCardStyle = (bg, color) => ({
    backgroundColor: bg,
    color,
    borderRadius: 8,
    padding: '10px 18px',
    minWidth: 160,
    flex: '1 1 auto',
});

const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    fontSize: '0.85rem',
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

export default ProjectAcceptedPage;
