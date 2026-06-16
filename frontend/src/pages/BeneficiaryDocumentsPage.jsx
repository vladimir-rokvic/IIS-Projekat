import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./beneficiaryDocuments.css";

const TYPE_LABELS = {
    LICNA_KARTA: "Identity Card",
    POTVRDA_O_NEZAPOSLENOSTI: "Unemployment Certificate",
    POTVRDA_O_PRIHODIMA: "Income Statement",
    MEDICINSKA_DOKUMENTACIJA: "Medical Documentation",
};

export default function BeneficiaryDocuments() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const fileInputRef = useRef(null);

    const [docs, setDocs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [loadError, setLoadError] = useState(false);

    const [selectedType, setSelectedType] = useState("");
    const [selectedFile, setSelectedFile] = useState(null);
    const [uploading, setUploading] = useState(false);

    const [toast, setToast] = useState({ visible: false, msg: "", type: "success" });
    const [deleteId, setDeleteId] = useState(null);
    const [dragover, setDragover] = useState(false);

    // Load documents 
    const loadDocuments = async () => {
        setLoading(true);
        setLoadError(false);
        try {
            const res = await api.get(`/dokumenti/korisnik/${user.id}`);
            setDocs(res.data);
        } catch {
            setLoadError(true);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (user?.id) loadDocuments();
    }, [user]);

    //Toast 
    const showToast = (msg, type = "success") => {
        setToast({ visible: true, msg, type });
        setTimeout(() => setToast((t) => ({ ...t, visible: false })), 3000);
    };

    //File selection 
    const handleFileChange = (file) => {
        if (file) setSelectedFile(file);
    };

    const handleDrop = (e) => {
        e.preventDefault();
        setDragover(false);
        const file = e.dataTransfer.files[0];
        if (file) setSelectedFile(file);
    };

    const handleView = async (id) => {
        try {
            const res = await api.get(`/dokumenti/${id}/fajl`, {
                responseType: "blob",
            });
            const url = URL.createObjectURL(res.data);
            window.open(url, "_blank");
        } catch {
            showToast("Failed to load document.", "error");
        }
    };

    // Upload
    const handleUpload = async () => {
        if (!selectedType) { showToast("Please select a document type.", "error"); return; }
        if (!selectedFile) { showToast("Please select a file.", "error"); return; }

        const formData = new FormData();
        formData.append("korisnikId", user.id);
        formData.append("tip", selectedType);
        formData.append("fajl", selectedFile);

        setUploading(true);
        try {
            await api.post("/dokumenti/upload", formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });
            showToast("Document uploaded successfully.");
            setSelectedType("");
            setSelectedFile(null);
            if (fileInputRef.current) fileInputRef.current.value = "";
            loadDocuments();
        } catch {
            showToast("Upload failed. Please try again.", "error");
        } finally {
            setUploading(false);
        }
    };

    //Delete 
    const handleDelete = async () => {
        try {
            await api.delete(`/dokumenti/${deleteId}`);
            showToast("Document deleted successfully.");
            setDeleteId(null);
            loadDocuments();
        } catch {
            showToast("Failed to delete document.", "error");
            setDeleteId(null);
        }
    };

    // Render 
    return (
        <div className="ben-page">
            <header className="dashboard-header">
                <h1>{user?.name} {user?.surname}</h1>
                <div className="user-info" onClick={() => { logout(); navigate("/login"); }} title="Logout">
                    <div className="avatar" />
                    <span>{user?.name} {user?.surname}</span>
                </div>
            </header>

            <hr className="ben-divider" />

            <div className="ben-card">

                {/* ── Document list ── */}
                <div className="form-section">
                    <div className="form-section-header">
                        <h3>
                            My Documents{" "}
                            <span className="badge-section">
                                {docs.length} file{docs.length !== 1 ? "s" : ""}
                            </span>
                        </h3>
                    </div>

                    <div className="doc-list">
                        {loading && <p className="loading-text">Loading documents...</p>}

                        {!loading && loadError && (
                            <div className="empty-state empty-state--error">
                                Failed to load documents.
                            </div>
                        )}

                        {!loading && !loadError && docs.length === 0 && (
                            <div className="empty-state">No documents uploaded yet.</div>
                        )}

                        {!loading && !loadError && docs.map((d) => (
                            <div className="doc-item" key={d.id}>
                                <div className="doc-item-left">
                                    <div>
                                        <div className="doc-name">{TYPE_LABELS[d.tip] || d.tip || ""}</div>
                                    </div>
                                </div>
                                <div className="doc-item-right">
                                    {d.url && (
                                        <button className="btn-icon" onClick={() => window.open(d.url, "_blank")}>
                                            View
                                        </button>
                                    )}
                                    <button className="btn-icon" onClick={() => handleView(d.id)}>
                                        View
                                    </button>

                                    <button className="btn-icon danger" onClick={() => setDeleteId(d.id)}>
                                        Delete
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* ── Upload ── */}
                <div className="form-section">
                    <div className="form-section-header">
                        <h3>Upload New Document</h3>
                    </div>

                    <div className="form-row">
                        <div className="form-field">
                            <label>Document Type</label>
                            <select value={selectedType} onChange={(e) => setSelectedType(e.target.value)}>
                                <option value="">— Select type —</option>
                                {Object.entries(TYPE_LABELS).map(([val, label]) => (
                                    <option key={val} value={val}>{label}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div
                        className={`upload-zone${dragover ? " dragover" : ""}`}
                        onClick={() => fileInputRef.current?.click()}
                        onDragOver={(e) => { e.preventDefault(); setDragover(true); }}
                        onDragLeave={() => setDragover(false)}
                        onDrop={handleDrop}
                    >
                        <p>Drag & drop a file here, or click to select</p>
                        <p className="upload-zone__hint">PDF, JPG, PNG supported</p>
                        <input
                            ref={fileInputRef}
                            type="file"
                            accept=".pdf,.jpg,.jpeg,.png"
                            style={{ display: "none" }}
                            onChange={(e) => handleFileChange(e.target.files[0])}
                        />
                    </div>

                    {selectedFile && (
                        <div className="selected-file">
                            <span>{selectedFile.name} ({(selectedFile.size / 1024).toFixed(1)} KB)</span>
                        </div>
                    )}

                    <div className="upload-btn-row">
                        <button className="btn-primary" onClick={handleUpload} disabled={uploading}>
                            {uploading ? "Uploading..." : "Upload"}
                        </button>
                    </div>
                </div>

            </div>

            {/* ── Delete modal ── */}
            {deleteId !== null && (
                <div className="modal-overlay">
                    <div className="modal-box">
                        <p>Are you sure you want to delete this document?</p>
                        <div className="modal-actions">
                            <button className="btn-secondary" onClick={() => setDeleteId(null)}>Cancel</button>
                            <button className="btn-primary btn-danger" onClick={handleDelete}>Delete</button>
                        </div>
                    </div>
                </div>
            )}

            {/* ── Toast ── */}
            <div className={`toast${toast.visible ? " show" : ""} ${toast.type}`}>
                {toast.msg}
            </div>
        </div>
    );
}