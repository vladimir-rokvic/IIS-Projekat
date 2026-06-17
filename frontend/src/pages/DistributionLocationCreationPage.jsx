import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./beneficiary.css";

const LOCATION_TYPES = [
  { value: "WAREHOUSE", label: "Warehouse" },
  { value: "BUILDING", label: "Building" },
  { value: "VEHICLE", label: "Vehicle" },
  { value: "TENT", label: "Tent" },
  { value: "COMMUNITY_CENTER", label: "Community Center" },
  { value: "SCHOOL", label: "School" },
  { value: "STORAGE_UNIT", label: "Storage Unit" },
];

const pad = (v) => String(v).padStart(2, "0");

export default function NewDistributionLocation() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    capacity: "",
    type: "",
    contactName: "",
    contactNumber: "",
    city: "",
    street: "",
    country: "",
    beginH: "",
    beginM: "",
    endH: "",
    endM: "",
  });

  const [error, setError] = useState("");

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  // Allow only 0-23 for hours, 0-59 for minutes
  const handleTimeChange = (e, max) => {
    const val = e.target.value.replace(/\D/g, "").slice(0, 2);
    if (val === "" || Number(val) <= max)
      setForm({ ...form, [e.target.name]: val });
  };

  const handleSave = async () => {
    setError("");

    if (!form.name || !form.capacity || !form.type || !form.contactName ||
      !form.contactNumber || !form.city || !form.street || !form.country ||
      !form.beginH || !form.beginM || !form.endH || !form.endM) {
      setError("Please fill in all fields.");
      return;
    }

    const payload = {
      name: form.name,
      capacity: Number(form.capacity),
      type: form.type,
      contactName: form.contactName,
      contactNumber: form.contactNumber,
      city: form.city,
      street: form.street,
      country: form.country,
      workHoursBegin: `${pad(form.beginH)}:${pad(form.beginM)}:00`,
      workHoursEnd: `${pad(form.endH)}:${pad(form.endM)}:00`,
    };

    try {
      await api.post("/distribution/location/create", payload);
      navigate(-1);
    } catch (err) {
      setError(err?.response?.data?.message || "Failed to create location.");
    }
  };

  return (
    <div className="create-page">
      <header >
        <div className="projects-top-left">
          <h1>New Distribution Location</h1>
        </div>
      </header>
      <br />
      <div className="form-section">
        <div className="form-section-header">
          <h3>Create Distribution Location</h3>
        </div>

        <div className="form-row">
          {/* Name */}
          <div className="form-field">
            <label>Name</label>
            <input
              name="name"
              value={form.name ?? ""}
              onChange={handleChange}
              placeholder="Name"
            />
          </div>

          {/* Capacity */}
          <div className="form-field">
            <label>Capacity</label>
            <input
              name="capacity"
              type="number"
              min="1"
              value={form.capacity ?? ""}
              onChange={handleChange}
              placeholder="Capacity"
            />
          </div>
        </div>

        <div className="form-row">
          {/* Type */}
          <div className="form-field">
            <label>Type</label>
            <select
              name="type"
              value={form.type ?? ""}
              onChange={handleChange}
            >
              <option value="" disabled>
                Type
              </option>

              {LOCATION_TYPES.map((t) => (
                <option key={t.value} value={t.value}>
                  {t.label}
                </option>
              ))}
            </select>
          </div>

          {/* Contact Name */}
          <div className="form-field">
            <label>Contact Name</label>
            <input
              name="contactName"
              value={form.contactName ?? ""}
              onChange={handleChange}
              placeholder="Contact Name"
            />
          </div>
        </div>

        <div className="form-row">
          {/* Contact Number */}
          <div className="form-field">
            <label>Contact Number</label>
            <input
              name="contactNumber"
              value={form.contactNumber ?? ""}
              onChange={handleChange}
              placeholder="Contact Number"
            />
          </div>

          {/* City */}
          <div className="form-field">
            <label>City</label>
            <input
              name="city"
              value={form.city ?? ""}
              onChange={handleChange}
              placeholder="City"
            />
          </div>
        </div>

        <div className="form-row">
          {/* Street */}
          <div className="form-field">
            <label>Street</label>
            <input
              name="street"
              value={form.street ?? ""}
              onChange={handleChange}
              placeholder="Street"
            />
          </div>

          {/* Country */}
          <div className="form-field">
            <label>Country</label>
            <input
              name="country"
              value={form.country ?? ""}
              onChange={handleChange}
              placeholder="Country"
            />
          </div>
        </div>

        {/* Working Hours */}
        <div className="form-row">
          <div className="form-field">
            <label>Working Hours</label>

            <div
              style={{
                display: "flex",
                alignItems: "center",
                gap: "8px",
              }}
            >
              <input
                style={{ maxWidth: "70px" }}
                name="beginH"
                placeholder="HH"
                value={form.beginH ?? ""}
                onChange={(e) => handleTimeChange(e, 23)}
              />

              <span>:</span>

              <input
                style={{ maxWidth: "70px" }}
                name="beginM"
                placeholder="MM"
                value={form.beginM ?? ""}
                onChange={(e) => handleTimeChange(e, 59)}
              />

              <span>–</span>

              <input
                style={{ maxWidth: "70px" }}
                name="endH"
                placeholder="HH"
                value={form.endH ?? ""}
                onChange={(e) => handleTimeChange(e, 23)}
              />

              <span>:</span>

              <input
                style={{ maxWidth: "70px" }}
                name="endM"
                placeholder="MM"
                value={form.endM ?? ""}
                onChange={(e) => handleTimeChange(e, 59)}
              />
            </div>
          </div>
        </div>

        {error && <p className="error-text">{error}</p>}

        <div className="form-actions">
          <button
            className="btn-cancel"
            onClick={() => navigate(-1)}
          >
            Cancel
          </button>

          <button
            className="btn-save"
            onClick={handleSave}
          >
            Save
          </button>
        </div>
      </div>
    </div>
  );
}