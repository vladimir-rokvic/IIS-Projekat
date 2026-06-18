import { useEffect, useRef, useState } from "react";
import api from "../api/axios";
import { useNavigate, useParams } from "react-router-dom";
import "./TaskDetailsPage.css";
import { useAuth } from "../context/AuthContext";

const TaskDetailsPage = () => {
	const {id} = useParams();
	const [task, setTask] = useState(null);
	const [volunteer, setVolunteer] = useState(null);
	const navigate = useNavigate();

	const comment = useRef('');
	const grade = useRef(3);

	const { user } = useAuth();

	const weekDays = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
	const [startDay, setStartDay] = useState(0);
	const [endDay, setEndDay] = useState(0);
	useEffect(() => {
		const fetchTask = async () => {
			try {
				const res = await api.get(`/tasks/${id}`);
				setTask(res.data);
				console.log(res.data);
				setVolunteer(res.data.volunteer);
				setStartDay(new Date(res.data.startDate).getDay());
				setEndDay(new Date(res.data.endDate).getDay());
			} catch (err){
				console.log(err);
			}
		};
		fetchTask();
	}, []);

	const handleSubmit = async () => {
		const body = {
			grade: grade.current.value,
			comment: comment.current.value,
			taskId: id,
			volunteerId: volunteer.id,
		};
		try {
			api.post(`/tasks/rate/${id}`, body);
		} catch (err) {
			console.log(err);
		}
	};

    if (!task) return <p>Loading...</p>;
	return (
        <div className="task-details-page">
            <div className="header">
                <div className="header-left">
                    <h1>{task.name}</h1>
                </div>
				{user.role !== "VOLUNTEER" && (<div className="header-buttons">
                    <button className="btn-edit" onClick={() => navigate(`/coord/tasksEdit/${id}`)}>Edit</button>
                </div>)}
            </div>

					<label className="addressLabelInre">
						Coordinator information
					</label>

            <div className="coordinator-section">
                <div className="coordinator-row">
				    {task.coordinator && (
						<div className="choosen-volunteer">
							<div style={{display: 'flex'}}>
                    		<div className="avatar-small" />
				            <p className="coordinator-name">
								{task.coordinator.name} {task.coordinator.surname}
							</p>
							</div>
							<h3>Contact information</h3>
							<p className="contact-info">Email: {task.coordinator.email}</p>
							{task.coordinator.phone ? 
							<p className="contact-info">Phone: {task.coordinator.phone}</p> : <p style={{color: '#555', fontSize: '1.3rem'}}>No phone given</p>}
							<h3>Address</h3>
							<p className="contact-info">{volunteer.address.street}  {volunteer.address.city} {volunteer.address.country}</p>
						</div>
				    )}
                </div>
            </div>


			<label className="addressLabelInre">Volunteer information</label>
            <div className="volunteer-section">
                <div className="volunteer-row">
				    {volunteer ? (
						<div className="choosen-volunteer">
							<div style={{display: 'flex'}}>
                    		<div className="avatar-small" />
				            <span className="volunteer-name">
								{volunteer.name} {volunteer.surname}
							</span>
							</div>
							<h3>Contact information</h3>
							<p className="contact-info">Email: {volunteer.email}</p>
							<p className="contact-info">Phone: {volunteer.phone}</p>
							<h3>Address</h3>
							<p className="contact-info">{volunteer.address.street}  {volunteer.address.city} {volunteer.address.country}</p>

							<h3>Skills</h3>
							{ volunteer.skills.length == 0 ? <p style={{color: '#555',
fontSize: '1.3rem'}}>Volunteer doesn't have any skills yet</p> :
							<p className="contact-info">{volunteer.skills.map((skill) => (skill))}</p>}
						</div>
				    ) : (
					<>
                    	<div className="avatar-small" />
				        <span className="volunteer-placeholder">No volunteer selected</span>
					</>
				    )}
                </div>
            </div>

			<label className="addressLabelInre">Basic information</label>
			<div className="basicinfo-section">
				<p className="volunteer-name" style={{fontWeight:'bold'}}>{task.name}</p>
				<p className="contact-info" style={{marginTop: '5px'}}>Description:</p>
				<p className="task-description">{task.description}</p>
				<div className="time-details">
				<p style={{marginBottom: '10px', fontWeight: 'bold'}} className="contact-info">Time period of the task</p>
				<p style={{marginBottom: '10px'}} className="contact-info">Start date: {weekDays[startDay]} {(task.startDate)}</p>
				<p className="contact-info">End date: {weekDays[endDay]} {(task.endDate)}</p>
				</div>
			</div>


            <div className="skills-section">
                <label className="addressLabelInre">Skills required</label>
                <div className="skills-list">
                    {task.requiredSkillTypes.length == 0 ? 
						(<p style={{color: '#555'}}>No skills set yet</p>) :
						task.requiredSkillTypes.map((s, index) => (
                        <div key={index} className="skill-item">
                            <p>{s.name}</p>
							<p>{s.desc}</p>
                        </div>
                    ))}
                </div>
            </div>
		
		{(Date.parse(task.endDate) < Date.now()
		&& task.performance == null && user.role !== "VOLUNTEER") && (
			<>
            	<label className="addressLabelInre">Rate the performance of the volunteer</label>
				<div className="performance-class">
					<div style={{display: 'flex'}}>
						<p>Grade:</p>
						<select ref={grade}>
							<option>1</option>
							<option>1.5</option>
							<option>2</option>
							<option>2.5</option>
							<option>3</option>
							<option>3.5</option>
							<option>4</option>
							<option>4.5</option>
							<option>5</option>
						</select>
						<button className="btn-edit" style={{marginLeft: '10px', 
								height: '35px', marginTop: '5px'}} onClick={handleSubmit}>
							Submit
						</button>
					</div>

					<div style={{display: 'flex'}}>
            			<textarea
							ref={comment}
            			    className="description-input"
							style={{width: '740px'}}
            			    placeholder="Enter comment"
            			/>
					</div>

				</div>
			</>
		)}
		
		{task.performance != null && (
			<>
            	<label className="addressLabelInre">Performance rating</label>
				<div className="performance-class">
					<p>Performance grade: {task.performance.grade}</p>
					<p style={{marginBottom: '0px'}}>Comment:</p>
					<p className="task-description"
						style={{marginTop: '0px', fontWeight: 'unset'}}>{task.performance.comment}</p>
				</div>
			</>
		)}


        </div>
    );
};

export default TaskDetailsPage;
