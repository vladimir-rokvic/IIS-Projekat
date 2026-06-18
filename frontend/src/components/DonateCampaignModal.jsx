import { useState, useRef } from "react";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "../pages/Dashboard.css";

const DonateModal = ({ campaign, onClose, onDonated }) => {
    const { user } = useAuth();
    const [status, setStatus] = useState("form");
    const [error, setError] = useState("");
    const amountRef = useRef();

    const handleDonate = async () => {
        const amount = parseFloat(amountRef.current?.value);
        if (!amount || amount <= 0) {
            setError("Please enter a valid amount.");
            return;
        }
        setError("");
        setStatus("loading");

        try {
            await api.post("/donations", {
                amount,
                donationType: "MONETARY",
                paymentDate: new Date().toISOString().split("T")[0],
                donorId: user.id,
                campaignId: campaign.id,
                periodicity: "ONE_TIME",
            });
            setStatus("success");
            setTimeout(() => {
                onDonated();
                onClose();
            }, 2000);
        } catch (e) {
            setError(e.response?.data?.message || "Failed to process donation.");
            setStatus("error");
        }
    };

    return (
        <div style={{
            position: "fixed", inset: 0, background: "rgba(0,0,0,0.35)",
            display: "flex", alignItems: "center", justifyContent: "center", zIndex: 100
        }} onClick={onClose}>
            <div className="donor-panel" onClick={(e) => e.stopPropagation()} style={{ padding: 15}}>

                {status === "form" && (
                    <>
                        <h2 className="donor-page-title" style={{ fontSize: "1.1rem", marginBottom: 18 }}>
                            Donate to {campaign.name}
                        </h2>
                        <div style={{ display: "flex", alignItems: "center", gap: 16, marginBottom: 18 }}>
                            <span style={{ fontWeight: 700, minWidth: 70 }}>Amount</span>
                            <input
                                type="number"
                                placeholder="Amount (in American dollars)"
                                ref={amountRef}
                                className="donor-input"
                            />
                        </div>
                        {error && <p className="donor-error">{error}</p>}
                        <div style={{ display: "flex", justifyContent: "space-between", gap: 12, marginTop: 8 }}>
                            <button className="btn-secondary" onClick={onClose}>Cancel</button>
                            <button className="btn-primary" onClick={handleDonate}>
                                Donate
                            </button>
                        </div>
                    </>
                )}

                {status === "loading" && (
                    <p className="donor-page-title" style={{ fontSize: "1.1rem", textAlign: "center" }}>
                        Processing your donation of <strong>${amountRef.current?.value}</strong>...
                    </p>
                )}

                {status === "success" && (
                    <p className="donor-page-title" style={{ fontSize: "1.1rem", textAlign: "center" }}>
                        Your donation to <strong>{campaign.name}</strong> has been<br />successfully processed!
                    </p>
                )}

                {status === "error" && (
                    <>
                        <p className="donor-page-title" style={{ fontSize: "1.1rem", textAlign: "center", marginBottom: 20 }}>
                            Something went wrong. Please try again.
                        </p>
                        {error && <p className="donor-error">{error}</p>}
                        <div style={{ display: "flex", justifyContent: "space-between", gap: 12 }}>
                            <button className="btn-secondary" onClick={onClose}>Cancel</button>
                            <button className="btn-primary" onClick={() => setStatus("form")}>
                                Try Again
                            </button>
                        </div>
                    </>
                )}

            </div>
        </div>
    );
};

export default DonateModal;