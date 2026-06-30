import { useState } from "react";
import api from "../api/axios";

const ReportModal = ({ onClose }) => {
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [generating, setGenerating] = useState(false);
    const [error, setError] = useState("");

    const handleGenerate = async () => {
        if (!startDate || !endDate) {
            setError("Please select both dates.");
            return;
        }
        if (startDate > endDate) {
            setError("Start date must be before end date.");
            return;
        }

        setError("");
        setGenerating(true);

        try {
            const res = await api.get("/donations/donation-trends", {
                params: { startDate, endDate },
                responseType: "blob",
            });

            // Extract filename from headers if provided, otherwise default
            const disposition = res.headers["content-disposition"];
            let filename = `report_${startDate}_to_${endDate}.pdf`;
            if (disposition) {
                const match = disposition.match(/filename="?([^"]+)"?/);
                if (match) filename = match[1];
            }

            const url = window.URL.createObjectURL(new Blob([res.data]));
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", filename);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);

            onClose();
        } catch (err) {
            console.log(err);
            setError("Failed to generate report. Please try again.");
        } finally {
            setGenerating(false);
        }
    };

    return (
        <div className="campaign-modal-overlay" onClick={generating ? undefined : onClose}>
            <div className="campaign-modal campaign-form-card" style={{ width: "min(420px, 100%)" }} onClick={(e) => e.stopPropagation()}>
                <h2 className="campaign-form-title">Generate Report</h2>

                <div className="campaign-form-field" style={{ marginBottom: 14 }}>
                    <label>Start date</label>
                    <input
                        type="date"
                        value={startDate}
                        onChange={(e) => { setStartDate(e.target.value); setError(""); }}
                        disabled={generating}
                    />
                </div>

                <div className="campaign-form-field" style={{ marginBottom: 14 }}>
                    <label>End date</label>
                    <input
                        type="date"
                        value={endDate}
                        onChange={(e) => { setEndDate(e.target.value); setError(""); }}
                        disabled={generating}
                    />
                </div>

                {error && <p className="donor-small-text" style={{ color: "#b42318" }}>{error}</p>}

                <div className="campaign-form-actions">
                    <button className="campaign-form-secondary" onClick={onClose} disabled={generating}>
                        Cancel
                    </button>
                    <button className="campaign-form-primary" onClick={handleGenerate} disabled={generating}>
                        {generating ? "Generating..." : "Generate report"}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ReportModal;