import { useEffect, useState } from "react";
import api from "../api/axios";
import "./CampaignCoordinatorDashboard.css";
import SendDocumentModal from "../components/SendDocumentModal";
import { useNavigate } from "react-router-dom";

const formatDate = (dateValue) => {
    if (!dateValue) return "-";
    return new Date(dateValue).toLocaleDateString("en-GB");
};

const ReturnDocumentsPage = () => {
    const [documents, setDocuments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [selectedDocument, setSelectedDocument] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchDocuments = async () => {
            try {
                const res = await api.get("/return-documents");
                setDocuments(res.data);
            } catch (err) {
                console.log(err);
                setError("Failed to load documents.");
            } finally {
                setLoading(false);
            }
        };

        fetchDocuments();
    }, []);

    if (loading) {
        return (
            <div className="campaign-content">
                <h1 className="campaign-page-title" style={{ marginBottom: 34 }}>Return Documents</h1>
                <p className="donor-small-text">Loading documents...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="campaign-content">
                <h1 className="campaign-page-title" style={{ marginBottom: 34 }}>Return Documents</h1>
                <p className="donor-small-text">{error}</p>
            </div>
        );
    }

    return (
        <div className="campaign-content">
            <div className="campaign-header" style={{ marginTop: 20 }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 20 }}>
                    <h1 className="campaign-page-title" style={{ marginBottom: 34 }}>Return Documents</h1>
                    <button className="btn-primary" onClick={() => navigate(-1)}>
                        Back to dashboard
                    </button>
                </div>
            </div>

            <section className="campaign-card-grid">
                {documents.map((doc) => (
                    <div key={doc.id} className="campaign-card">
                        <div className="campaign-card-header">
                            <div className="campaign-card-title">{doc.documentType}</div>
                            <div className="campaign-status-pill">{doc.documentStatus}</div>
                        </div>
                        <div className="campaign-card-text">
                            Issued: {formatDate(doc.issuedDate)}
                        </div>
                        <div className="campaign-card-text">
                            {doc.content}
                        </div>
                        <div className="campaign-card-footer">
                            <span>Donation ID: {doc.donationId ?? "-"}</span>
                            <button className="btn-primary" onClick={() => setSelectedDocument(doc)}>
                                Send
                            </button>
                        </div>
                    </div>
                ))}
            </section>

            {documents.length === 0 && (
                <p className="donor-small-text">No documents found.</p>
            )}
            {selectedDocument && (
                <SendDocumentModal
                    document={selectedDocument}
                    onClose={() => setSelectedDocument(null)}
                    onSent={(id) => {
                        setDocuments((current) =>
                            current.map((d) => d.id === id ? { ...d, documentStatus: "SENT" } : d)
                        );
                        setSelectedDocument(null);
                    }}
                />
            )}

        </div>
    );
};

export default ReturnDocumentsPage;