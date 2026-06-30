import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";
import api from "../api/axios";

export default function BeneficiaryHomePage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const [beneficiary, setBeneficiary] = useState(null);

  useEffect(() => {
    const fetchBeneficiary = async () => {
      try {
        const response = await api.get(`/beneficiary/${user.id}`);
        setBeneficiary(response.data);
      } catch (err) {
        console.error(err);
      }
    };

    if (user?.id) {
      fetchBeneficiary();
    }
  }, [user]);

  const handleReassessmentRequest = async () => {
    try {
      await api.post("/beneficiary/reassessment-requests/create", {
        beneficiaryId: user.id,
      });

      alert("Reassessment request created successfully");
    } catch (err) {
      console.error(err);
      alert(err.response.data);
    }
  };

  return (
    <div className="dashboard-page">
            <header className="dashboard-header">
                <div>
                    <h1>{user?.name} {user?.surname}</h1>
                    <p style={{ fontSize: '0.9rem', color: '#555', marginTop: 2 }}>Aid type:{" "}
          {beneficiary?.type?.replaceAll("_", " ")} | Status:{" "}
          {beneficiary?.eligible ? "Eligible" : "Not eligible"}</p>
                </div>
                <div className="user-info" onClick={() => { logout(); navigate('/login'); }} title="Logout">
                    <div className="avatar" />
                    <span>{user?.name} {user?.surname}</span>
                </div>
            </header>

            <main className="dashboard-main">
                <div className="dashboard-card">
                    <h2>Account Management</h2>
                    <p>Update your information and upload new documents.</p>
                    <button className="btn-primary" onClick={() => navigate("/beneficiary/profile")}>
                        My Account
                    </button>
                </div>

                <div className="dashboard-card" style={{ marginTop: 16 }}>
                    <h2>Reassessment Request</h2>
                    <p>Send a reassessment request to one of our managers.</p>
                    <button className="btn-primary" onClick={handleReassessmentRequest}>
                        Send a reassessment request
                    </button>
                </div>

                <div className="dashboard-card" style={{ marginTop: 16 }}>
                    <h2>Aid History</h2>
                    <p>View every package you have received so far.</p>
                    <button className="btn-primary" onClick={()=>navigate("/beneficiary/history")}>
                        Aid History
                    </button>
                </div>

            </main>
        </div>
  );
}