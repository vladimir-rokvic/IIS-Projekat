import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const STATUS_OPTIONS = [
    { value: "U_PRIPREMI", label: "In preparation" },
    { value: "SPREMAN_ZA_ODOBRENJE", label: "Ready for approval" },
];

const EditProjectPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    const [naziv, setNaziv] = useState('');
    const [opis, setOpis] = useState('');
    const [ciljevi, setCiljevi] = useState('');
    const [rokPocetak, setRokPocetak] = useState('');
    const [rokKraj, setRokKraj] = useState('');
    const [status, setStatus] = useState('U_PRIPREMI');
    const [razlog, setRazlog] = useState(''); // read-only, od menadžera

    const [ciljnaGrupa, setCiljnaGrupa] = useState('');
    const [geografskaLokacija, setGeografskaLokacija] = useState('');
    const [izvoriFinansiranja, setIzvoriFinansiranja] = useState('');

    const [sviKoordinatori, setSviKoordinatori] = useState([]);
    const [odabraniPomocni, setOdabraniPomocni] = useState([]);

    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        Promise.all([
            api.get(`/projekti/${id}`),
            api.get('/projekti/koordinatori'),
        ]).then(([projektRes, koordinatoriRes]) => {
            const p = projektRes.data;
            setNaziv(p.naziv || '');
            setOpis(p.opis || '');
            setCiljevi(p.ciljevi || '');
            setRokPocetak(p.rokPocetak || '');
            setRokKraj(p.rokKraj || '');
            setStatus(p.status || 'U_PRIPREMI');
            setRazlog(p.razlog || '');
            setCiljnaGrupa(p.ciljnaGrupa || '');
            setGeografskaLokacija(p.geografskaLokacija || '');
            setIzvoriFinansiranja(p.izvoriFinansiranja || '');
            setOdabraniPomocni(p.pomocniKoordinatoriIds || []);
            setSviKoordinatori(koordinatoriRes.data);
        }).catch(() => setError('Failed to load project.'))
            .finally(() => setLoading(false));
    }, [id]);

    const handleSubmit = async () => {
        setError('');
        setSuccess('');

        if (!naziv.trim()) { setError('Project name is required.'); return; }
        if (!opis.trim()) { setError('Description is required.'); return; }
        if (!ciljevi.trim()) { setError('Project goals are required.'); return; }
        if (!rokPocetak) { setError('Start date is required.'); return; }
        if (!rokKraj) { setError('End date is required.'); return; }

        setSubmitting(true);
        try {
            await api.put(`/projekti/${id}`, {
                naziv, opis, ciljevi, rokPocetak, rokKraj,
                ciljnaGrupa: ciljnaGrupa || null,
                geografskaLokacija: geografskaLokacija || null,
                izvoriFinansiranja: izvoriFinansiranja || null,
                pomocniKoordinatoriIds: odabraniPomocni,
            });

            if (status === 'SPREMAN_ZA_ODOBRENJE') {
                await api.put(`/projekti/${id}/spreman`);
            }

            setSuccess('Project saved successfully.');
            if (status === 'SPREMAN_ZA_ODOBRENJE') {
                setTimeout(() => navigate('/projects'), 1000);
            }
        } catch (e) {
            setError(e.response?.data?.message || 'Error saving project.');
        } finally {
            setSubmitting(false);
        }
    };

    const pomocniOptions = sviKoordinatori.filter(k => k.email !== user?.email);

    if (loading) return <div className="loading-text">Loading project...</div>;

    return (
        <div className="create-page">
            <button className="create-back-btn" onClick={() => navigate('/projects')}>
                ← Back to Projects
            </button>

            <h1 className="create-title">Edit project</h1>
            <p className="create-subtitle">Make changes to the project.</p>

            <div className="form-section">
                <div className="form-section-header">
                    <h3>Basic information</h3>
                    <div>
                        <span style={{ fontSize: '0.88rem', fontWeight: 500 }}>Coordinator &nbsp;</span>
                        <span className="coordinator-display">{user?.name} {user?.surname}</span>
                    </div>
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Project name *</label>
                    <input type="text" value={naziv} onChange={e => setNaziv(e.target.value)} />
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Description *</label>
                    <textarea value={opis} onChange={e => setOpis(e.target.value)} rows={3} />
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Project goals *</label>
                    <textarea value={ciljevi} onChange={e => setCiljevi(e.target.value)} rows={3} />
                </div>

                <div className="form-row">
                    <div className="form-field">
                        <label>Deadlines *</label>
                        <div style={{ display: 'flex', gap: 10 }}>
                            <input type="date" value={rokPocetak} onChange={e => setRokPocetak(e.target.value)} />
                            <input type="date" value={rokKraj} onChange={e => setRokKraj(e.target.value)} />
                        </div>
                    </div>
                    <div className="status-field">
                        <label>Status</label>
                        <select
                            value={status}
                            onChange={e => setStatus(e.target.value)}
                            style={{ padding: '8px 12px', backgroundColor: '#e0e0e0', border: '1px solid #bbb', borderRadius: 6, fontSize: '0.88rem' }}
                        >
                            {STATUS_OPTIONS.map(opt => (
                                <option key={opt.value} value={opt.value}>{opt.label}</option>
                            ))}
                        </select>
                    </div>
                </div>

                {/* Razlog od menadžera - read-only, prikazan samo ako postoji */}
                {razlog && (
                    <div className="form-field" style={{ marginTop: 14 }}>
                        <label>Reason from manager</label>
                        <textarea value={razlog} readOnly rows={3} className="readonly-field" />
                    </div>
                )}

                <div className="form-field" style={{ marginTop: 14 }}>
                    <label>Assistant coordinators</label>
                    <select
                        multiple
                        value={odabraniPomocni.map(String)}
                        onChange={e => {
                            const selected = Array.from(e.target.selectedOptions, o => Number(o.value));
                            setOdabraniPomocni(selected);
                        }}
                        style={{ minHeight: 36 }}
                    >
                        {pomocniOptions.length === 0
                            ? <option disabled value="">No other coordinators available</option>
                            : pomocniOptions.map(k => (
                                <option key={k.id} value={k.id}>
                                    {k.ime} {k.prezime} ({k.email})
                                </option>
                            ))
                        }
                    </select>
                    {pomocniOptions.length > 0 &&
                        <span style={{ fontSize: '0.78rem', color: '#666' }}>Hold Ctrl / Cmd to select multiple</span>
                    }
                </div>
            </div>

            <div className="form-section">
                <h3 style={{ marginBottom: 16 }}>Additional information</h3>
                <div className="form-row">
                    <div className="form-field">
                        <label>Target group</label>
                        <input type="text" value={ciljnaGrupa} onChange={e => setCiljnaGrupa(e.target.value)} />
                    </div>
                    <div className="form-field">
                        <label>Geographic location</label>
                        <input type="text" value={geografskaLokacija} onChange={e => setGeografskaLokacija(e.target.value)} />
                    </div>
                </div>
                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Funding sources (optional)</label>
                    <input type="text" value={izvoriFinansiranja} onChange={e => setIzvoriFinansiranja(e.target.value)} />
                </div>
            </div>

            <div className="form-section disabled">
                <h3 style={{ marginBottom: 8 }}>KPI configuration</h3>
                <p style={{ fontSize: '0.82rem', color: '#666', marginBottom: 16 }}>KPI can be configured after the project is approved.</p>
                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>KPI description</label>
                    <textarea placeholder="Enter a short KPI description of the project" disabled rows={3} />
                </div>
                <div className="form-field">
                    <label>Measurement Frequency</label>
                    <select disabled><option>Select frequency</option></select>
                </div>
            </div>

            {error && <p className="error-text" style={{ marginBottom: 8 }}>{error}</p>}
            {success && <p className="success-text" style={{ marginBottom: 8 }}>{success}</p>}
            <div className="form-actions">
                <button className="btn-cancel" onClick={() => navigate('/projects')}>Cancel</button>
                <button className="btn-save" onClick={handleSubmit} disabled={submitting}>
                    {submitting ? 'Saving...' : 'Save'}
                </button>
            </div>
        </div>
    );
};

export default EditProjectPage;
