import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import "./DonorDashboard.css";

const DonorProjectFullDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [project, setProject] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api.get(`/projekti/${id}`)
            .then(res => setProject(res.data))
            .finally(() => setLoading(false));
    }, [id]);

    if (loading) return <div className="donor-content" style={{ padding: 40 }}>Loading...</div>;
    if (!project) return <div className="donor-content" style={{ padding: 40 }}>Project not found.</div>;

    const field = (label, value) => value ? (
        <div style={{ marginBottom: 14 }}>
            <label style={{ display: "block", fontWeight: 700, fontSize: "0.88rem", marginBottom: 4 }}>{label}</label>
            <div style={{
                padding: "10px 14px", borderRadius: 6, border: "1px solid #9e9e9e",
                background: "#d9d9d9", fontSize: "0.9rem", color: "#000"
            }}>{value}</div>
        </div>
    ) : null;

    return (
        <div className="donor-content">
            <button className="donor-btn" style={{ marginBottom: 24 }} onClick={() => navigate(`/donor/projects/${id}`)}>
                ← Back
            </button>

            <h1 className="donor-page-title" style={{ textAlign: "center", marginBottom: 6 }}>Project details</h1>
            <p style={{ textAlign: "center", color: "#4b5563", marginBottom: 28, fontSize: "0.95rem" }}>
                Full information about the project
            </p>

            <div className="donor-panel" style={{ padding: "20px 24px", marginBottom: 20 }}>
                <h2 style={{ fontSize: "1.1rem", fontWeight: 700, marginBottom: 16 }}>Basic information</h2>
                {field("Project name", project.naziv)}
                {field("Description", project.opis)}
                {field("Project goals", project.ciljevi)}
                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14 }}>
                    {field("Start date", project.rokPocetak)}
                    {field("End date", project.rokKraj)}
                </div>
            </div>

            {(project.ciljnaGrupa || project.geografskaLokacija || project.izvoriFinansiranja) && (
                <div className="donor-panel" style={{ padding: "20px 24px" }}>
                    <h2 style={{ fontSize: "1.1rem", fontWeight: 700, marginBottom: 16 }}>Additional information</h2>
                    {field("Target group", project.ciljnaGrupa)}
                    {field("Geographic location", project.geografskaLokacija)}
                    {field("Funding sources", project.izvoriFinansiranja)}
                </div>
            )}
        </div>
    );
};

export default DonorProjectFullDetailPage;
