import { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./DonorDashboard.css";

const DonorProjectDetailPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    const [project, setProject] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    // Donation form state
    const [periodicity, setPeriodicity] = useState('ONE_TIME');
    const [wantsNotifications, setWantsNotifications] = useState(true);
    const [notificationChannel, setNotificationChannel] = useState('EMAIL');
    const [submitError, setSubmitError] = useState('');
    const [submitSuccess, setSubmitSuccess] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const amountRef = useRef();

    const [existingDonation, setExistingDonation] = useState(null);
    const [donorId, setDonorId] = useState(null);

    useEffect(() => {
        api.get(`/projekti/${id}`)
            .then(res => setProject(res.data))
            .catch(() => setError("Failed to load project."))
            .finally(() => setLoading(false));

        if (user?.id) {
            api.get(`/donations/donor/${user.id}/project/${id}`)
                .then(res => { if (res.data) setExistingDonation(true); })
                .catch(() => {});
        }
    }, [id]);

    const handleDonate = async () => {
        const amount = parseFloat(amountRef.current?.value);
        if (!amount || amount <= 0) {
            setSubmitError("Please enter a valid amount.");
            return;
        }
        setSubmitError('');
        setSubmitting(true);

        try {
            const donorId = user.id;

            await api.post("/donations", {
                amount,
                donationType: "MONETARY",
                paymentDate: new Date().toISOString().split('T')[0],
                periodicity,
                wantsNotifications,
                notificationFrequency: wantsNotifications ? "MONTHLY" : null,
                notificationChannel: wantsNotifications ? notificationChannel : null,
                donorId: user.id,
                projectId: parseInt(id),
            });
            setSubmitSuccess(true);
        } catch (e) {
            setSubmitError(e.response?.data?.message || "Failed to save donation.");
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) return <div className="donor-content" style={{ padding: 40 }}>Loading...</div>;
    if (!project) return <div className="donor-content" style={{ padding: 40 }}>Project not found.</div>;

    return (
        <div className="donor-content">
            {/* Success modal */}
            {submitSuccess && (
                <div style={{
                    position: "fixed", inset: 0, background: "rgba(0,0,0,0.35)",
                    display: "flex", alignItems: "center", justifyContent: "center", zIndex: 100
                }}>
                    <div style={{
                        background: "#fff", borderRadius: 12, padding: "32px 40px",
                        textAlign: "center", boxShadow: "0 8px 32px rgba(0,0,0,0.18)"
                    }}>
                        <p style={{ fontSize: "1.1rem", marginBottom: 20 }}>
                            Your donation has been<br />successfully saved!
                        </p>
                        <button className="donor-btn" style={{ background: "#369FBC", color: "#000", border: "1px solid #000" }}
                            onClick={() => navigate("/donor/projects")}>
                            Back to Projects
                        </button>
                    </div>
                </div>
            )}

            {/* Back button */}
            <button className="donor-btn" style={{ marginBottom: 24 }} onClick={() => navigate("/donor/projects")}>
                ← Back to Projects
            </button>

            <h1 className="donor-page-title" style={{ textAlign: "center", marginBottom: 6 }}>Project information</h1>
            <p style={{ textAlign: "center", color: "#4b5563", marginBottom: 28, fontSize: "0.95rem" }}>
                Here you can access all the information about the project
            </p>

            {error && <p style={{ color: "#cc0000", marginBottom: 12 }}>{error}</p>}

            {/* Basic information panel */}
            <div className="donor-panel" style={{ padding: "20px 24px", marginBottom: 20 }}>
                <h2 style={{ fontSize: "1.1rem", fontWeight: 700, marginBottom: 16 }}>Basic information</h2>
                <div style={{ display: "grid", gridTemplateColumns: "160px 1fr", gap: "8px 0", fontSize: "0.93rem" }}>
                    <span style={{ fontWeight: 700 }}>Project name</span>
                    <span>{project.naziv}</span>
                    <span style={{ fontWeight: 700 }}>Status</span>
                    <span>Accepted</span>
                    <span style={{ fontWeight: 700 }}>Start date</span>
                    <span>{project.rokPocetak}</span>
                    <span style={{ fontWeight: 700 }}>End date</span>
                    <span>{project.rokKraj}</span>
                    {project.geografskaLokacija && (
                        <>
                            <span style={{ fontWeight: 700 }}>Location</span>
                            <span>{project.geografskaLokacija}</span>
                        </>
                    )}
                    {project.ciljnaGrupa && (
                        <>
                            <span style={{ fontWeight: 700 }}>Target group</span>
                            <span>{project.ciljnaGrupa}</span>
                        </>
                    )}
                </div>
                <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 12 }}>
                    <button
                        className="donor-btn"
                        style={{ background: "#369FBC", color: "#000", border: "1px solid #000" }}
                        onClick={() => navigate(`/donor/projects/${id}/details`)}
                    >
                        Details
                    </button>
                </div>
            </div>

            {existingDonation && (
                <div style={{
                    background: "#e6f4f8", border: "1px solid #369FBC",
                    borderRadius: 8, padding: "12px 18px", marginBottom: 20,
                    fontSize: "0.93rem", color: "#1a5f72"
                }}>
                    ✓ You have already made a donation to this project.
                </div>
            )}

            {/* Donate panel */}
            <div className="donor-panel" style={{ padding: "20px 24px" }}>
                <h2 style={{ fontSize: "1.1rem", fontWeight: 700, marginBottom: 18 }}>Donate</h2>

                {/* Monthly / One time */}
                <div style={{ display: "flex", gap: 24, marginBottom: 16, fontSize: "0.93rem" }}>
                    <label style={{ display: "flex", alignItems: "center", gap: 6, cursor: "pointer" }}>
                        <input type="radio" name="periodicity" value="MONTHLY"
                            checked={periodicity === "MONTHLY"}
                            onChange={() => setPeriodicity("MONTHLY")} />
                        Monthly donation
                    </label>
                    <label style={{ display: "flex", alignItems: "center", gap: 6, cursor: "pointer" }}>
                        <input type="radio" name="periodicity" value="ONE_TIME"
                            checked={periodicity === "ONE_TIME"}
                            onChange={() => setPeriodicity("ONE_TIME")} />
                        Only once
                    </label>
                </div>

                {/* Amount */}
                <div style={{ display: "flex", alignItems: "center", gap: 16, marginBottom: 18, fontSize: "0.93rem" }}>
                    <span style={{ fontWeight: 700, minWidth: 70 }}>Amount</span>
                    <input
                        type="number"
                        placeholder="Amount (in American dollars)"
                        ref={amountRef}
                        style={{
                            padding: "8px 14px", borderRadius: 6, border: "1px solid #9e9e9e",
                            background: "#d9d9d9", fontSize: "0.9rem", width: 220,
                            color: "#000", outline: "none"
                        }}
                    />
                </div>

                {/* Wants notifications */}
                <div style={{ display: "flex", alignItems: "center", gap: 24, marginBottom: 16, fontSize: "0.93rem" }}>
                    <span style={{ fontWeight: 700 }}>Do you wish to know about where your donation went?</span>
                    <label style={{ display: "flex", alignItems: "center", gap: 6, cursor: "pointer" }}>
                        <input type="radio" name="notify" value="yes"
                            checked={wantsNotifications}
                            onChange={() => setWantsNotifications(true)} />
                        Yes
                    </label>
                    <label style={{ display: "flex", alignItems: "center", gap: 6, cursor: "pointer" }}>
                        <input type="radio" name="notify" value="no"
                            checked={!wantsNotifications}
                            onChange={() => setWantsNotifications(false)} />
                        No
                    </label>
                </div>

                {/* Notification channel */}
                {wantsNotifications && (
                    <div style={{ display: "flex", alignItems: "center", gap: 16, marginBottom: 18, fontSize: "0.93rem" }}>
                        <span style={{ fontWeight: 700 }}>Where would you like to receive notifications?</span>
                        <select
                            value={notificationChannel}
                            onChange={e => setNotificationChannel(e.target.value)}
                            style={{
                                padding: "8px 14px", borderRadius: 6, border: "1px solid #9e9e9e",
                                background: "#d9d9d9", fontSize: "0.9rem", color: "#000", outline: "none"
                            }}
                        >
                            <option value="EMAIL">Email</option>
                            <option value="APP">In-app notification</option>
                        </select>
                    </div>
                )}

                {submitError && <p style={{ color: "#cc0000", marginBottom: 10, fontSize: "0.88rem" }}>{submitError}</p>}

                {/* Actions */}
                <div style={{ display: "flex", justifyContent: "flex-end", gap: 12, marginTop: 8 }}>
                    <button className="donor-btn" onClick={() => navigate("/donor/projects")}>Cancel</button>
                    <button
                        className="donor-btn"
                        style={{ background: "#369FBC", color: "#000", border: "1px solid #000" }}
                        onClick={handleDonate}
                        disabled={submitting}
                    >
                        {submitting ? "Saving..." : "Save"}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default DonorProjectDetailPage;
