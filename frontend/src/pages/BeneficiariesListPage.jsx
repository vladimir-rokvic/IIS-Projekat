import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const aidTypeLabel = {
    SHELTER: "Shelter",
    FOOD: "Food",
    MEDICAL: "Medical",
    FINANCIAL: "Financial",
    OTHER: "Other",
};

const BeneficiariesListPage = () => {
    const navigate = useNavigate();
    const { logout } = useAuth();

    const [beneficiaries, setBeneficiaries] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        api.get("/beneficiary/details")
            .then(res => setBeneficiaries(res.data))
            .catch(e => {
                if (e.response?.status === 401) {
                    logout();
                    navigate("/login");
                } else {
                    setError("Failed to load beneficiaries.");
                }
            })
            .finally(() => setLoading(false));
    }, []);

    return (
        <div className="projects-page">

            {/* HEADER */}
            <div className="projects-top">

                <div className="projects-top-left">
                    <h1>Beneficiaries</h1>

                    <p>
                        View all registered beneficiaries
                    </p>
                </div>

                <button
                    className="btn-primary"
                    onClick={() => navigate("/manager")}
                >
                    ← Back to Home page
                </button>
            </div>

            {/* CONTENT */}
            <div className="projects-container">

                <h2>All beneficiaries</h2>

                {loading && (
                    <p className="loading-text">
                        Loading...
                    </p>
                )}

                {error && (
                    <p className="error-text">
                        {error}
                    </p>
                )}

                {!loading &&
                    !error &&
                    beneficiaries.length === 0 && (
                        <p className="loading-text">
                            No beneficiaries found.
                        </p>
                    )}

                {!loading &&
                    !error &&
                    beneficiaries.length > 0 && (
                        <div className="projects-grid">

                            {beneficiaries.map(b => (
                                <div
                                    key={b.id}
                                    className="project-card"
                                >

                                    <h3>
                                        {b.name} {b.surname}
                                    </h3>

                                    <p>
                                        <strong>ID:</strong>{" "}
                                        {b.id}
                                    </p>

                                    <p>
                                        <strong>Aid Type:</strong>{" "}
                                        {aidTypeLabel[b.aidType] || b.aidType}
                                    </p>

                                    <p>
                                        <strong>Eligible:</strong>{" "}
                                        {b.eligible ? "Yes" : "No"}
                                    </p>

                                    <p>
                                        <strong>Date of Birth:</strong>{" "}
                                        {b.dateOfBirth || "-"}
                                    </p>

                                    <p>
                                        <strong>Country:</strong>{" "}
                                        {b.country || "-"}
                                    </p>

                                    <p>
                                        <strong>City:</strong>{" "}
                                        {b.city || "-"}
                                    </p>

                                    <p>
                                        <strong>Street:</strong>{" "}
                                        {b.street || "-"}
                                    </p>
                                </div>
                            ))}
                        </div>
                    )}
            </div>
        </div>
    );
};

export default BeneficiariesListPage;