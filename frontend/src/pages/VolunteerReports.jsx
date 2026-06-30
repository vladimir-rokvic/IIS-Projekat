
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const VolunteerReports = () => {
    const navigate = useNavigate();
    const { logout } = useAuth();

    const [projects, setProjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [downloading, setDownloading] = useState(null);

    useEffect(() => {
        api.get("/volunteer")
            .then(res => {
						setProjects(res.data);
						console.log(res.data);
					})
            .catch(e => {
                if (e.response?.status === 401) { logout(); navigate('/login'); }
                else setError("Failed to load projects.");
            })
            .finally(() => setLoading(false));
    }, []);

    const handleGenerateReport = async (volunteerId) => {
        setDownloading(volunteerId);
        try {
            const response = await api.get(`/volunteer/${volunteerId}/report`, {
                responseType: 'blob',
            });
            const url = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `report-volunteer-${volunteerId}.pdf`);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch (e) {
            alert('Failed to generate report for this volunteer.');
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
                        <p>Generate reports for volunteers</p>
                    </div>
                    <button className="btn-primary" onClick={() => navigate('/manager')}>
                        ← Back to Home page
                    </button>
                </div>

                <div className="projects-container">
                    <h2>All volunteers</h2>

                    {loading && <p className="loading-text">Loading...</p>}
                    {error && <p className="error-text">{error}</p>}
                    {!loading && !error && projects.length === 0 && (
                        <p className="loading-text">No volunteers registered.</p>
                    )}

                    {!loading && !error && projects.length > 0 && (
                        <div className="projects-grid">
                            {projects.map(project => (
                                <div key={project.id} className="project-card">
                                    <h3>{project.name} {project.surname}</h3>
										<div style={{display: 'flex', flexDirection: 'column'}}>
                                        Contact info: 
										<span style={{marginTop: '10px', marginBottom: '10px'}}>
                                            {project.phone ? (project.phone) : (<p style={{color: '#555'}}>No phone given</p>)}
										</span>
										<span style={{marginBottom: '10px'}}>
                                            {project.email ? (project.email) : (<p style={{color: '#555'}}>No email given</p>)}
                                        </span>
										</div>
                                    <p>Birth date: {project.dateOfBirth}</p>
                                    <button
                                        className="btn-primary"
                                        onClick={() => handleGenerateReport(project.id)}
                                        disabled={downloading === project.id}
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

export default VolunteerReports;
