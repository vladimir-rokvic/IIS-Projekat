import "./beneficiary.css";
import LabelInput from "../components/LabelInput";
import { useState } from "react";
import api from "../api/axios";
import { useNavigate } from "react-router-dom";

export default function RegisterBeneficiaryPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: "",
    surname: "",
    dateOfBirth: "",
    street: "",
    city: "",
    country: "",
    phone: "",
    email: "",
    password: "",
    confirmPassword: "",
    type:""
  });

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const handleRegister = async () => {
    if (form.password !== form.confirmPassword) {
      alert("Passwords do not match");
      return;
    }

    try {
      const payload = {
        name: form.name,
        surname: form.surname,
        dateOfBirth: form.dateOfBirth,
        street: form.street,
        city: form.city,
        country: form.country,
        phone: form.phone,
        email: form.email,
        password: form.password,
        type: form.type
      };

      const res = await api.post("/beneficiary/register", payload);
      console.log(res.data);
      navigate('/login')
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="ben-page">
      <div className="ben-card">

        <div className="ben-account-header">
          <h2>Register Beneficiary</h2>
        </div>

        <div className="ben-account-body1">
          <div className="ben-account-fields">

            <div className="field-row">
              <label className="field-label">First Name</label>
              <input required
                className="field-input"
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="Enter first name"
              />
            </div>

            <div className="field-row">
              <label className="field-label">Last Name</label>
              <input required
                className="field-input"
                name="surname"
                value={form.surname}
                onChange={handleChange}
                placeholder="Enter last name"
              />
            </div>

            <div className="field-row">
              <label className="field-label">Date of Birth</label>
              <input required
                className="field-input field-input--date"
                type="date"
                name="dateOfBirth"
                value={form.dateOfBirth}
                onChange={handleChange}
              />
            </div>

            <div className="field-row">
              <label className="field-label">Street</label>
              <input required
                className="field-input"
                name="street"
                value={form.street}
                onChange={handleChange}
                placeholder="Enter street"
              />
            </div>

            <div className="field-row">
              <label className="field-label">City</label>
              <input required
                className="field-input"
                name="city"
                value={form.city}
                onChange={handleChange}
                placeholder="Enter city"
              />
            </div>

            <div className="field-row">
              <label className="field-label">Country</label>
              <input required
                className="field-input"
                name="country"
                value={form.country}
                onChange={handleChange}
                placeholder="Enter country"
              />
            </div>

            <div className="field-row">
              <label className="field-label">Phone</label>
              <input required
                className="field-input"
                name="phone"
                value={form.phone}
                onChange={handleChange}
                placeholder="Enter phone number"
              />
            </div>

            <div className="field-row">
              <label className="field-label">Email</label>
              <input required
                className="field-input"
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="Enter email"
              />
            </div>

            <div className="field-row">
              <label className="field-label">Aid Type</label>
              <select required
                className="field-input"
                name="type"
                value={form.type}
                onChange={handleChange}
                placeholder="Please select aid type"
              >
                <option value="0">FOOD</option>
                <option value="1">SHELTER</option>
                <option value="2">MEDICINE</option>
                <option value="3">CLOTHES</option>
                <option value="4">FINANCIAL</option>
              </select>
            </div>

            <div className="field-row">
              <label className="field-label">Password</label>
              <input required
                className="field-input"
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder="Enter password"
              />
              <input required
                className="field-input"
                type="password"
                name="confirmPassword"
                value={form.confirmPassword}
                onChange={handleChange}
                placeholder="Confirm password"
                style={{ marginTop: "6px" }}
              />
            </div>

            <button className="btn-update" onClick={handleRegister}>
              Register
            </button>

          </div>
        </div>
      </div>
    </div>
  );
}