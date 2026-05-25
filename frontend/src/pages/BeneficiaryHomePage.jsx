import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import "./beneficiary.css"; // putanja do CSS fajla

export default function BeneficiaryHomePage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="ben-page">
      {/* ── Top bar ── */}
      <nav className="ben-topbar">
        <span className="ben-topbar-title">Home page - Beneficiary</span>
        <button className="btn-nav btn-nav-active" onClick={() => navigate("/beneficiary/profile")}>
          My Account
        </button>
        <button className="btn-nav">Request reassessment of needs</button>
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

        {/* Ukloni ili zakomentiraj ovaj blok dok ne implementiraš assessment */}
        {/* 
        <p className="ben-home-section-title">Eligibility status</p>
        <p className="ben-home-section-title">Current needs assessment:</p>
        <p className="ben-home-info">Date of assessment: {assessment?.date}</p>
        <p className="ben-home-info">
          Priority:{" "}
          <span className="priority-low">Low</span>/
          <span className="priority-medium">Medium</span>/
          <span className="priority-high">High</span>
        </p>
        <p className="ben-home-info">Category: {assessment?.category}</p>
        */}
      </div>
    </div>
  );
}
