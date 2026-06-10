import { useState, useRef, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./CreateTaskPage.css";
import { useAuth } from "../context/AuthContext";

const CreateTaskPage = () => {
    const navigate = useNavigate();
    const taskName = useRef();
    const description = useRef();
    const startDate = useRef();
    const endDate = useRef();

    const [volunteer, setVolunteer] = useState(null);
    const [skills, setSkills] = useState([]);
    const [skill, setSkill] = useState("");

	const { user } = useAuth();

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
			coordinatorId: user.id,
            requiredSkills: skills,
        };
        console.log(body);
        try {
            await api.post("/tasks", body);
            navigate('/tasks');
        } catch (err) {
            console.log(err);
        }
    };

	const handleAddVolunteer = () => {
		navigate('/coord/createTask/addVolunteer', {state: {from: 'create'}});
	}

    return (
        <div className="create-task-page">
            <button className="create-back-btn" style={{width: '140px'}} onClick={() => navigate('/tasks')}>
                ← Back to Tasks
            </button>

            <h1 className="create-title">Create a new task</h1>
            <p className="create-subtitle">Fill in the required information to create a new task.</p>

			<label className="addressLabelInre">Volunteer information</label>
            <div className="volunteer-section">
                <button className="btn-add-volunteer"
					onClick={handleAddVolunteer}>Add volunteer +</button>
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

			<label className="addressLabelInre">Meta information</label>
				<div className="volunteer-section">
				<div className="v-field">
					<input type="text" placeholder="Enter task name here"
					ref={taskName}/>
				</div>
            	<textarea
            	    ref={description}
            	    className="description-input"
					style={{width: '740px'}}
            	    placeholder="Enter the description of the task here"
            	/>

            	<div className="date-row">
            	    <div className="v-field">
            	        <label>Start date</label>
            	        <input ref={startDate} type="date" />
            	    </div>
            	    <div className="v-field">
            	        <label>End date</label>
            	        <input ref={endDate} type="date" />
            	    </div>
            	</div>
			</div>

            <div className="skills-section">
                <label className="addressLabelInre">Skills required</label>
                <div className="skills-input-row">
                    <input
                        type="text"
                        value={skill}
                        onChange={(e) => setSkill(e.target.value)}
                        placeholder="Enter a skill"
                    />
                    <button className="btn-save" onClick={addSkill}>Add +</button>
                </div>
                <div className="skills-list">
                    {skills.length == 0 ? (<p style={{color: '#555'}}>No skills set yet</p>) : skills.map((s, index) => (
                        <div key={index} className="skill-item">
                            <p>{s.name}</p>
                            <button className="btn-remove" onClick={() => removeSkill(index)}>x</button>
                        </div>
                    ))}
                </div>
            </div>

			
            <div className="form-actions">
                <button className="btn-cancel" onClick={() => navigate('/tasks')}>
                    Cancel
                </button>
                <button className="btn-save" onClick={handleSave}>Save</button>
            </div>
        </div>
    );
};

export default CreateTaskPage;
