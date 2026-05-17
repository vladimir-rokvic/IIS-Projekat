import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

// Mapiramo backend status na čitljiv tekst
const statusLabel = {
    U_PRIPREMI: "In preparation",
    SPREMAN_ZA_ODOBRENJE: "Ready for approval",
    ODOBREN: "Accepted",
    ODBIJEN: "Denied",
    POTREBNA_IZMENA: "Necessary change",
};

const ProjectsPage = () => {
    const navigate = useNavigate();
    const { user, logout } = useAuth();
    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchProjects = async () => {
            try {
                const res = await api.get("/projekti/moji");
                setProjects(res.data);
            } catch (e) {
                if (e.response?.status === 401) {
                    logout();
                    navigate('/login');
                } else {
                    setError("Failed to load projects.");
                }
            } finally {
                setLoading(false);
            }
        };
        fetchProjects();
    }, []);

    return (
        <div>
            <header className="dashboard-header">
                <h1>Welcome to Our Humanitarian Organization</h1>
                <div className="user-info" onClick={() => { logout(); navigate('/login'); }} title="Logout">
                    <div className="avatar" />
                    <span>{user?.name} {user?.surname}</span>
                </div>
            </header>

            <div className="projects-page">
                <div className="projects-top">
                    <div className="projects-top-left">
                        <h1>Projects</h1>
                        <p>Manage existing projects and create a new one.</p>
                    </div>
                    <button className="btn-primary" onClick={() => navigate('/projects/new')}>
                        Create a project +
                    </button>
                </div>

                <div className="projects-container">
                    <h2>All projects</h2>

                    {loading && <p className="loading-text">Loading...</p>}
                    {error && <p className="error-text">{error}</p>}

                    {!loading && !error && projects.length === 0 && (
                        <p className="loading-text">No projects yet. Create your first one!</p>
                    )}

                    {!loading && !error && projects.length > 0 && (
                        <div className="projects-grid">
                            {projects.map((project) => (
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
                                        onClick={() =>
                                            project.status === 'U_PRIPREMI'
                                                ? navigate(`/projects/${project.id}/edit`)
                                                : navigate(`/projects/${project.id}`)
                                        }
                                    >
                                        Details
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

export default ProjectsPage;
