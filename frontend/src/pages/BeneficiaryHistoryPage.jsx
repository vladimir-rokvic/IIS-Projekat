import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const BeneficiaryHistoryPage = () => {

    const navigate = useNavigate();
    const { user, logout } = useAuth();

    const [history, setHistory] = useState([]);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {

        api.get(`/packages/history/${user.id}`)
            .then(res => {
                setHistory(res.data);
            })
            .catch(e => {

                if (e.response?.status === 401) {

                    logout();
                    navigate("/login");

                } else {

                    setError(
                        "Failed to load aid history."
                    );
                }
            })
            .finally(() => {
                setLoading(false);
            });

    }, []);

    return (
        <div className="projects-page">

            {/* HEADER */}
            <div className="projects-top">

                <div className="projects-top-left">

                    <h1>
                        Aid History
                    </h1>

                    <p>
                        View previously received aid packages
                    </p>
                </div>

                <button
                    className="btn-primary"
                    onClick={() => navigate("/beneficiary")}
                >
                    ← Back to Home page
                </button>
            </div>

            {/* CONTENT */}
            <div className="projects-container">

                <h2>
                    Received Packages
                </h2>

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
                    history.length === 0 && (
                        <p className="loading-text">
                            No aid history found.
                        </p>
                    )}

                {!loading &&
                    !error &&
                    history.length > 0 && (

                        <div className="projects-grid">

                            {history.map((pkg, index) => (

                                <div
                                    key={index}
                                    className="project-card"
                                >

                                    <h3>
                                        Package #{index + 1}
                                    </h3>

                                    <p>
                                        <strong>Beneficiary:</strong>{" "}
                                        {pkg.beneficiaryName}
                                    </p>

                                    <p>
                                        <strong>Date Received:</strong>{" "}
                                        {pkg.dateReceived}
                                    </p>

                                    <div
                                        style={{
                                            marginTop: "14px",
                                        }}
                                    >

                                        <strong>
                                            Items:
                                        </strong>

                                        <div
                                            style={{
                                                display: "flex",
                                                flexDirection: "column",
                                                gap: "10px",
                                                marginTop: "10px",
                                            }}
                                        >

                                            {pkg.items?.map(item => (

                                                <div
                                                    key={item.id}
                                                    style={{
                                                        border: "1px solid #ddd",
                                                        borderRadius: "10px",
                                                        padding: "10px",
                                                        background: "#f8f8f8",
                                                    }}
                                                >

                                                    <p>
                                                        <strong>Product:</strong>{" "}
                                                        {item.product}
                                                    </p>

                                                    <p>
                                                        <strong>Quantity:</strong>{" "}
                                                        {item.quantity} {item.unit}
                                                    </p>

                                                    {item.description && (
                                                        <p>
                                                            <strong>Description:</strong>{" "}
                                                            {item.description}
                                                        </p>
                                                    )}
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
            </div>
        </div>
    );
};

export default BeneficiaryHistoryPage;