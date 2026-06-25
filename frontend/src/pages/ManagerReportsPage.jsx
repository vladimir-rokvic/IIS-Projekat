import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const statusLabel = {
    U_PRIPREMI: "In preparation",
    SPREMAN_ZA_ODOBRENJE: "Ready for approval",
    ODOBREN: "Accepted",
    NEOPHODNA_IZMENA: "Necessary change",
    ODBIJEN: "Denied",
    ZAVRSEN: "Finished",
};

const ManagerReportsPage = () => {
    const navigate = useNavigate();
    const { logout } = useAuth();

    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [downloading, setDownloading] = useState(null);

    useEffect(() => {
        api.get("/projekti/svi")
            .then(res => setProjects(res.data))
            .catch(e => {
                if (e.response?.status === 401) { logout(); navigate('/login'); }
                else setError("Failed to load projects.");
            })
            .finally(() => setLoading(false));
    }, []);

    const handleGenerateReport = async (projectId) => {
        setDownloading(projectId);
        try {
            const response = await api.get(`/projekti/${projectId}/izvestaj`, {
                responseType: 'blob',
            });
            const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `izvestaj-projekat-${projectId}.pdf`);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch (e) {
            alert('Failed to generate report for this project.');
        } finally {
            setDownloading(null);
        }
    };

    return (
        <div>
            <div className="projects-page">
                <div className="projects-top">
                    <div className="projects-top-left">
                        <h1>Reports</h1>
                        <p>Generate reports for finished projects</p>
                    </div>
                    <button className="btn-primary" onClick={() => navigate('/manager')}>
                        ← Back to Home page
                    </button>
                </div>

                <div className="projects-container">
                    <h2>All projects</h2>

                    {loading && <p className="loading-text">Loading...</p>}
                    {error && <p className="error-text">{error}</p>}
                    {!loading && !error && projects.length === 0 && (
                        <p className="loading-text">No projects yet.</p>
                    )}

                    {!loading && !error && projects.length > 0 && (
                        <div className="projects-grid">
                            {projects.filter(p => p.status !== 'U_PRIPREMI').map(project => (
                                <div key={project.id} className="project-card">
                                    <h3>{project.naziv}</h3>
                                    <p>
                                        Status: <span className={`status-badge status-${project.status}`}>
                                            {statusLabel[project.status] || project.status}
                                        </span>
                                    </p>
                                    <p>Start date: {project.rokPocetak}</p>
                                    <p>Due date: {project.rokKraj}</p>
                                    <p>Coordinator: {project.koordinatorIme} {project.koordinatorPrezime}</p>
                                    <button
                                        className="btn-primary"
                                        onClick={() => handleGenerateReport(project.id)}
                                        disabled={downloading === project.id || project.status !== 'ZAVRSEN'}
                                        title={project.status !== 'ZAVRSEN' ? 'Report available only for finished projects' : ''}
                                    >
                                        {downloading === project.id ? 'Generating...' : '⬇ Generate report'}
                                    </button>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ManagerReportsPage;