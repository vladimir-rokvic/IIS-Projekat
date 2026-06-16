import { useEffect, useRef, useState } from "react";
import api from "../api/axios";
import { useNavigate, useParams } from "react-router-dom";
import "./TaskDetailsEdit.css";

const TaskDetailsEdit = () => {
	const {id} = useParams();
	const [task, setTask] = useState(null);
	const [volunteer, setVolunteer] = useState(null);
	const navigate = useNavigate();

    const taskName = useRef();
    const description = useRef();

	const [skill, setSkill] = useState(null);
	const [skillTypes, setSkillTypes] = useState([]);
	const [skills, setSkills] = useState([]);

	const comment = useRef('');
	const grade = useRef(3);

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
				setSkills(res.data.requiredSkillTypes);
				setStartDay(new Date(res.data.startDate).getDay());
				setEndDay(new Date(res.data.endDate).getDay());
			} catch (err){
				console.log(err);
			}
		};

		const fetchSkillTypes = async () => {
			try {
				const res = await api.get('/skill-types');
				setSkillTypes(res.data);
			} catch (err) {
				console.log(err);
			}
		};

		fetchTask();
		fetchSkillTypes();
	}, []);

    const addSkill = () => {
		if(!skill) return;
        setSkills([...skills, { id: skill.id, name: skill.name, description: skill.description }]);
    };

    const removeSkill = (index) => {
        setSkills(skills.filter((_, i) => i !== index));
    };

	const handleSubmit = async () => {
		const body = {
			grade: grade.current.value,
			comment: comment.current.value,
			taskId: id,
			volunteerId: volunteer.id,
			id: task.performance.id
		};
		try {
			api.put(`/tasks/rate/${id}`, body);
		} catch (err) {
			console.log(err);
		}
	};

	const handleSave = async () => {
		const body = {
			requiredSkills: skills,
			name: taskName.current.value,
			description: description.current.value,
			volunteerId: volunteer.id
		};

		try {
			const res = await api.put(`/tasks/${id}`, body);
			console.log(res.data);
			navigate(`/coord/tasks/${id}`);
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
                <div className="header-buttons">
                    <button className="btn-edit" onClick={handleSave}>Save</button>
                </div>
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
			{ Date.parse(task.endDate) > Date.now() && (<button 
														className="btn-change"
														onClick={() =>
															navigate('/coord/createTask/addVolunteer', {state: {from: 'edit', taskId: id}})
														}
														>Change volunteer</button>)}
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
				<div className="volunteer-section">
				<div className="v-field">
					<input type="text" placeholder={task.name}
					ref={taskName} style={{marginLeft: '10px'}}/>
				</div>
            	<textarea
            	    ref={description}
            	    className="description-input"
					style={{width: '740px'}}
            	    placeholder={task.description}
            	/>

				<div className="time-details">
				<p style={{marginBottom: '10px', fontWeight: 'bold'}} className="contact-info">Time period of the task</p>
				<p style={{marginBottom: '10px'}} className="contact-info">Start date: {weekDays[startDay]} {(task.startDate)}</p>
				<p className="contact-info">End date: {weekDays[endDay]} {(task.endDate)}</p>
				</div>
			</div>


            <div className="skills-section">
                <label className="addressLabelInre">Skills required</label>
                <div className="skills-input-row">
					<select
						value={skill?.id ?? ""}
						onChange={(e) => {
							const selected = skillTypes.find(
            				s => s.id === Number(e.target.value)
        					);

        					setSkill(selected);
						}}>
						<option value="">Choose a skill</option>
						{skillTypes.map(s => (
							<option key={s.id}
									value={s.id}>
								{s.name}
							</option>
						))}
					</select>
                    <button className="btn-save" onClick={addSkill}>Add +</button>
                </div>
                <div className="skills-list">
                    {skills.length == 0 ? (<p style={{color: '#555'}}>No skills set yet</p>) : skills.map((s, index) => (
                        <div key={index} className="skill-item">
                            <p style={{fontWeight: 'bold'}}>{s.name}</p>
							<p>{s.desc}</p>
                            <button className="btn-remove" onClick={() => removeSkill(index)}>x</button>
                        </div>
                    ))}
                </div>
            </div>
		
		{(Date.parse(task.endDate) < Date.now()) && (
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
						{ task.performance == null ? (
            			<textarea
							ref={comment}
            			    className="description-input"
							style={{width: '740px'}}
            			    placeholder="Enter comment"
            			/>) : (
            			<textarea
							ref={comment}
            			    className="description-input"
							style={{width: '740px'}}
            			    placeholder={task.performance.comment} />
						)}
					</div>
				</div>
			</>
		)}

        </div>
    );
};

export default TaskDetailsEdit;
