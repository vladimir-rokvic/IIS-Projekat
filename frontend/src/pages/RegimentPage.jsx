import { useEffect, useState } from "react";
import { useLocation, useParams } from "react-router-dom";
import api from "../api/axios";
import "./CreateTaskPage.css";

const RegimentPage = () => {
	const {id} = useParams();
	const user = JSON.parse(localStorage.getItem("user"));

	const [regiment, setRegiment] = useState(null);
	const [volunteer, setVolunteer] = useState(null);
	const weekDays = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
	const [startDay, setStartDay] = useState(0);
	const [endDay, setEndDay] = useState(0);

	const location = useLocation();

	useEffect(() => {
		const fetchRegiment = async () => {
			try {
				const res = await api.get(`/regiment/${id}`);
				console.log(res.data);
				setRegiment(res.data);
				setVolunteer(res.data.trainer);
				setStartDay(new Date(res.data.startDate).getDay());
				setEndDay(new Date(res.data.endDate).getDay());
			} catch (err) {
				console.log(err);
			};
		};
		fetchRegiment();
	}, []);

	const removeTrainee = (id) => {
        setRegiment(prev => ({
            ...prev,
            trainees: prev.trainees.filter(t => t.id !== id)
        }));
	};

	const saveRegiment = async () => {
		try {
			await api.put(`regiment/${regiment.id}/removeTrainees`, regiment);
		} catch (err) {
			console.log(err);
		}
	};
	
	if(!regiment) return <p>Loading</p>;
	return (
		<div className="create-task-page">
			<label className="addressLabelInre">Certificate information</label>
			<div className="dashboard-certificate">
				<div style={{display: 'flex', flexDirection: 'column'}}>
					<span style={{fontWeight: 'bold'}} className="volunteer-name">
						{regiment.certificate.name}
					</span>
					<p style={{
						wordWrap: 'break-word', 
						margin: '20px',
						fontSize: '1.3rem'
						}}>
						{regiment.certificate.description}</p>
				</div>
			</div>


			<label className="addressLabelInre"
					>Trainer information</label>
            <div className="volunteer-section">
                <div className="volunteer-row">
						<div className="choosen-volunteer">
							<div style={{display: 'flex'}}>
                    		<div className="avatar-small" />
				            <span className="volunteer-name">
								{volunteer.name} {volunteer.surname}
							</span>
							</div>
							<h3>Contact information</h3>
							{volunteer.email ? (<p className="contact-info">Email: {volunteer.email}</p>) : (<p style={{color: '#555', fontSize: '1.3rem'}}>No email provided</p>)}
							{volunteer.phone ? (<p className="contact-info">Phone: {volunteer.phone}</p>) : (<p style={{color: '#555', fontSize: '1.3rem'}}>No email provided</p>)}
							<h3>Address</h3>
							{volunteer.address ?
							(<p className="contact-info">{volunteer.address.street}  {volunteer.address.city} {volunteer.address.country}</p>) : (<p style={{color: '#555', fontSize: '1.3rem'}}>No address info provided</p>)}

							<h3>Skills</h3>
							<div style={{display: 'flex', gap: '10px'}}>
							{ volunteer.skills.length == 0 ? <p style={{color: '#555',
fontSize: '1.3rem'}}>Volunteer doesn't have any skills yet</p> :
								volunteer.skills.map((skill) => (
									<div key={skill.name} className="v-dashboard-skills">
										<p style={{fontSize: '1.3rem', alignSelf: 'center', fontWeight: 'bold'}}>{skill.name}</p>
										<p style={{fontSize: '1.3rem'}}>{skill.desc}</p>
									</div>
								))
							}</div>
						</div>
                </div>
            </div>


			<label className="addressLabelInre">Regiment description</label>
			<div className="basicinfo-section">
				<p className="contact-info" style={{marginTop: '5px'}}>Description:</p>
				<p className="task-description">{regiment.description}</p>
				<div className="time-details">
				<p style={{marginBottom: '10px', fontWeight: 'bold'}} className="contact-info">Time period of the regiment</p>
				<p style={{marginBottom: '10px'}} className="contact-info">Start date: {weekDays[startDay]} {(regiment.startDate)}</p>
				<p className="contact-info">End date: {weekDays[endDay]} {(regiment.endDate)}</p>
				</div>
			</div>

			<div style={{display: 'flex'}}>
				<label className="addressLabelInre">Trainees</label>
			{(user.id === regiment.trainer.id) 
			&& <button className="btn-save" style={{marginLeft: '10px', 
					height:'30px'}}
					onClick={saveRegiment}>Save</button>}
			</div>
			<div className="trainees-dash">
				{regiment.trainees && (regiment.trainees.map((trainee, index) => (
					<div key={index} className="trainee-card">
						<p style={{marginBottom: '10px', fontWeight: 'bold'}}>{trainee.name} {trainee.surname}</p>
						{trainee.email ? (<p style={{marginBottom: '5px'}}>Email: {trainee.email}</p>) : (<p style={{color: '#555', fontSize: '1.3rem'}}>No email provided</p>)}
						{trainee.phone ? (<p style={{marginBottom: '5px'}}>Phone: {trainee.phone}</p>) : (<p style={{color: '#555', fontSize: '1.3rem'}}>No phone provided</p>)}
						{(user.id === regiment.trainer.id) && (
							<button className="btn-save" style={{marginTop: '5px'}}
								onClick={() => removeTrainee(trainee.id)}>
								Remove
							</button>
						)}
					</div>
				)))}
			</div>

		</div>
	);
};

export default RegimentPage;
