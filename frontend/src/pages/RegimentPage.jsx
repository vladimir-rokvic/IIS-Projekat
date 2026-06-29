import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/axios";
import "./CreateTaskPage.css";

const RegimentPage = () => {
	const {id} = useParams();

	const [regiment, setRegiment] = useState(null);
	const [volunteer, setVolunteer] = useState(null);

	useEffect(() => {
		const fetchRegiment = async () => {
			try {
				const res = await api.get(`/regiment/${id}`);
				console.log(res.data);
				setRegiment(res.data);
				setVolunteer(res.data.trainer);
			} catch (err) {
				console.log(err);
			};
		};
		fetchRegiment();
	}, []);
	
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


			<label className="addressLabelInre">Trainer information</label>
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
							<p className="contact-info">Email: {volunteer.email}</p>
							<p className="contact-info">Phone: {volunteer.phone}</p>
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

		</div>
	);
};

export default RegimentPage;
