import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const REQUEST_STATUS_COLOR = {
    NA_CEKANJU: "#856404",
    ODOBREN: "#155724",
    DELIMICNO_ODOBREN: "#0c5460",
    ODBIJEN: "#721c24",
};

const REQUEST_STATUS_BG = {
    NA_CEKANJU: "#fff3cd",
    ODOBREN: "#d4edda",
    DELIMICNO_ODOBREN: "#d1ecf1",
    ODBIJEN: "#f8d7da",
};

const REQUEST_STATUS_LABEL = {
    NA_CEKANJU: "Pending",
    ODOBREN: "Approved",
    DELIMICNO_ODOBREN: "Partially approved",
    ODBIJEN: "Rejected",
};

const ManagerFundingProjectsPage = () => {
    const navigate = useNavigate();
    const { logout } = useAuth();

    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        api.get("/zahtevi-za-sredstvima/svi")
            .then(res => setRequests(res.data))
            .catch(e => {
                if (e.response?.status === 401) { logout(); navigate('/login'); }
                else setError("Failed to load funding requests.");
            })
            .finally(() => setLoading(false));
    }, []);

    // Grupiši zahteve po projektu — prikaži samo projekte koji imaju bar jedan zahtev
    const projectsMap = {};
    requests.forEach(req => {
        if (!projectsMap[req.projectId]) {
            projectsMap[req.projectId] = {
                projectId: req.projectId,
                projectNaziv: req.projectNaziv,
                requests: [],
            };
        }
        projectsMap[req.projectId].requests.push(req);
    });
    const projects = Object.values(projectsMap);

    return (
        <div>
            <div className="projects-page">
                <div className="projects-top">
                    <div className="projects-top-left">
                        <h1>Funding requests</h1>
                        <p>Projects with pending or past funding requests</p>
                    </div>
                    <button className="btn-primary" onClick={() => navigate('/manager')}>
                        ← Back to Home page
                    </button>
                </div>

                <div className="projects-container">
                    <h2>All projects with requests</h2>

                    {loading && <p className="loading-text">Loading...</p>}
                    {error && <p className="error-text">{error}</p>}
                    {!loading && !error && projects.length === 0 && (
                        <p className="loading-text">No funding requests yet.</p>
                    )}

                    {!loading && !error && projects.length > 0 && (
                        <div className="projects-grid">
                            {projects.map(p => {
                                const pending = p.requests.filter(r => r.status === 'NA_CEKANJU').length;
                                const total = p.requests.length;

                                return (
                                    <div key={p.projectId} className="project-card">
                                        <h3>{p.projectNaziv}</h3>

                                        <p style={{ marginTop: 6, marginBottom: 4 }}>
                                            <span style={{ fontSize: '0.82rem', color: '#555' }}>
                                                Total requests: <strong>{total}</strong>
                                            </span>
                                        </p>

                                        {pending > 0 && (
                                            <p style={{ marginBottom: 8 }}>
                                                <span style={{
                                                    display: 'inline-block',
                                                    backgroundColor: '#fff3cd',
                                                    color: '#856404',
                                                    fontWeight: 600,
                                                    fontSize: '0.8rem',
                                                    padding: '2px 10px',
                                                    borderRadius: 12,
                                                    border: '1px solid #ffc107',
                                                }}>
                                                    {pending} pending
                                                </span>
                                            </p>
                                        )}

                                        {/* Mini pregled statusa poslednjih zahteva */}
                                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 4, marginBottom: 12 }}>
                                            {p.requests.slice(0, 4).map(r => (
                                                <span key={r.id} style={{
                                                    fontSize: '0.75rem',
                                                    padding: '2px 8px',
                                                    borderRadius: 10,
                                                    backgroundColor: REQUEST_STATUS_BG[r.status] || '#eee',
                                                    color: REQUEST_STATUS_COLOR[r.status] || '#333',
                                                    fontWeight: 500,
                                                }}>
                                                    {r.zahtevanIznos.toLocaleString('sr-RS')} — {REQUEST_STATUS_LABEL[r.status]}
                                                </span>
                                            ))}
                                            {p.requests.length > 4 && (
                                                <span style={{ fontSize: '0.75rem', color: '#888', alignSelf: 'center' }}>
                                                    +{p.requests.length - 4} more
                                                </span>
                                            )}
                                        </div>

                                        <button
                                            className="btn-primary"
                                            onClick={() => navigate(`/manager/funding/${p.projectId}`)}
                                        >
                                            Review requests
                                        </button>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ManagerFundingProjectsPage;
