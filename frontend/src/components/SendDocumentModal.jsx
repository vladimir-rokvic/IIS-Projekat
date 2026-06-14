import { useState } from "react";
import api from "../api/axios";


const SendDocumentModal = ({ document, onClose, onSent }) => {
    const [status, setStatus] = useState("confirm"); // "confirm" | "loading" | "success" | "error"

    const handleSend = async () => {
        setStatus("loading");
        try {
            await api.post(`/return-documents/${document.id}/send`);
            setStatus("success");
            setTimeout(() => {
                onSent(document.id);
                onClose();
            }, 1500);
        } catch (err) {
            console.log(err);
            setStatus("error");
        }
    };

    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-box" onClick={(e) => e.stopPropagation()}>

                {status === "confirm" && (
                    <>
                        <h2>Send Document</h2>
                        <p>Are you sure you want to send this <strong>{document.documentType}</strong>?</p>
                        <div style={{ display: "flex", justifyContent: "space-between", marginTop: 24 }}>
                            <button className="btn-secondary" onClick={onClose} style={{ width: "20%" }}>Cancel</button>
                            <button className="btn-primary" onClick={handleSend} style={{ width: "20%" }}>Send</button>
                        </div>
                    </>
                )}

                {status === "loading" && (
                    <>
                        <h2>Sending...</h2>
                        <p>Please wait while the document is being sent.</p>
                    </>
                )}

                {status === "success" && (
                    <>
                        <h2>Document Sent!</h2>
                        <p>The document was sent successfully. This window will close shortly.</p>
                    </>
                )}

                {status === "error" && (
                    <>
                        <h2>Something went wrong</h2>
                        <p>The document could not be sent. Please try again.</p>
                        <div style={{ display: "flex", justifyContent: "space-between", marginTop: 24 }}>
                            <button className="btn-secondary" onClick={onClose}>Cancel</button>
                            <button className="btn-primary" onClick={handleSend}>Try Again</button>
                        </div>
                    </>
                )}

            </div>
        </div>
    );
};

export default SendDocumentModal;