import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const DistributionsPage = () => {
    const navigate = useNavigate();
    const { logout } = useAuth();

    const [distributions, setDistributions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        api.get("/distribution")
            .then(res => setDistributions(res.data))
            .catch(e => {
                if (e.response?.status === 401) {
                    logout();
                    navigate("/login");
                } else {
                    setError("Failed to load distributions.");
                }
            })
            .finally(() => setLoading(false));
    }, []);

    const handleDelete = async (id) => {
        const confirmed = window.confirm(
            "Are you sure you want to delete this distribution?"
        );

        if (!confirmed) return;

        try {
            await api.delete(`/distribution/${id}`);

            setDistributions(prev =>
                prev.filter(d => d.id !== id)
            );
        } catch (e) {
            alert("Failed to delete distribution.");
        }
    };

    const formatDate = (date) => {
        if (!date) return "";
        return date;
    };

    const getLocationText = (location) => {
        if (!location) return "Deleted location";
        return location.name;
    };

    return (
        <div className="projects-page">
            <div className="projects-top">
                <div className="projects-top-left">
                    <h1>Distributions</h1>
                    <p>View and manage all aid distributions</p>
                </div>

                <button
                    className="btn-primary"
                    onClick={() => navigate("/manager")}
                >
                    ← Back to Home page
                </button>
            </div>

            <div className="projects-container">
                <h2>All distributions</h2>

                {loading && (
                    <p className="loading-text">Loading...</p>
                )}

                {error && (
                    <p className="error-text">{error}</p>
                )}

                {!loading && !error && distributions.length === 0 && (
                    <p className="loading-text">
                        No distributions yet.
                    </p>
                )}

                {!loading && !error && distributions.length > 0 && (
                    <div className="projects-grid">
                        {distributions.map(d => (
                            <div
                                key={d.id}
                                className="project-card"
                            >
                                <h3>Distribution #{d.id}</h3>

                                <p>
                                    <strong>Date:</strong>{" "}
                                    {formatDate(d.scheduledDate)}
                                </p>

                                <p>
                                    <strong>Status:</strong>{" "}
                                    {d.status}
                                </p>

                                <p>
                                    <strong>Note:</strong>{" "}
                                    {d.note || "-"}
                                </p>

                                <p>
                                    <strong>Location:</strong>{" "}
                                    {getLocationText(d.location)}
                                </p>

                                <p>
                                    <strong>Volunteers:</strong>{" "}
                                    {d.volunteers?.length || 0}
                                </p>

                                <p>
                                    <strong>Packages:</strong>{" "}
                                    {d.packages?.length || 0}
                                </p>

                                <div
                                    style={{
                                        display: "flex",
                                        gap: "8px",
                                        marginTop: "8px",
                                    }}
                                >
                                    <button
                                        className="btn-primary"
                                        onClick={() =>
                                            navigate(
                                                `/manager/distribution/${d.id}`
                                            )
                                        }
                                    >
                                        View
                                    </button>

                                    <button
                                        className="btn-primary-red"
                                        onClick={() =>
                                            handleDelete(d.id)
                                        }
                                    >
                                        Delete
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default DistributionsPage;