import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const CreateProjectPage = () => {
    const navigate = useNavigate();
    const { user } = useAuth();

    // Obavezna polja
    const [naziv, setNaziv] = useState('');
    const [opis, setOpis] = useState('');
    const [ciljevi, setCiljevi] = useState('');
    const [rokPocetak, setRokPocetak] = useState('');
    const [rokKraj, setRokKraj] = useState('');

    // Opciona polja
    const [ciljnaGrupa, setCiljnaGrupa] = useState('');
    const [geografskaLokacija, setGeografskaLokacija] = useState('');
    const [izvoriFinansiranja, setIzvoriFinansiranja] = useState('');

    // Pomoćni koordinatori
    const [sviKoordinatori, setSviKoordinatori] = useState([]);
    const [odabraniPomocni, setOdabraniPomocni] = useState([]);

    // Dokument
    const fileRef = useRef();
    const [fileName, setFileName] = useState('No file selected');

    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        // Učitaj sve koordinatore za odabir pomoćnih
        api.get("/projekti/koordinatori")
            .then(res => setSviKoordinatori(res.data))
            .catch(() => {}); // nije kritično
    }, []);

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        setFileName(file ? file.name : 'No file selected');
    };

    const togglePomocni = (id) => {
        setOdabraniPomocni(prev =>
            prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id]
        );
    };

    const handleSubmit = async () => {
        setError('');

        // Validacija
        if (!naziv.trim()) { setError('Project name is required.'); return; }
        if (!opis.trim()) { setError('Description is required.'); return; }
        if (!ciljevi.trim()) { setError('Project goals are required.'); return; }
        if (!rokPocetak) { setError('Start date is required.'); return; }
        if (!rokKraj) { setError('End date is required.'); return; }
        if (!fileRef.current?.files[0]) { setError('Project document is required.'); return; }

        setSubmitting(true);
        try {
            const formData = new FormData();
            formData.append('naziv', naziv);
            formData.append('opis', opis);
            formData.append('ciljevi', ciljevi);
            formData.append('rokPocetak', rokPocetak);
            formData.append('rokKraj', rokKraj);
            if (ciljnaGrupa) formData.append('ciljnaGrupa', ciljnaGrupa);
            if (geografskaLokacija) formData.append('geografskaLokacija', geografskaLokacija);
            if (izvoriFinansiranja) formData.append('izvoriFinansiranja', izvoriFinansiranja);
            formData.append('dokument', fileRef.current.files[0]);

            const res = await api.post('/projekti', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });

            // Postavi pomoćne koordinatore ako su odabrani
            if (odabraniPomocni.length > 0) {
                await api.put(`/projekti/${res.data.id}/pomocni-koordinatori`, {
                    pomocniKoordinatoriIds: odabraniPomocni
                });
            }

            navigate(`/projects/${res.data.id}/edit`);
        } catch (e) {
            setError(e.response?.data?.message || 'Error creating project. Please try again.');
        } finally {
            setSubmitting(false);
        }
    };

    // Filtriramo trenutnog korisnika iz liste pomoćnih koordinatora
    const pomocniOptions = sviKoordinatori.filter(k => k.email !== user?.email);

    return (
        <div className="create-page">
            <button className="create-back-btn" onClick={() => navigate('/projects')}>
                ← Back to Projects
            </button>

            <h1 className="create-title">Create a new project</h1>
            <p className="create-subtitle">Fill in the required information to create a new project.</p>

            {/* Basic information */}
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
                    <input
                        type="text"
                        placeholder="Enter the name of the project"
                        value={naziv}
                        onChange={e => setNaziv(e.target.value)}
                    />
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Description *</label>
                    <textarea
                        placeholder="Enter a short description of the project"
                        value={opis}
                        onChange={e => setOpis(e.target.value)}
                        rows={3}
                    />
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Project goals *</label>
                    <textarea
                        placeholder="Describe the project goals"
                        value={ciljevi}
                        onChange={e => setCiljevi(e.target.value)}
                        rows={3}
                    />
                </div>

                <div className="form-row">
                    <div className="form-field">
                        <label>Deadlines *</label>
                        <div style={{ display: 'flex', gap: 10 }}>
                            <input
                                type="date"
                                value={rokPocetak}
                                onChange={e => setRokPocetak(e.target.value)}
                                placeholder="Start date"
                            />
                            <input
                                type="date"
                                value={rokKraj}
                                onChange={e => setRokKraj(e.target.value)}
                                placeholder="End date"
                            />
                        </div>
                    </div>
                    <div className="status-field">
                        <label>Status</label>
                        <div className="status-display">In preparation</div>
                    </div>
                </div>

                <div className="form-field">
                    <label>Assistant coordinators</label>
                    <select
                        multiple
                        value={odabraniPomocni.map(String)}
                        onChange={e => {
                            const selected = Array.from(e.target.selectedOptions, o => Number(o.value));
                            setOdabraniPomocni(selected);
                        }}
                        style={{ height: pomocniOptions.length > 0 ? 'auto' : 36, minHeight: 36 }}
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
                        <span style={{ fontSize: '0.78rem', color: '#666' }}>
                            Hold Ctrl / Cmd to select multiple
                        </span>
                    }
                </div>
            </div>

            {/* Additional information */}
            <div className="form-section">
                <h3 style={{ marginBottom: 16 }}>Additional information</h3>

                <div className="form-row">
                    <div className="form-field">
                        <label>Target group</label>
                        <input
                            type="text"
                            placeholder="Enter the target group"
                            value={ciljnaGrupa}
                            onChange={e => setCiljnaGrupa(e.target.value)}
                        />
                    </div>
                    <div className="form-field">
                        <label>Geographic location</label>
                        <input
                            type="text"
                            placeholder="Enter the geographic location"
                            value={geografskaLokacija}
                            onChange={e => setGeografskaLokacija(e.target.value)}
                        />
                    </div>
                </div>

                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>Funding sources (optional)</label>
                    <input
                        type="text"
                        placeholder="Enter funding sources"
                        value={izvoriFinansiranja}
                        onChange={e => setIzvoriFinansiranja(e.target.value)}
                    />
                </div>

                <div className="form-field">
                    <label>Project document upload *</label>
                    <div className="file-upload-row">
                        <label className="upload-btn">
                            Upload
                            <input
                                type="file"
                                ref={fileRef}
                                style={{ display: 'none' }}
                                onChange={handleFileChange}
                                accept=".pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx"
                            />
                        </label>
                        <span className="file-name">{fileName}</span>
                    </div>
                </div>
            </div>

            {/* KPI (disabled — tek nakon odobrenja) */}
            <div className="form-section disabled">
                <h3 style={{ marginBottom: 8 }}>KPI configuration</h3>
                <p style={{ fontSize: '0.82rem', color: '#666', marginBottom: 16 }}>
                    KPI can be configured after the project is approved.
                </p>
                <div className="form-field" style={{ marginBottom: 14 }}>
                    <label>KPI description</label>
                    <textarea
                        placeholder="Enter a short KPI description of the project"
                        disabled
                        rows={3}
                    />
                </div>
                <div className="form-field">
                    <label>Measurement Frequency</label>
                    <select disabled>
                        <option>Select frequency</option>
                    </select>
                </div>
            </div>

            {/* Actions */}
            {error && <p className="error-text" style={{ marginBottom: 8 }}>{error}</p>}
            <div className="form-actions">
                <button className="btn-cancel" onClick={() => navigate('/projects')}>
                    Cancel
                </button>
                <button className="btn-save" onClick={handleSubmit} disabled={submitting}>
                    {submitting ? 'Saving...' : 'Save'}
                </button>
            </div>
        </div>
    );
};

export default CreateProjectPage;
