import api from "../api/axios";
import { useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./TaskDetailsEdit.css";

const TaskDetailsEdit = () => {
	const {id} = useParams();
	const [task, setTask] = useState(null);
	const [volunteer, setVolunteer] = useState(null);
    const [skills, setSkills] = useState([]);
    const [skill, setSkill] = useState("");
    const navigate = useNavigate();

    const taskName = useRef();
    const description = useRef();
	
	useEffect(() => {
		const fetchTask = async () => {
			try {
				const res = await api.get(`/tasks/${id}`);
				setTask(res.data);
				console.log(res.data);
                setSkills(res.data.requiredSkills ? [...res.data.requiredSkills] : []);
                setVolunteer(res.data.volunteer);
			} catch (err){
				console.log(err);
			}
		};
		fetchTask();
	}, []);

	const addSkill = () => {
        if (!skill.trim()) return;
        setSkills([...skills, { name: skill, desc: "" }]);
        setSkill("");
    };

    const removeSkill = (index) => {
        setSkills(skills.filter((_, i) => i !== index));
    };

    const handleSave = async () => {
        const body = {
            name: taskName.current.value,
            description: description.current.value,
            volunteerId: volunteer ? volunteer.id : null,
            requiredSkills: skills,
        };
        try {
            await api.put(`/tasks/${id}`, body);
            navigate(-1);
        } catch (err) {
            console.log(err);
        }
    };

    if (!task) return <p>Loading...</p>;
    return (
        <div className="edit-task-page">
            <div className="header">
                <div className="header-left">
                    <input
                        ref={taskName}
                        type="text"
                        className="task-name-input"
                        defaultValue={task.name}
                    />
                    <span>Currently managed by:</span>
                </div>
                <div className="header-buttons">
                    <button className="btn-save" onClick={handleSave}>Save</button>
                </div>
            </div>

            <div className="volunteer-row">
                <div className="avatar-small" />
                {volunteer ? (
                    <>
                        <div className="volunteer-name-box">{volunteer.name} {volunteer.surname}</div>
                        <button
                            className="btn-change"
                            onClick={() => navigate('/coord/createTask/addVolunteer', { state: { taskId: id } })}
                        >Change</button>
                        <button
                            className="btn-details"
                            onClick={() => navigate(`/volunteer/details/${volunteer.id}`)}
                        >Details</button>
                    </>
                ) : (
                    <>
                        <span className="no-volunteer">No volunteer assigned</span>
                        <button
                            className="btn-change"
                            onClick={() => navigate('/coord/createTask/addVolunteer', { state: { taskId: id } })}
                        >Add</button>
                    </>
                )}
            </div>

            {volunteer && (
                <div className="volunteer-contact">
                    <span>{volunteer.phone || "No phone"}</span>
                    <span>{volunteer.email}</span>
                </div>
            )}

            <textarea
                ref={description}
                className="description-input"
                defaultValue={task.description}
            />

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
                <div className="skills-box">
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

export default TaskDetailsEdit;
