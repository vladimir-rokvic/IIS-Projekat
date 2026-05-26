import { useState, useRef, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./CreateTaskPage.css";

const CreateTaskPage = () => {
    const navigate = useNavigate();
    const taskName = useRef();
    const description = useRef();
    const startDate = useRef();
    const endDate = useRef();

    const [volunteer, setVolunteer] = useState(null);
    const [skills, setSkills] = useState([]);
    const [skill, setSkill] = useState("");

	const location = useLocation();
	const v_id = location.state?.v_id;

	useEffect(() => {
		if(!v_id) return;
		const fetchVolunteer = async () => {
			try {
				const res = await api.get(`/volunteer/${v_id}`);
				setVolunteer(res.data);
			} catch (err) {
				console.log(err);
			}
		};
		fetchVolunteer();
	}, [v_id]);

    const addSkill = () => {
        if (!skill.trim()) return;
        setSkills([...skills, { name: skill, desc: "" }]);
        setSkill("");
    };

    const removeSkill = (index) => {
        setSkills(skills.filter((_, i) => i !== index));
    };

    const handleSave = async () => {
		if(!v_id) return;
        const body = {
            name: taskName.current.value,
            description: description.current.value,
            startDate: startDate.current.value,
            endDate: endDate.current.value,
            volunteerId: v_id ? v_id:null,
            requiredSkills: skills,
        };
        console.log(body);
        try {
            await api.post("/tasks", body);
            navigate('/');
        } catch (err) {
            console.log(err);
        }
    };

	const handleAddVolunteer = () => {
		navigate('/coord/createTask/addVolunteer');
	}

    return (
        <div className="create-task-page">
            <div className="header">
                <input
                    ref={taskName}
                    type="text"
                    className="task-name-input"
                    placeholder="Task name"
                />
                <div className="header-buttons">
                    <button className="btn-save" onClick={handleSave}>Save</button>
                </div>
            </div>

            <div className="volunteer-section">
                <button className="btn-add-volunteer"
					onClick={handleAddVolunteer}>Add volunteer</button>
                <div className="volunteer-row">
                    <div className="avatar-small" />
				    {volunteer ? (
				            <span className="volunteer-name">{volunteer.name} {volunteer.surname}</span>
				    ) : (
				        <span className="volunteer-placeholder">No volunteer selected</span>
				    )}
                </div>
            </div>

            <textarea
                ref={description}
                className="description-input"
                placeholder="This here is a task description"
            />

            <div className="date-row">
                <div className="field">
                    <label>Start date</label>
                    <input ref={startDate} type="date" />
                </div>
                <div className="field">
                    <label>End date</label>
                    <input ref={endDate} type="date" />
                </div>
            </div>

            <div className="skills-section">
                <label className="group-label">Skills required</label>
                <div className="skills-input-row">
                    <input
                        type="text"
                        value={skill}
                        onChange={(e) => setSkill(e.target.value)}
                        placeholder="Enter a skill"
                    />
                    <button className="btn-save" onClick={addSkill}>Add</button>
                </div>
                <div className="skills-list">
                    {skills.map((s, index) => (
                        <div key={index} className="skill-item">
                            <span>{s.name}</span>
                            <button className="btn-remove" onClick={() => removeSkill(index)}>x</button>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default CreateTaskPage;
