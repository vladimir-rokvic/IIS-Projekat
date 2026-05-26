import { useEffect, useState} from "react";
import api from "../api/axios";
import "./TasksPage.css";
import { useNavigate } from "react-router-dom";

const TasksPage = () => {
	const [tasks, setTasks] = useState([]);
	const navigate = useNavigate();

	useEffect(() => {
		const fetchTasks = async () => {
			try {
				const res = await api.get('/tasks');
				setTasks(res.data);
			} catch(err) {
				console.log(err);
			}
		};
		fetchTasks();
	}, []);

    return (
        <div className="tasks-page">
            <div className="tasks-header">
                <div className="tasks-header-left">
                    <h1>Tasks</h1>
                    <span>Manage tasks</span>
                </div>
                <div className="tasks-header-buttons">
                    <button className="btn-create" onClick={() => navigate('/coord/createTask')}>Create task</button>
                </div>
            </div>

            <div className="tasks-container">
                <h3>All Tasks</h3>
                <div className="tasks-grid">
                    {tasks.map((task) => (
                        <div key={task.id} className="task-card">
                            <h4>{task.name}</h4>
                            {task.volunteer ? (
                                <>
                                    <span>Current Volunteer:</span>
                                    <span>{task.volunteer.name} {task.volunteer.surname}</span>
                                    <span>{task.volunteer.phone}</span>
                                    <span>{task.volunteer.email}</span>
                                </>
                            ) : (
                                <span>No volunteer assigned</span>
                            )}
                            <button
                                className="btn-details"
                                onClick={() => navigate(`/coord/tasks/${task.id}`)}
                            >Details</button>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default TasksPage;
