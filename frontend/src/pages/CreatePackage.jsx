import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./beneficiary.css";

const MAX_ITEMS = 3;

const emptyItem = () => ({ product: "", quantity: "" });

export default function CreatePackage() {
  const navigate = useNavigate();

  const [beneficiaryId, setBeneficiaryId] = useState("");
  const [items, setItems] = useState([emptyItem()]);
  const [error, setError] = useState("");

  const handleItemChange = (index, field, value) => {
    setItems((prev) =>
      prev.map((item, i) => (i === index ? { ...item, [field]: value } : item))
    );
  };

  const handleAddProduct = () => {
    if (items.length < MAX_ITEMS)
      setItems((prev) => [...prev, emptyItem()]);
  };

  const handleRemoveItem = (index) => {
    setItems((prev) => prev.filter((_, i) => i !== index));
  };

  const handleSave = async () => {
    setError("");

    if (!beneficiaryId) {
      setError("Please enter a Beneficiary ID.");
      return;
    }

    for (let i = 0; i < items.length; i++) {
      if (!items[i].product || !items[i].quantity) {
        setError(`Please fill in all fields for item ${i + 1}.`);
        return;
      }
    }

    const payload = {
      beneficiaryId: Number(beneficiaryId),
      items: items.map((item) => ({
        product: item.product,
        quantity: Number(item.quantity),
      })),
    };

    try {
      await api.post("/packages/create", payload);
      alert("Package created successfully!");
      navigate(-1);
    } catch (err) {
      const msg = err?.response?.data?.message || "Failed to create package.";
      setError(msg);
      alert(`Error: ${msg}`);
    }
  };

  return (
    <div className="ben-page">
      <nav className="ben-topbar">
        <span className="ben-topbar-title">Create a new package</span>
      </nav>

      <hr className="ben-divider" />

      <div className="ben-card pkg-card">
        {/* Back */}
        <button className="btn-nav pkg-back" onClick={() => navigate(-1)}>
          Back
        </button>

        <h2 className="pkg-title">Create a new package</h2>

        {/* Inner bordered box */}
        <div className="pkg-box">

          {/* Beneficiary */}
          <div className="pkg-row">
            <label className="pkg-label">Beneficiary</label>
            <input
              className="field-input pkg-input-wide"
              placeholder="Beneficiary"
              value={beneficiaryId ?? ""}
              onChange={(e) => setBeneficiaryId(e.target.value)}
            />
          </div>

          {/* Items */}
          {items.map((item, i) => (
            <div key={i} className="pkg-item-group">
              <div className="pkg-row">
                <label className="pkg-label">Product</label>
                <input
                  className="field-input pkg-input-mid"
                  placeholder="Product"
                  value={item.product ?? ""}
                  onChange={(e) => handleItemChange(i, "product", e.target.value)}
                />
                {items.length > 1 && (
                  <button
                    className="pkg-remove"
                    onClick={() => handleRemoveItem(i)}
                    title="Remove"
                  >✕</button>
                )}
              </div>
              <div className="pkg-row">
                <label className="pkg-label">Amount</label>
                <input
                  className="field-input pkg-input-sm"
                  placeholder="Amount"
                  type="number"
                  min="0"
                  step="0.1"
                  value={item.quantity ?? ""}
                  onChange={(e) => handleItemChange(i, "quantity", e.target.value)}
                />
              </div>
            </div>
          ))}

          {/* New Product button */}
          {items.length < MAX_ITEMS && (
            <button className="btn-nav pkg-new-product" onClick={handleAddProduct}>
              New Product
            </button>
          )}

          {error && <p className="dl-error" style={{ marginTop: "12px" }}>{error}</p>}

          {/* Save – bottom right */}
          <div className="pkg-save-row">
            <button className="btn-update pkg-save" onClick={handleSave}>
              Save
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}