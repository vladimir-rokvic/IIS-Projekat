import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./beneficiary.css";

export default function BeneficiaryMyAccount() {
  const { user, setUser, logout } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    surname: "",
    phone: "",
    city: "",
    street: "",
    country: "",
    dateOfBirth: "",
    password: "",
    confirmPassword: "",
  });

  useEffect(() => {
    if (user) {
      setForm((prev) => ({
        ...prev,
        name: user.name || "",
        surname: user.surname || "",
        phone: user.phone || "",
        city: user.city || "",
        street: user.street || "",
        country: user.country || "",
        // Ako user.dateOfBirth dolazi kao "2000-03-15" već je u ispravnom formatu
        dateOfBirth: user.dateOfBirth || "",
      }));
    }
  }, [user]);

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleUpdate = async () => {
    if (form.password && form.password !== form.confirmPassword) {
      alert("Passwords do not match");
      return;
    }
    try {
      const payload = {
        name: form.name,
        surname: form.surname,
        phone: form.phone,
        city: form.city,
        street: form.street,
        country: form.country,
        ...(form.dateOfBirth ? { dateOfBirth: form.dateOfBirth } : {}),
        ...(form.password ? { password: form.password } : {}),
      };
      const res = await api.put(`/beneficiary/${user.id}`, payload);
      setUser(res.data);
      alert("Updated successfully");
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="ben-page">
      {/* ── Top bar ── */}
      <nav className="ben-topbar">
        <span className="ben-topbar-title">My Account - Beneficiary</span>
        <button className="btn-nav btn-nav-active" onClick={() => navigate("/beneficiary/profile")}>
          My Account
        </button>
        <button className="btn-nav">Available surveys</button>
        <button className="btn-nav">Aid History</button>
        <div className="ben-topbar-spacer" />
        <button className="btn-logout" onClick={logout}>Log Out</button>
      </nav>

      <hr className="ben-divider" />

      {/* ── Content card ── */}
      <div className="ben-card">
        {/* Header */}
        <div className="ben-account-header">
          <h2>My Account</h2>
          <button className="btn-docs">My Documents</button>
        </div>

        {/* Body: fields + notice */}
        <div className="ben-account-body">
          <div className="ben-account-fields">

            {/* First name */}
            <div className="field-row">
              <label className="field-label">
                First Name: <span>{form.name}</span>
              </label>
              <input
                className="field-input"
                name="name"
                value={form.name ?? ""}
                onChange={handleChange}
                placeholder="Enter new first name"
              />
            </div>

            {/* Last name */}
            <div className="field-row">
              <label className="field-label">
                Last Name: <span>{form.surname}</span>
              </label>
              <input
                className="field-input"
                name="surname"
                value={form.surname ?? ""}
                onChange={handleChange}
                placeholder="Enter new last name"
              />
            </div>

            {/* Date of birth */}
            <div className="field-row">
              <label className="field-label">Date Of Birth:</label>
              <input
                className="field-input field-input--date"
                type="date"
                name="dateOfBirth"
                value={form.dateOfBirth ?? ""}
                onChange={handleChange}
              />
            </div>

            {/* Phone */}
            <div className="field-row">
              <label className="field-label">
                Phone Number: <span>{form.phone}</span>
              </label>
              <input
                className="field-input"
                name="phone"
                value={form.phone ?? ""}
                onChange={handleChange}
                placeholder="Enter new phone number"
              />
            </div>

            {/* City */}
            <div className="field-row">
              <label className="field-label">
                City: <span>{form.city}</span>
              </label>
              <input
                className="field-input"
                name="city"
                value={form.city ?? ""}
                onChange={handleChange}
                placeholder="Enter new city"
              />
            </div>

            {/* Street */}
            <div className="field-row">
              <label className="field-label">
                Street: <span>{form.street}</span>
              </label>
              <input
                className="field-input"
                name="street"
                value={form.street ?? ""}
                onChange={handleChange}
                placeholder="Enter new street"
              />
            </div>

            {/* Country */}
            <div className="field-row">
              <label className="field-label">
                Country: <span>{form.country}</span>
              </label>
              <input
                className="field-input"
                name="country"
                value={form.country ?? ""}
                onChange={handleChange}
                placeholder="Enter new country"
              />
            </div>

            {/* Change password */}
            <div className="field-row">
              <label className="field-label">Change password</label>
              <input
                className="field-input"
                name="password"
                type="password"
                value={form.password ?? ""}
                onChange={handleChange}
                placeholder="New password"
                style={{ marginBottom: "6px" }}
              />
              <input
                className="field-input"
                name="confirmPassword"
                type="password"
                value={form.confirmPassword ?? ""}
                onChange={handleChange}
                placeholder="Confirm new password"
              />
            </div>

            <button className="btn-update" onClick={handleUpdate}>
              Update
            </button>
          </div>

          {/* Notice */}
          <p className="ben-notice">
            Please ensure your personal information is accurate and up to date.
            All details must match the information on your official Identity Card.
          </p>
        </div>
      </div>
    </div>
  );
}