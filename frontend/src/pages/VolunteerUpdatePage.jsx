import { useEffect, useState } from "react";
import api from "../api/axios";
import "./VolunteerUpdatePage.css";
import { useNavigate } from "react-router-dom";

const VolunteerUpdatePage = () => {
    const [user, setUser] = useState(null);
    const [skills, setSkills] = useState([]);
    const [newSkill, setNewSkill] = useState({ name: "", desc: "" });
    const [allSkillTypes, setAllSkillTypes] = useState([]);
    const [selectedSkillTypeId, setSelectedSkillTypeId] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const fetchVolunteer = async () => {
            try {
                const id = JSON.parse(localStorage.getItem("user")).id;
                const res = await api.get("/volunteer/" + id);
                setUser(res.data);
                if (res.data.skills) {
                    setSkills(res.data.skills);
                }
            } catch (err) {
                console.log(err);
            }
        };
        const fetchSkillTypes = async () => {
            try {
                const res = await api.get("/skill-types");
                setAllSkillTypes(res.data);
                if (res.data.length > 0) setSelectedSkillTypeId(String(res.data[0].id));
            } catch (err) {
                console.log(err);
            }
        };
        fetchVolunteer();
        fetchSkillTypes();
    }, []);

    const handleChange = (field, value) => {
        setUser((prev) => ({ ...prev, [field]: value }));
    };

    const handleAddressChange = (field, value) => {
        setUser((prev) => ({
            ...prev,
            address: { ...prev.address, [field]: value },
        }));
    };

    const calculateAge = (u) => {
        const today = new Date();
        const birthDay = new Date(u.dateOfBirth);
        return today.getFullYear() - birthDay.getFullYear();
    };

    const handleAddSkill = () => {
        if (!newSkill.name.trim()) return;
        setSkills((prev) => [...prev, { ...newSkill }]);
        setNewSkill({ name: "", desc: "" });
    };

    const handleRemoveSkill = (index) => {
        setSkills((prev) => prev.filter((_, i) => i !== index));
    };

    const handleAddSkillType = () => {
        const toAdd = allSkillTypes.find((st) => String(st.id) === selectedSkillTypeId);
        if (!toAdd) return;
        const alreadyAdded = user.skillTypes.some((st) => st.id === toAdd.id);
        if (alreadyAdded) return;
        setUser((prev) => ({
            ...prev,
            skillTypes: [...prev.skillTypes, toAdd],
        }));
    };

    const handleRemoveSkillType = (index) => {
        setUser((prev) => ({
            ...prev,
            skillTypes: prev.skillTypes.filter((_, i) => i !== index),
        }));
    };

    const handleSave = async () => {
        try {
            const id = JSON.parse(localStorage.getItem("user")).id;
            const body = { ...user, skills };
            await api.put("/volunteer/" + id, body);
            navigate("/volunteer/profile");
        } catch (err) {
            console.log(err);
        }
    };

    if (!user) return <p>Loading...</p>;

    return (
        <div className="volunteer-profile">
            <div className="header">
                <div className="header-buttons">
                    <button className="btn-edit" onClick={handleSave}>
                        Save
                    </button>
                </div>
            </div>

            <label className="addressLabelInre">Basic information</label>
            <div className="vp-basic-info">
                <h2 style={{ marginTop: "10px" }}>Name</h2>
                <div style={{ display: "flex", gap: "16px" }}>
                    <input
                        className="vp-input"
                        type="text"
                        value={user.name}
                        placeholder={user.name}
                        onChange={(e) => handleChange("name", e.target.value)}
                    />
                    <input
                        className="vp-input"
                        type="text"
                        value={user.surname}
                        placeholder={user.surname}
                        onChange={(e) => handleChange("surname", e.target.value)}
                    />
                </div>

                <h2>Contact information</h2>
                <div style={{ display: "flex", flexDirection: "column", gap: "8px" }}>
                    <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                        <span className="vp-input-label">Email:</span>
                        <input
                            className="vp-input"
                            type="text"
                            value={user.email}
                            placeholder={user.email}
                            onChange={(e) => handleChange("email", e.target.value)}
                        />
                    </div>
                    <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                        <span className="vp-input-label">Phone:</span>
                        <input
                            className="vp-input"
                            type="text"
                            value={user.phone}
                            placeholder={user.phone}
                            onChange={(e) => handleChange("phone", e.target.value)}
                        />
                    </div>
                </div>

                <h2>Address</h2>
                <div style={{ display: "flex", gap: "50px" }}>
                    {["country", "city", "street"].map((field) => (
                        <div key={field}>
                            <p style={{ fontSize: "1.3rem", textTransform: "capitalize" }}>
                                {field}
                            </p>
                            <input
                                className="vp-input"
                                type="text"
                                value={user.address[field]}
                                placeholder={user.address[field]}
                                onChange={(e) => handleAddressChange(field, e.target.value)}
                            />
                        </div>
                    ))}
                </div>

                <h2>Date of birth</h2>
                <input
                    className="vp-input"
                    type="date"
                    value={user.dateOfBirth}
                    onChange={(e) => handleChange("dateOfBirth", e.target.value)}
                />
                <p>Age: {calculateAge(user)}</p>

                <h2>Biography</h2>
                <textarea
                    className="vp-input vp-textarea"
                    value={user.bio || ""}
                    placeholder="Write a short bio..."
                    onChange={(e) => handleChange("bio", e.target.value)}
                />
            </div>

            <label className="addressLabelInre">Skill types</label>
            <div className="vp-add-skill">
                <select
                    className="vp-input"
                    value={selectedSkillTypeId}
                    onChange={(e) => setSelectedSkillTypeId(e.target.value)}
                >
                    {allSkillTypes.map((st) => (
                        <option key={st.id} value={String(st.id)}>
                            {st.name}
                        </option>
                    ))}
                </select>
                <button className="btn-add" onClick={handleAddSkillType}>
                    Add +
                </button>
            </div>
            <div className="back-to-shore">
                {user.skillTypes.length !== 0 ? (
                    user.skillTypes.map((skillType, index) => (
                        <div key={index} className="skill-card">
                            <h3>{skillType.name}</h3>
                            <p>{skillType.desc}</p>
                            <button
                                className="btn-remove"
                                onClick={() => handleRemoveSkillType(index)}
                            >
                                Remove
                            </button>
                        </div>
                    ))
                ) : (
                    <p style={{ color: "#555", fontSize: "1.3rem" }}>
                        No skill types set
                    </p>
                )}
            </div>

            <label className="addressLabelInre">Skills</label>
            <div className="vp-add-skill">
                <input
                    className="vp-input"
                    type="text"
                    placeholder="Skill name"
                    value={newSkill.name}
                    onChange={(e) =>
                        setNewSkill((prev) => ({ ...prev, name: e.target.value }))
                    }
                />
                <input
                    className="vp-input"
                    type="text"
                    placeholder="Short description"
                    value={newSkill.desc}
                    onChange={(e) =>
                        setNewSkill((prev) => ({ ...prev, desc: e.target.value }))
                    }
                />
                <button className="btn-add" onClick={handleAddSkill}>
                    Add +
                </button>
            </div>
            <div className="back-to-shore">
                {skills.length !== 0 ? (
                    skills.map((s, index) => (
                        <div key={index} className="skill-card">
                            <h3>{s.name}</h3>
                            <p>{s.desc}</p>
                            <button
                                className="btn-remove"
                                onClick={() => handleRemoveSkill(index)}
                            >
                                Remove
                            </button>
                        </div>
                    ))
                ) : (
                    <p style={{ color: "#555", fontSize: "1.3rem" }}>No skills set</p>
                )}
            </div>
        </div>
    );
};

export default VolunteerUpdatePage;
