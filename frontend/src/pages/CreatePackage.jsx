import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/axios";
import "./Dashboard.css";

const MAX_ITEMS = 3;

const UNITS = [
    "kg",
    "rsd",
    "l",
    "pieces",
];

const emptyItem = () => ({
    product: "",
    quantity: "",
    unit: "pieces",
    description: "",
});

export default function CreatePackage() {
    const navigate = useNavigate();
    const { id } = useParams();

    const [beneficiaries, setBeneficiaries] = useState([]);

    const [beneficiaryId, setBeneficiaryId] = useState("");
    const [items, setItems] = useState([emptyItem()]);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        api.get("/beneficiary")
            .then(res => {
                setBeneficiaries(res.data);
            })
            .catch(() => {
                setError("Failed to load beneficiaries.");
            })
            .finally(() => {
                setLoading(false);
            });
    }, []);

    const handleItemChange = (index, field, value) => {
        setItems(prev =>
            prev.map((item, i) =>
                i === index
                    ? { ...item, [field]: value }
                    : item
            )
        );
    };

    const handleAddProduct = () => {
        if (items.length < MAX_ITEMS) {
            setItems(prev => [...prev, emptyItem()]);
        }
    };

    const handleRemoveItem = (index) => {
        setItems(prev =>
            prev.filter((_, i) => i !== index)
        );
    };

    const handleSave = async () => {
        setError("");

        if (!beneficiaryId) {
            setError("Please select a beneficiary.");
            return;
        }

        for (let i = 0; i < items.length; i++) {
            const item = items[i];

            if (
                !item.product ||
                !item.quantity ||
                !item.unit
            ) {
                setError(
                    `Please fill all required fields for item ${i + 1}.`
                );
                return;
            }
        }

        const payload = {
            beneficiaryId: Number(beneficiaryId),
            items: items.map(item => ({
                product: item.product,
                quantity: Number(item.quantity),
                unit: item.unit,
                description: item.description,
            })),
        };

        setSaving(true);

        try {
            await api.post(
                `/distribution/${id}/packages`,
                payload
            );

            navigate(-1);
        } catch (err) {
            setError(
                err?.response?.data?.message ||
                "Failed to create package."
            );
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="create-page">

            {/* TOP */}
            <div className="projects-top">
                <div className="projects-top-left">
                    <h1>Create Package</h1>
                    <p>
                        Create a package for aid distribution
                    </p>
                </div>

                <button
                    className="btn-primary"
                    onClick={() => navigate(-1)}
                >
                    ← Back
                </button>
            </div>

            {/* FORM */}
            <div className="form-section">

                <div className="form-section-header">
                    <h3>Package Information</h3>
                </div>

                {loading ? (
                    <p className="loading-text">
                        Loading...
                    </p>
                ) : (
                    <>
                        {/* BENEFICIARY */}
                        <div className="form-row">
                            <div className="form-field">
                                <label>
                                    Beneficiary *
                                </label>

                                <select
                                    value={beneficiaryId}
                                    onChange={(e) =>
                                        setBeneficiaryId(e.target.value)
                                    }
                                >
                                    <option value="">
                                        Select beneficiary
                                    </option>

                                    {beneficiaries.map(b => (
                                        <option
                                            key={b.id}
                                            value={b.id}
                                        >
                                            {b.name} {b.surname}
                                            {" "}
                                            ({b.aidType})
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        {/* ITEMS */}
                        {items.map((item, index) => (
                            <div
                                key={index}
                                className="form-section"
                                style={{
                                    marginTop: "16px",
                                }}
                            >
                                <div
                                    className="form-section-header"
                                    style={{
                                        display: "flex",
                                        justifyContent: "space-between",
                                        alignItems: "center",
                                    }}
                                >
                                    <h3>
                                        Product #{index + 1}
                                    </h3>

                                    {items.length > 1 && (
                                        <button
                                            className="btn-primary-red"
                                            onClick={() =>
                                                handleRemoveItem(index)
                                            }
                                        >
                                            Remove
                                        </button>
                                    )}
                                </div>

                                {/* PRODUCT */}
                                <div className="form-row">
                                    <div className="form-field">
                                        <label>
                                            Product *
                                        </label>

                                        <input
                                            type="text"
                                            value={item.product}
                                            onChange={(e) =>
                                                handleItemChange(
                                                    index,
                                                    "product",
                                                    e.target.value
                                                )
                                            }
                                            placeholder="Product name"
                                        />
                                    </div>
                                </div>

                                {/* QUANTITY + UNIT */}
                                <div className="form-row">

                                    <div className="form-field">
                                        <label>
                                            Quantity *
                                        </label>

                                        <input
                                            type="number"
                                            min="0"
                                            step="0.1"
                                            value={item.quantity}
                                            onChange={(e) =>
                                                handleItemChange(
                                                    index,
                                                    "quantity",
                                                    e.target.value
                                                )
                                            }
                                        />
                                    </div>

                                    <div className="form-field">
                                        <label>
                                            Unit *
                                        </label>

                                        <select
                                            value={item.unit}
                                            onChange={(e) =>
                                                handleItemChange(
                                                    index,
                                                    "unit",
                                                    e.target.value
                                                )
                                            }
                                        >
                                            {UNITS.map(unit => (
                                                <option
                                                    key={unit}
                                                    value={unit}
                                                >
                                                    {unit}
                                                </option>
                                            ))}
                                        </select>
                                    </div>
                                </div>

                                {/* DESCRIPTION */}
                                <div className="form-row">
                                    <div className="form-field">
                                        <label>
                                            Description
                                        </label>

                                        <textarea
                                            value={item.description}
                                            onChange={(e) =>
                                                handleItemChange(
                                                    index,
                                                    "description",
                                                    e.target.value
                                                )
                                            }
                                            placeholder="Optional description..."
                                        />
                                    </div>
                                </div>
                            </div>
                        ))}

                        {/* ADD PRODUCT */}
                        {items.length < MAX_ITEMS && (
                            <button
                                className="btn-primary"
                                style={{
                                    marginTop: "16px",
                                }}
                                onClick={handleAddProduct}
                            >
                                + Add Product
                            </button>
                        )}

                        {/* ERROR */}
                        {error && (
                            <p className="error-text">
                                {error}
                            </p>
                        )}

                        {/* ACTIONS */}
                        <div className="form-actions">

                            <button
                                className="btn-primary"
                                onClick={handleSave}
                                disabled={saving}
                            >
                                {saving
                                    ? "Saving..."
                                    : "Create Package"}
                            </button>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}