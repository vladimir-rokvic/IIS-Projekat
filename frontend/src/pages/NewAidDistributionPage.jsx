import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./Dashboard.css";

const NewAidDistributionPage = () => {
    const navigate = useNavigate();
    const { logout } = useAuth();

    const [locations, setLocations] = useState([]);

    const [form, setForm] = useState({
        scheduledDate: "",
        note: "",
        locationId: "",
    });

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        api.get("/distribution/locations")
            .then(res => setLocations(res.data))
            .catch(e => {
                if (e.response?.status === 401) {
                    logout();
                    navigate("/login");
                } else {
                    setError("Failed to load locations.");
                }
            })
            .finally(() => setLoading(false));
    }, []);

    const handleChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value,
        });
    };

    const handleSave = async () => {
        setError("");

        if (!form.scheduledDate || !form.locationId) {
            setError("Please fill in all required fields.");
            return;
        }

        const selectedDate = new Date(form.scheduledDate);
        const today = new Date();

        today.setHours(0, 0, 0, 0);

        if (selectedDate <= today) {
            setError("Scheduled date must be in the future.");
            return;
        }

        setSaving(true);

        try {
            const payload = {
                scheduledDate: form.scheduledDate,
                note: form.note,
                locationId: Number(form.locationId),
                volunteerIds: [],
            };

            const res = await api.post("/distribution", payload);
            console.log(res.data);
            navigate(`/manager/distribution/${res.data.id}`);
        } catch (e) {
            setError(
                e.response?.data?.message ||
                "Failed to create aid distribution."
            );
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="create-page">
            <div className="projects-top">
                <div className="projects-top-left">
                    <h1>New Aid Distribution</h1>
                    <p>
                        Create a new aid distribution event
                    </p>
                </div>

                <button
                    className="btn-primary"
                    onClick={() => navigate(-1)}
                >
                    ← Back
                </button>
            </div>

            <div className="form-section">
                <div className="form-section-header">
                    <h3>Distribution Information</h3>
                </div>

                {loading ? (
                    <p className="loading-text">Loading...</p>
                ) : (
                    <>
                        {/* Date */}
                        <div className="form-row">
                            <div className="form-field">
                                <label>Scheduled Date *</label>

                                <input
                                    type="date"
                                    name="scheduledDate"
                                    value={form.scheduledDate}
                                    onChange={handleChange}
                                />
                            </div>
                        </div>

                        {/* Location */}
                        <div className="form-row">
                            <div className="form-field">
                                <label>Distribution Location *</label>

                                <select
                                    name="locationId"
                                    value={form.locationId}
                                    onChange={handleChange}
                                >
                                    <option value="">
                                        Select location
                                    </option>

                                    {locations.map(location => (
                                        <option
                                            key={location.id}
                                            value={location.id}
                                        >
                                            {location.name} ({location.city}, {location.street}, {location.country}
                                            )

                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        {/* Note */}
                        <div className="form-row">
                            <div className="form-field">
                                <label>Note</label>

                                <textarea
                                    name="note"
                                    value={form.note}
                                    onChange={handleChange}
                                    placeholder="Optional note..."
                                />
                            </div>
                        </div>

                        {error && (
                            <p className="error-text">
                                {error}
                            </p>
                        )}

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
                                disabled={saving}
                            >
                                {saving ? "Creating..." : "Create"}
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default NewAidDistributionPage;