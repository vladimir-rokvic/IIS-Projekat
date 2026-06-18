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

      <hr className="ben-divider" />

      {/* ── Content card ── */}
      <div className="ben-card">
        <div className="form-section">
          <div className="form-section-header">
            <h3>My Account</h3>

            <div className="btn-secondary" onClick={() =>navigate('/beneficiary/documents')}>
              My Documents
            </div>
          </div>

          <div className="form-row">
            <div className="form-field">
              <label>First Name</label>
              <input
                name="name"
                value={form.name ?? ""}
                onChange={handleChange}
                placeholder="Enter first name"
              />
            </div>

            <div className="form-field">
              <label>Last Name</label>
              <input
                name="surname"
                value={form.surname ?? ""}
                onChange={handleChange}
                placeholder="Enter last name"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-field">
              <label>Date Of Birth</label>
              <input
                type="date"
                name="dateOfBirth"
                value={form.dateOfBirth ?? ""}
                onChange={handleChange}
              />
            </div>

            <div className="form-field">
              <label>Phone Number</label>
              <input
                name="phone"
                value={form.phone ?? ""}
                onChange={handleChange}
                placeholder="Enter phone number"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-field">
              <label>City</label>
              <input
                name="city"
                value={form.city ?? ""}
                onChange={handleChange}
                placeholder="Enter city"
              />
            </div>

            <div className="form-field">
              <label>Street</label>
              <input
                name="street"
                value={form.street ?? ""}
                onChange={handleChange}
                placeholder="Enter street"
              />
            </div>

            <div className="form-field">
              <label>Country</label>
              <input
                name="country"
                value={form.country ?? ""}
                onChange={handleChange}
                placeholder="Enter country"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-field">
              <label>New Password</label>
              <input
                type="password"
                name="password"
                value={form.password ?? ""}
                onChange={handleChange}
                placeholder="Enter new password"
              />
            </div>

            <div className="form-field">
              <label>Confirm Password</label>
              <input
                type="password"
                name="confirmPassword"
                value={form.confirmPassword ?? ""}
                onChange={handleChange}
                placeholder="Confirm password"
              />
            </div>
          </div>

          <div className="form-actions">
            <button className="btn-primary" onClick={handleUpdate}>
              Update
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}