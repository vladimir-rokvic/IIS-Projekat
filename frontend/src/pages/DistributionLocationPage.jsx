import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./beneficiary.css";

const LOCATION_TYPES = [
  { value: "WAREHOUSE",        label: "Warehouse" },
  { value: "BUILDING",         label: "Building" },
  { value: "VEHICLE",          label: "Vehicle" },
  { value: "TENT",             label: "Tent" },
  { value: "COMMUNITY_CENTER", label: "Community Center" },
  { value: "SCHOOL",           label: "School" },
  { value: "STORAGE_UNIT",     label: "Storage Unit" },
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
      workHoursEnd:   `${pad(form.endH)}:${pad(form.endM)}:00`,
    };

    try {
      await api.post("/distribution/location/create", payload);
      navigate(-1);
    } catch (err) {
      setError(err?.response?.data?.message || "Failed to create location.");
    }
  };

  return (
    <div className="ben-page">
      <nav className="ben-topbar">
        
        <span className="ben-topbar-title">New Distribution Location</span>
        
      </nav>

      <hr className="ben-divider" />
      

      <div className="ben-card dl-card">
        

        <h2 className="dl-title">New Distribution location</h2>

        <div className="dl-form">
          {/* Name */}
          <div className="field-row">
            <label className="field-label">Name</label>
            <input className="field-input dl-input" name="name"
              value={form.name ?? ""} onChange={handleChange} placeholder="Name" />
          </div>

          {/* Capacity */}
          <div className="field-row">
            <label className="field-label">Capacity</label>
            <input className="field-input dl-input" name="capacity" type="number" min="1"
              value={form.capacity ?? ""} onChange={handleChange} placeholder="Capacity" />
          </div>

          {/* Type */}
          <div className="field-row">
            <label className="field-label">Type</label>
            <select className="field-input dl-input dl-select" name="type"
              value={form.type ?? ""} onChange={handleChange}>
              <option value="" disabled>Type</option>
              {LOCATION_TYPES.map((t) => (
                <option key={t.value} value={t.value}>{t.label}</option>
              ))}
            </select>
          </div>

          {/* Contact Name */}
          <div className="field-row">
            <label className="field-label">Contact Name</label>
            <input className="field-input dl-input" name="contactName"
              value={form.contactName ?? ""} onChange={handleChange} placeholder="Contact Name" />
          </div>

          {/* Contact Number */}
          <div className="field-row">
            <label className="field-label">Contact Number</label>
            <input className="field-input dl-input" name="contactNumber"
              value={form.contactNumber ?? ""} onChange={handleChange} placeholder="Contact Number" />
          </div>

          {/* City */}
          <div className="field-row">
            <label className="field-label">City</label>
            <input className="field-input dl-input" name="city"
              value={form.city ?? ""} onChange={handleChange} placeholder="City" />
          </div>

          {/* Street */}
          <div className="field-row">
            <label className="field-label">Street</label>
            <input className="field-input dl-input" name="street"
              value={form.street ?? ""} onChange={handleChange} placeholder="Street" />
          </div>

          {/* Country */}
          <div className="field-row">
            <label className="field-label">Country</label>
            <input className="field-input dl-input" name="country"
              value={form.country ?? ""} onChange={handleChange} placeholder="Country" />
          </div>

          {/* Working Hours */}
          <div className="field-row">
            <label className="field-label">Working Hours</label>
            <div className="dl-time-row">
              <input className="dl-time-box" name="beginH" placeholder="HH"
                value={form.beginH ?? ""} onChange={(e) => handleTimeChange(e, 23)} />
              <span className="dl-time-sep">:</span>
              <input className="dl-time-box" name="beginM" placeholder="MM"
                value={form.beginM ?? ""} onChange={(e) => handleTimeChange(e, 59)} />
              <span className="dl-time-dash">–</span>
              <input className="dl-time-box" name="endH" placeholder="HH"
                value={form.endH ?? ""} onChange={(e) => handleTimeChange(e, 23)} />
              <span className="dl-time-sep">:</span>
              <input className="dl-time-box" name="endM" placeholder="MM"
                value={form.endM ?? ""} onChange={(e) => handleTimeChange(e, 59)} />
            </div>
          </div>

          {error && <p className="dl-error">{error}</p>}

          {/* Save */}
          <button className="btn-update dl-save" onClick={handleSave}>
            Save
          </button>
        </div>
      </div>
    </div>
  );
}