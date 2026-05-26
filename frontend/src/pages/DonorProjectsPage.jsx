import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./DonorDashboard.css";

const DonorProjectsPage = () => {
    const navigate = useNavigate();
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        api.get("/projekti/odobreni")
            .then(res => setProjects(res.data))
            .catch(() => setError("Failed to load projects."))
            .finally(() => setLoading(false));
    }, []);

    return (
        <div className="donor-content">
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 28 }}>
                <div>
                    <h1 className="donor-page-title" style={{ marginBottom: 4 }}>Projects</h1>
                    <p style={{ fontSize: "0.95rem", color: "#4b5563" }}>Browse all active and upcoming projects</p>
                </div>
            </div>

            {loading && <p style={{ color: "#555" }}>Loading...</p>}
            {error && <p style={{ color: "#cc0000" }}>{error}</p>}
            {!loading && !error && projects.length === 0 && (
                <p style={{ color: "#555" }}>No projects available at the moment.</p>
            )}

            {!loading && !error && projects.length > 0 && (
                <div className="donor-campaign-grid">
                    {projects.map(project => (
                        <article key={project.id} className="donor-campaign-card donor-panel">
                            <h3>{project.naziv}</h3>
                            <p className="donor-small-text">{project.opis}</p>
                            <div style={{ marginTop: "auto", display: "flex", flexDirection: "column", gap: 6 }}>
                                <p className="donor-small-text">
                                    <strong>Start:</strong> {project.rokPocetak} &nbsp;|&nbsp; <strong>End:</strong> {project.rokKraj}
                                </p>
                                {project.geografskaLokacija && (
                                    <p className="donor-small-text"><strong>Location:</strong> {project.geografskaLokacija}</p>
                                )}
                                <button
                                    className="donor-btn"
                                    style={{ alignSelf: "center", marginTop: 8 }}
                                    onClick={() => navigate(`/donor/projects/${project.id}`)}
                                >
                                    Details
                                </button>
                            </div>
                        </article>
                    ))}
                </div>
            )}
        </div>
    );
};

export default DonorProjectsPage;
