import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import "./beneficiary.css"; // putanja do CSS fajla
import api from "../api/axios";


export default function BeneficiaryHomePage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const handleReassessmentRequest = async () => {
  try {
    await api.post(
      "/beneficiary/reassessment-requests/create",
      {
        beneficiaryId: user.id
      }
    );

    alert("Reassessment request created successfully");
  } catch (err) {
    console.error(err);

    alert(err.response.data);

  }
};

  return (
    <div className="ben-page">
      {/* ── Top bar ── */}
      <nav className="ben-topbar">
        <span className="ben-topbar-title">Home page - Beneficiary</span>
        <button className="btn-nav btn-nav-active" onClick={() => navigate("/beneficiary/profile")}>
          My Account
        </button>
        <button className="btn-nav" onClick={handleReassessmentRequest}>Request reassessment of needs</button>
        <button className="btn-nav">Available surveys</button>
        <button className="btn-nav">Aid History</button>
        <div className="ben-topbar-spacer" />
        <button className="btn-logout" onClick={logout}>Log Out</button>
      </nav>

      <hr className="ben-divider" />

      {/* ── Content card ── */}
      <div className="ben-card">
        <h1 className="ben-home-name">
          {user?.name} {user?.surname}
        </h1>

      </div>
    </div>
  );
}
