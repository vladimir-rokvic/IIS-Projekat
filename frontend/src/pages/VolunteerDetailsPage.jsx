import { useEffect, useState } from "react"
import { useParams } from "react-router-dom";
import api from "../api/axios";
import "./VolunteerDetailsPage.css";

const VolunteerDetailsPage = () => {
	const { id } = useParams();
	const [volunteer, setVolunteer] = useState();

	useEffect(() => {
		const fetchVolunteer = async () => {
			try {
				const res = await api.get(`/volunteer/${id}`);
				setVolunteer(res.data)
				//console.log(res.data);
			} catch (err) {
				console.log(err);
			}
		};
		fetchVolunteer();
	}, []);

	const calculateAge = (user) => {
		const today = new Date();
		const birthDay = new Date(user.dateOfBirth);
		return (today.getFullYear() - birthDay.getFullYear());
	};

	if(!volunteer) return<p>heh</p>;
	return (
        <div className="volunteer-profile">

			<label className="addressLabelInre">Basic information</label>
			<div className="vp-basic-info">
				<h2 style={{marginTop: '10px'}}>Name</h2>
				<p>{volunteer.name} {volunteer.surname}</p>
				<h2>Contact information</h2>
				<p>Email: {volunteer.email}</p>
				<p>Phone: {volunteer.phone}</p>
				<h2>Address</h2>
				{(volunteer.address !== null) ? (
				<div style={{display: 'flex', gap: '50px'}}>
					<div>
						<p style={{fontSize: '1.3rem'}}>Country</p>
						<p>{volunteer.address.country}</p>
					</div>
					<div>
						<p style={{fontSize: '1.3rem'}}>City</p>
						<p>{volunteer.address.city}</p>
					</div>
					<div>
						<p style={{fontSize: '1.3rem'}}>Street</p>
						<p>{volunteer.address.street}</p>
					</div>
				</div>) : (<p style={{color: '#555', fontSize: '1.3rem'}}>No address provided</p>)}
				<h2>Date of birth</h2>
				<p>{volunteer.dateOfBirth}</p>
				<p>Age: {calculateAge(volunteer)}</p>

				<h2>Biography</h2>
				{(volunteer.bio) ? 
					(<p className="vp-bio-section">{volunteer.bio}</p>)
					: (<p style={{color: '#555'}}>No bio given</p>)
				}
			</div>

			<label className="addressLabelInre">Skill types</label>
			<div className="back-to-shore">
				{(volunteer.skillTypes.length !== 0) ? (volunteer.skillTypes.map((skill, index) =>
				(<div key={index} className="skill-card">
					<h3>{skill.name}</h3>
					<p>{skill.desc}</p>
				</div>)))
				: (<p style={{color: '#555', fontSize: '1.3rem'}}>No skill types set</p>)}
			</div>
			<label className="addressLabelInre">Skills</label>
			<div className="back-to-shore">
				{(volunteer.skills.length !== 0) ? (volunteer.skills.map((skill, index) =>
				(<div key={index} className="skill-card">
					<h3>{skill.name}</h3>
					<p>{skill.desc}</p>
				</div>)))
				: (<p style={{color: '#555', fontSize: '1.3rem'}}>No skills set</p>)}
			</div>
        </div>
    );
};

export default VolunteerDetailsPage;
