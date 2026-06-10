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
			<div className='v-dashboard'>
			<div className='volunteer-dash-btns'>
                <div className="dashboard-card" style={{width: '450px'}}>
                    <h2>Assigned tasks</h2>
                    <p>See a full list of all tasks that have been assigned to you.</p>
                    <button className="btn-primary" 
							onClick={seeTasks}>
                        See assigned tasks
                    </button>
                </div>
                <div className="dashboard-card" style={{width: '450px'}}>
                    <h2>Rated tasks</h2>
                    <p>See a full list of all your tasks that have been rated.</p>
                    <button className="btn-primary" 
							onClick={seeTasks}>
                        See rated tasks
                    </button>
                </div>
                <div className="dashboard-card" style={{width: '450px'}}>
                    <h2>Training regiment</h2>
                    <p>See the training regiment that you have been assigned to.</p>
                    <button className="btn-primary" 
							onClick={seeTasks}>
                        See training regiment
                    </button>
                </div>
			</div>
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
