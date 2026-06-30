import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const typeLabel = {
    WAREHOUSE: "Warehouse",
    BUILDING: "Building",
    VEHICLE: "Vehicle",
    TENT: "Tent",
    COMMUNITY_CENTER: "Community Center",
    SCHOOL: "School",
    STORAGE_UNIT: "Storage Unit",
};

const formatTime = (time) => {
    if (!time) return "";
    return time.slice(0, 5); // 08:00:00 -> 08:00
};

const DistributionLocationsPage = () => {
    const navigate = useNavigate();
    const { logout } = useAuth();

    const [locations, setLocations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        api.get("/distribution/locations")
            .then(res => setLocations(res.data))
            .catch(e => {
                if (e.response?.status === 401) {
                    logout();
                    navigate("/login");
                } else {
                    setError("Failed to load distribution locations.");
                }
            })
            .finally(() => setLoading(false));
    }, []);

    const handleDelete = async (id) => {
        const confirmed = window.confirm(
            "Are you sure you want to delete this distribution location?"
        );

        if (!confirmed) return;

        try {
            await api.delete(`/distribution/location/${id}`);

            setLocations(prev =>
                prev.filter(location => location.id !== id)
            );
        } catch (e) {
            alert("Failed to delete distribution location.");
        }
    };

    return (
        <div>
            <div className="projects-page">
                <div className="projects-top">
                    <div className="projects-top-left">
                        <h1>Distribution Locations</h1>
                        <p>
                            View and manage all distribution locations
                        </p>
                    </div>

                    <button
                        className="btn-primary"
                        onClick={() => navigate("/manager")}
                    >
                        ← Back to Home page
                    </button>
                </div>

                <div className="projects-container">
                    <h2>All distribution locations</h2>

                    {loading && (
                        <p className="loading-text">Loading...</p>
                    )}

                    {error && (
                        <p className="error-text">{error}</p>
                    )}

                    {!loading && !error && locations.length === 0 && (
                        <p className="loading-text">
                            No distribution locations yet.
                        </p>
                    )}

                    {!loading && !error && locations.length > 0 && (
                        <div className="projects-grid">
                            {locations.map(location => (
                                <div
                                    key={location.id}
                                    className="project-card"
                                >
                                    <h3>{location.name}</h3>

                                    <p>
                                        <strong>Type:</strong>{" "}
                                        {typeLabel[location.type] || location.type}
                                    </p>

                                    <p>
                                        <strong>Capacity:</strong>{" "}
                                        {location.capacity}
                                    </p>

                                    <p>
                                        <strong>Contact:</strong>{" "}
                                        {location.contactName}
                                    </p>

                                    <p>
                                        <strong>City:</strong>{" "}
                                        {location.city}
                                    </p>

                                    <p>
                                        <strong>Street:</strong>{" "}
                                        {location.street}
                                    </p>

                                    <p>
                                        <strong>Country:</strong>{" "}
                                        {location.country}
                                    </p>

                                    <p>
                                        <strong>Phone:</strong>{" "}
                                        {location.contactNumber}
                                    </p>

                                    <p>
                                        <strong>Working hours:</strong>{" "}
                                        {formatTime(location.workHoursBegin)} -{" "}
                                        {formatTime(location.workHoursEnd)}
                                    </p>

                                    <div
                                        style={{
                                            display: "flex",
                                            gap: "8px",
                                            marginTop: "8px",
                                        }}
                                    >
                                        <button
                                            className="btn-primary-red"
                                            onClick={() =>
                                                handleDelete(location.id)
                                            }
                                        >
                                            Delete</button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default DistributionLocationsPage;