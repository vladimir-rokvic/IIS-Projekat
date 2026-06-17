import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import { useAuth } from "../context/AuthContext";
import "./DistributionDetails.css";

const DistributionDetailsPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const { logout } = useAuth();

    const [distribution, setDistribution] = useState(null);
    const [volunteers, setVolunteers] = useState([]);

    const [form, setForm] = useState({
        scheduledDate: "",
        note: "",
    });

    const [selectedVolunteers, setSelectedVolunteers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    // FETCH DISTRIBUTION
    useEffect(() => {
        const fetchData = async () => {
            try {
                const [distRes, volRes] = await Promise.all([
                    api.get(`/distribution/${id}`),
                    api.get("/volunteer"),
                ]);

                const dist = distRes.data;

                setDistribution(dist);
                setVolunteers(volRes.data);

                setForm({
                    scheduledDate: dist.scheduledDate || "",
                    note: dist.note || "",
                });

                setSelectedVolunteers(
                    dist.volunteers?.map(v => v.id) || []
                );
            } catch (e) {
                if (e.response?.status === 401) {
                    logout();
                    navigate("/login");
                } else {
                    setError("Failed to load distribution.");
                }
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [id]);

    const handleChange = (e) => {
        setForm({
            ...form,
            [e.target.name]: e.target.value,
        });
    };

    const toggleVolunteer = (volId) => {
        setSelectedVolunteers(prev => {
            if (prev.includes(volId)) {
                return prev.filter(id => id !== volId);
            }

            if (prev.length >= 5) {
                setError("Maximum 5 volunteers allowed.");
                return prev;
            }

            return [...prev, volId];
        });
    };

    const handleSave = async () => {
        setError("");

        setSaving(true);
        try {
            const payload = {
                scheduledDate: form.scheduledDate,
                note: form.note,
                locationId: distribution.location.id,
                volunteerIds: selectedVolunteers,
            };

            await api.put(`/distribution/${id}`, payload);

            const res = await api.get(`/distribution/${id}`);
            setDistribution(res.data);
        } catch (e) {
            setError(
                e.response?.data?.message ||
                "Failed to update distribution."
            );
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <p className="loading-text">Loading...</p>;
    if (!distribution) return <p>Not found</p>;

    return (
        <div className="distribution-page">
            {/* HEADER */}
            <div className="projects-top">
                <div className="projects-top-left">
                    <h1>Distribution #{distribution.id}</h1>
                    <p>Status: {distribution.status}</p>
                </div>

                <button className="btn-primary" onClick={() => navigate(-1)}>
                    ← Back
                </button>
            </div>

            {/* FORM */}
            <div className="distribution-card">
                <div className="form-section-header">
                    <h3>Edit Distribution</h3>
                </div>

                <div className="distribution-main-grid">

                    {/* LEFT */}
                    <div className="dist-section">

                        <div className="dist-section-header">
                            <h3>Distribution Information</h3>
                        </div>

                        <div className="dist-field">
                            <label>Scheduled Date</label>

                            <input
                                type="date"
                                name="scheduledDate"
                                value={form.scheduledDate}
                                onChange={handleChange}
                            />
                        </div>

                        <div className="dist-field">
                            <label>Note</label>

                            <textarea
                                name="note"
                                value={form.note}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div className="dist-section">

                        <div className="dist-section-header">
                            <h3>Status</h3>
                        </div>

                        <div className="dist-field">

                            <label>Current Status</label>

                            <select
                                value={distribution.status}
                                onChange={async (e) => {

                                    const newStatus = e.target.value;

                                    try {

                                        await api.patch(
                                            `/distribution/${id}/status?status=${newStatus}`
                                        );

                                        setDistribution(prev => ({
                                            ...prev,
                                            status: newStatus,
                                        }));

                                    } catch (e) {

                                        alert(
                                            "Failed to update distribution status."
                                        );
                                    }
                                }}
                            >
                                <option value="PLANNED">
                                    Planned
                                </option>

                                <option value="COMPLETED">
                                    Completed
                                </option>

                                <option value="CANCELLED">
                                    Cancelled
                                </option>
                            </select>
                        </div>

                        <div
                            style={{
                                marginTop: "12px",
                            }}
                        >
                            <span
                                className={`distribution-status-badge status-${distribution.status}`}
                            >
                                Current status:  {distribution.status}
                            </span>
                        </div>
                    </div>

                    {/* RIGHT */}
                    <div className="dist-section">

                        <div className="dist-section-header">
                            <h3>Volunteers</h3>
                        </div>

                        <div className="dist-field volunteer-select">

                            <label>Add Volunteer</label>

                            <select
                                onChange={(e) =>
                                    toggleVolunteer(Number(e.target.value))
                                }
                                value=""
                            >
                                <option value="">
                                    Select volunteer
                                </option>

                                {volunteers.map(v => (
                                    <option key={v.id} value={v.id}>
                                        {v.name} {v.surname}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="selected-volunteers">

                            {selectedVolunteers.length === 0 && (
                                <p className="empty-text">
                                    No volunteers selected
                                </p>
                            )}

                            {selectedVolunteers.map(volunteerId => {

                                const v = volunteers.find(
                                    x => x.id === volunteerId
                                );

                                if (!v) return null;

                                return (
                                    <div
                                        key={volunteerId}
                                        className="volunteer-chip"
                                    >
                                        <span>
                                            {v.name} {v.surname}
                                        </span>

                                        <button
                                            className="btn-primary-red"
                                            onClick={() =>
                                                toggleVolunteer(volunteerId)
                                            }
                                        >
                                            Remove
                                        </button>
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                </div>

                <div className="packages-section">

                    <div className="packages-header">

                        <h2>Packages</h2>

                        <button
                            className="btn-primary"
                            onClick={() =>
                                navigate(
                                    `/manager/distribution/${id}/package/create`
                                )
                            }
                        >
                            + Add Package
                        </button>
                    </div>

                    {distribution.packages?.length === 0 && (
                        <p className="empty-text">
                            No packages added yet.
                        </p>
                    )}

                    <div className="packages-grid">

                        {distribution.packages?.map(pkg => (
                            <div
                                key={pkg.id}
                                className="package-card"
                            >

                                <div className="package-card-top">

                                    <div className="package-card-title">
                                        <h3>
                                            Package #{pkg.id}
                                        </h3>

                                        <p>
                                            Beneficiary:
                                            {" "}
                                            {pkg.beneficiaryName}
                                        </p>
                                    </div>

                                    <button
                                        className="btn-primary-red"
                                        onClick={async () => {

                                            const confirmed = window.confirm(
                                                "Are you sure you want to delete this package?"
                                            );

                                            if (!confirmed) return;

                                            try {

                                                await api.delete(
                                                    `/distribution/${id}/packages/${pkg.id}`
                                                );

                                                setDistribution(prev => ({
                                                    ...prev,
                                                    packages:
                                                        prev.packages.filter(
                                                            p => p.id !== pkg.id
                                                        ),
                                                }));

                                            } catch (e) {

                                                alert(
                                                    "Failed to delete package."
                                                );
                                            }
                                        }}
                                    >
                                        Delete
                                    </button>
                                </div>

                                <div className="package-items">

                                    {pkg.items?.map(item => (
                                        <div
                                            key={item.id}
                                            className="package-item"
                                        >
                                            <p>
                                                <strong>Product:</strong>
                                                {" "}
                                                {item.product}
                                            </p>

                                            <p>
                                                <strong>Quantity:</strong>
                                                {" "}
                                                {item.quantity}
                                                {" "}
                                                {item.unit}
                                            </p>

                                            {item.description && (
                                                <p>
                                                    <strong>Description:</strong>
                                                    {" "}
                                                    {item.description}
                                                </p>
                                            )}
                                        </div>
                                    ))}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                {error && (
                    <p className="error-text">{error}</p>
                )}

                {/* ACTIONS */}
                <div className="dist-actions">
                    <button
                        className="btn-primary"
                        onClick={handleSave}
                        disabled={saving}
                    >
                        {saving ? "Saving..." : "Save changes"}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default DistributionDetailsPage;