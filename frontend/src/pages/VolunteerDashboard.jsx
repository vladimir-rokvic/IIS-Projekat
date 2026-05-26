import { useNavigate } from 'react-router-dom';
import './VolunteerDashboard.css';
import api from '../api/axios';
import { useState } from 'react';

const VolunteerDashboard = () => {
	const navigate = useNavigate();
	const user = JSON.parse(localStorage.getItem("user"));
	const [tasks, setTasks] = useState(null);

	const goToProfile = () => {
		navigate('/profile');
	}

	const seeTasks = async () => {
		try {
			const res = await api.get(`tasks/volunteer/${user?.id}`);
			//console.log(res.data);
			setTasks(res.data);
		} catch (err) {
			console.log(err);
		};
	};

	return (
		<div className='volunteer-dash'>
			<div className='top-bar'>
                <span className='user-name' onClick={goToProfile}>
                    {user?.name} {user?.surname}
                </span>
            </div>

			<h1>Organization name</h1>
			<h2>Organization bio</h2>
			<div className='volunteer-dash-btns'>
				<button onClick={seeTasks}>See Tasks</button>
				<button>See Ratings</button>
				<button>See Training</button>
			</div>
			<br />
			{tasks != null && tasks.map((task) => (
				<div className='task-card'>
					<h4>{task.name}</h4>
					<span>Start date: {task.startDate}</span>
					<span>End date: {task.endDate}</span>
				</div>
			))}
		</div>
	);
};

export default VolunteerDashboard;
