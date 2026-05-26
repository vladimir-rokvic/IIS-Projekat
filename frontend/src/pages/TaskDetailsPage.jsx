import { useEffect, useState } from "react";
import api from "../api/axios";
import { useNavigate, useParams } from "react-router-dom";
import "./TaskDetailsPage.css";

const TaskDetailsPage = () => {
	const {id} = useParams();
	const [task, setTask] = useState(null);
	const navigate = useNavigate();

	useEffect(() => {
		const fetchTask = async () => {
			try {
				const res = await api.get(`/tasks/${id}`);
				setTask(res.data);
				console.log(res.data);
			} catch (err){
				console.log(err);
			}
		};
		fetchTask();
	}, []);

    if (!task) return <p>Loading...</p>;
	return (
        <div className="task-details-page">
            <div className="header">
                <div className="header-left">
                    <h2>{task.name}</h2>
                    <span>Currently managed by:</span>
                </div>
                <div className="header-buttons">
                    <button className="btn-edit" onClick={() => navigate(`/coord/tasksEdit/${id}`)}>Edit</button>
                </div>
            </div>

            <div className="volunteer-row">
                <div className="avatar-small" />
                {task.volunteer ? (
                    <>
                        <div className="volunteer-name-box">{task.volunteer.name} {task.volunteer.surname}</div>
                        <button
                            className="btn-details"
                            onClick={() => navigate(`/volunteer/details/${task.volunteer.id}`)}
                        >Details</button>
                    </>
                ) : (
                    <span className="no-volunteer">No volunteer assigned</span>
                )}
            </div>

            {task.volunteer && (
                <div className="volunteer-contact">
                    <span>{task.volunteer.phone || "No phone"}</span>
                    <span>{task.volunteer.email}</span>
                </div>
            )}

            <div className="description-box">
                {task.description}
            </div>

            <div className="skills-section">
                <label className="group-label">Skills required</label>
                <div className="skills-box">
                    {task.requiredSkills && task.requiredSkills.length > 0 ? (
                        task.requiredSkills.map((s, index) => (
                            <span key={index}>{s.name}</span>
                        ))
                    ) : (
                        <span>No skills required</span>
                    )}
                </div>
            </div>
        </div>
    );
};

export default TaskDetailsPage;
