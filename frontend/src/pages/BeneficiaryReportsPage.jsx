import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./Dashboard.css";

const BeneficiaryReportsPage = () => {
    const navigate = useNavigate();

    const [downloading, setDownloading] = useState(null);
    const [coveragePeriod, setCoveragePeriod] = useState("week");

    const downloadFile = async (url, filenamePrefix) => {
        setDownloading(filenamePrefix);
        try {
            const response = await api.get(url, {
                responseType: "blob",
            });

            const urlBlob = window.URL.createObjectURL(
                new Blob([response.data], { type: "application/pdf" })
            );

            const link = document.createElement("a");
            link.href = urlBlob;
            link.setAttribute("download", `${filenamePrefix}.pdf`);

            document.body.appendChild(link);
            link.click();
            link.remove();

            window.URL.revokeObjectURL(urlBlob);
        } catch (e) {
            alert("Failed to generate report.");
        } finally {
            setDownloading(null);
        }
    };

    const handleCoverage = () => {
        downloadFile(
            `/reports/beneficiary/coverage?period=${coveragePeriod}`,
            `coverage-report-${coveragePeriod}`
        );
    };

    const handleEfficiency = () => {
        downloadFile(
            `/reports/beneficiary/efficiency`,
            `efficiency-report`
        );
    };

    const handleImpact = () => {
        downloadFile(
            `/reports/beneficiary/impact`,
            `impact-report`
        );
    };

    return (
        <div className="projects-page">

            {/* HEADER */}
            <div className="projects-top">
                <div className="projects-top-left">
                    <h1>Reports</h1>
                    <p>Generate beneficiary system reports</p>
                </div>

                <button
                    className="btn-primary"
                    onClick={() => navigate("/manager")}
                >
                    ← Back to Home page
                </button>
            </div>

            {/* CARDS */}
            <div className="projects-container">
                <h2>Available reports</h2>

                <div className="projects-grid">

                    {/* COVERAGE */}
                    <div className="project-card">
                        <h3>Coverage report</h3>

                        <p>Select period:</p>
                        <select
                            value={coveragePeriod}
                            onChange={(e) => setCoveragePeriod(e.target.value)}
                        >
                            <option value="week">Past week</option>
                            <option value="month">Past month</option>
                            <option value="year">Past year</option>
                        </select>

                        <button
                            className="btn-primary"
                            onClick={handleCoverage}
                            disabled={downloading === "coverage"}
                        >
                            {downloading === "coverage"
                                ? "Generating..."
                                : "⬇ Generate coverage"}
                        </button>
                    </div>

                    {/* EFFICIENCY */}
                    <div className="project-card">
                        <h3>Efficiency report</h3>
                        <p>Distribution performance metrics</p>

                        <button
                            className="btn-primary"
                            onClick={handleEfficiency}
                            disabled={downloading === "efficiency"}
                        >
                            {downloading === "efficiency"
                                ? "Generating..."
                                : "⬇ Generate efficiency"}
                        </button>
                    </div>

                    {/* IMPACT */}
                    <div className="project-card">
                        <h3>Impact report</h3>
                        <p>Feedback and survey analysis</p>

                        <button
                            className="btn-primary"
                            onClick={handleImpact}
                            disabled={downloading === "impact"}
                        >
                            {downloading === "impact"
                                ? "Generating..."
                                : "⬇ Generate impact"}
                        </button>
                    </div>

                </div>
            </div>
        </div>
    );
};

export default BeneficiaryReportsPage;