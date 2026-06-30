import { useNavigate } from 'react-router-dom';
import './VolunteerDashboard.css';
import api from '../api/axios';
import { useEffect, useState } from 'react';
import * as Slider from '@radix-ui/react-slider';

const VolunteerDashboard = () => {
	const navigate = useNavigate();
	const user = JSON.parse(localStorage.getItem("user"));
	const [tasks, setTasks] = useState(null);
	const [ratings, setRatings] = useState(null)
	const [regiments, setRegiments] = useState(null);

	const DAYS = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];

	const DAY_LABELS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];

	const [availability, setAvailabillity] = useState(
		DAYS.reduce((acc, day) => ({
			...acc,
			[day]: {enabled: false, range: [7, 22]}
		}), {})
	);

	useEffect(() => {
		const fetchVolunteer = async () => {
			try {
				const res = await api.get(`volunteer/${user.id}`);
				console.log(res.data);
				setAvailabillity(prev => {
					//napravimo shallow copy
					const updated = {...prev};
					res.data.availabilities.forEach(a => {
					//pritom ga ovde menjamo kako hocemo
					//tj ovo nam omogucava da menjamo samo one koje trebamo
						updated[a.day] = {
							enabled: a.enabled,
							range: [a.startHour, a.endHour]
						}
					});
					return updated;
				});
			} catch (err) {
				console.log(err);
			}
		};
		fetchVolunteer();
	}, []);

	const setRange = (day, val) => {
		setAvailabillity(p => ({
			...p,
			[day] : { ...p[day], range: val}
		}));
	};

	const saveAvailability = async () => {
		try {
			const body = DAYS.filter(day => availability[day].enabled)
							 .map(day => ({
								volunteerId: user.id,
								enabled: availability[day].enabled,
								day,
								startHour: availability[day].range[0],
								endHour: availability[day].range[1],
							}));
			await api.post('volunteer/saveAvailability', body);
		} catch (err){
			console.log(err);
		}
	}

	const toggleDay = (day) => {
		setAvailabillity(p => ({
			...p,
			[day] : {...p[day], enabled: !p[day].enabled}
		}));
	};


	const goToProfile = () => {
		navigate('/profile');
	}

	const seeTasks = async () => {
		try {
			const res = await api.get(`tasks/volunteer/${user?.id}`);
			console.log(res.data);
			setTasks(res.data);
		} catch (err) {
			console.log(err);
		};
	};

	const seeRatings = async () => {
		try {
			const res = await api.get(`/tasks/ratings/${user.id}`);
			console.log(res.data);
			setRatings(res.data);
		} catch (err) {
			console.log(err);
		}
	};

	const seeTraining = async () => {
		try {
			const res = await api.get(`/regiment/volunteer/${user.id}`);
			console.log(res.data);
			setRegiments(res.data);
		} catch (err) {
			console.log(err);
		}
	};

	const formatDate = (dateString) => {
		const date = new Date(dateString);
		return `${date.getDate()}/${date.getMonth() + 1}/${date.getFullYear()}`;
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
				<div>
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
        	    	        <h2>Ratings</h2>
        	    	        <p>See a full list of all your ratings.</p>
        	    	        <button className="btn-primary" 
									onClick={seeRatings}>
        	    	            See ratings
        	    	        </button>
        	    	    </div>
        	    	    <div className="dashboard-card" style={{width: '450px'}}>
        	    	        <h2>Training regiment</h2>
        	    	        <p>See the training regiment that you have been assigned to.</p>
        	    	        <button className="btn-primary" 
									onClick={seeTraining}>
        	    	            See training regiment
        	    	        </button>
        	    	    </div>
					</div>
				</div>

				<div className='v-availability-dash'>
						{DAYS.map((day, index) => (
                        <div key={day} className='day-card'>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                <input
                                    type='checkbox'
                                    checked={availability[day].enabled}
                                    onChange={() => toggleDay(day)}
                                />
                                <h2>{DAY_LABELS[index]}</h2>
                            </div>

                            {availability[day].enabled && (
                                <>
                                    <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '15px'}}>
                                        <p>{availability[day].range[0]}:00</p>
                                        <p>{availability[day].range[1]}:00</p>
                                    </div>
                                    <Slider.Root
                                        className="slider-root"
                                        min={7}
                                        max={22}
                                        step={1}
                                        value={availability[day].range}
                                        onValueChange={(val) => setRange(day, val)}
                                    >
                                        <Slider.Track className="slider-track">
                                            <Slider.Range className="slider-range" />
                                        </Slider.Track>
                                        <Slider.Thumb className="slider-thumb" />
                                        <Slider.Thumb className="slider-thumb" />
                                    </Slider.Root>
                                </>
                            )}
                        </div>
                    ))}
                    <button
                        style={{ height: '35px', width: '75px'}}
                        onClick={saveAvailability}
                    >Save</button>
				</div>
			</div>
			<br />
			{tasks != null && (
				<div className='volunteer-task-list'>
					{tasks.map((task) => (
						<div className='volunteer-task-card'>
							<h4>{task.name}</h4>
							<div className='task-card-coordinator'>
								<p style={{marginBottom: '10px',
										   marginTop: '10px',
										   fontWeight: 'bold'}}>Coordinator</p>
								<p style={{marginBottom: '5px'
								}}>{task.coordinator.name} {task.coordinator.surname}</p>
							{(task.coordinator.phone) ? <p>{task.coordinator.phone}</p> 
							: <p>No phone given</p>}
							{(task.coordinator.email) ? <p>{task.coordinator.email}</p> 
							: <p>No email given</p>}
							</div>
							<span>Start date: {formatDate(task.startDate)}</span>
							<span>End date: {formatDate(task.endDate)}</span>
							<button onClick={() => navigate(`/coord/tasks/${task.id}`)}>Details</button>
						</div>
						))}
				</div>
				)}
			{ratings != null && (
				<div className='volunteer-task-list'>
					{ratings.map((rating) => (
						<div className='volunteer-task-card'>
							<h4>{rating.comment}</h4>
							<h4>Grade: {rating.grade}</h4>
							<div className='task-card-coordinator'>
								<p style={{marginBottom: '10px',
										   marginTop: '10px',
										   fontWeight: 'bold'}}>Coordinator</p>
								<p style={{marginBottom: '5px'
								}}>{rating.coordinator.name} {rating.coordinator.surname}</p>
							{(rating.coordinator.phone) ? <p>{rating.coordinator.phone}</p> 
							: <p>No phone given</p>}
							{(rating.coordinator.email) ? <p>{rating.coordinator.email}</p> 
							: <p>No email given</p>}
							</div>
							<button onClick={() => navigate(`/coord/tasks/${rating.taskId}`)}>Details</button>
						</div>
						))}
				</div>
			)}
			{regiments != null && (
				<div className='volunteer-task-list'>
					{regiments.map((regiment) => (
						<div className='volunteer-task-card'>
							<div className='task-card-coordinator'>
								<p style={{marginBottom: '10px',
										   marginTop: '10px',
										   fontWeight: 'bold'}}>Your role: {regiment.trainer.id == user.id ? "Trainer" : "Trainee"}</p>
								<p style={{marginBottom: '10px',
										   marginTop: '10px',
										   fontWeight: 'bold'}}>Trainer</p>
								<p style={{marginBottom: '5px'
								}}>{regiment.trainer.name} {regiment.trainer.surname}</p>
							{(regiment.trainer.phone) ? <p>{regiment.trainer.phone}</p> 
							: <p>No phone given</p>}
							{(regiment.trainer.email) ? <p>{regiment.trainer.email}</p> 
							: <p>No email given</p>}
							</div>
							<div className='task-card-coordinator'>
								<p style={{marginBottom: '10px',
										   marginTop: '10px',
										   fontWeight: 'bold'}}>Certificate</p>
								<p style={{marginBottom: '5px'
								}}>{regiment.certificate.name}</p>
							</div>
							<button onClick={() => navigate(`/regiment/${regiment.id}`,
													{state: {from: 'volunteer', vId: user.id}})}>Details</button>
						</div>
						))}
				</div>
			)}
		</div>
	);
};

export default VolunteerDashboard;
