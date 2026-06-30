import { useEffect, useState } from "react";
import api from "../api/axios";
import "./VolunteerProfilePage.css";
import { useNavigate } from "react-router-dom";

const VolunterProfilePage = () => {
	const [user, setUser] = useState(null);

	useEffect(() => {
		const fetchVolunteer = async () => {
			try {
				const id = JSON.parse(localStorage.getItem("user")).id;
				const res = await api.get("/volunteer/" + id);
				console.log(res.data);
				//console.log(id);
				setUser(res.data);
			} catch(err) {
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

	const navigate = useNavigate();
	const editClick = () => {
		navigate('/volunteer/update');
	}

	if (!user) return <p>Loading...</p>;

    return (
        <div className="volunteer-profile">
            <div className="header">
                <div className="header-buttons">
                    <button 
						className="btn-edit"
						onClick={editClick}>Edit</button>
                </div>
            </div>

			<label className="addressLabelInre">Basic information</label>
			<div className="vp-basic-info">
				<div style={{display: 'flex', justifyContent: 'space-between'}}>
					<div>
						<h2 style={{marginTop: '10px'}}>Name</h2>
						<p>{user.name} {user.surname}</p>
						<h2>Contact information</h2>
						<p>Email: {user.email}</p>
						<p>Phone: {user.phone}</p>
						<h2>Address</h2>
						{(user.address) ? (
						<div style={{display: 'flex', gap: '50px'}}>
							<div>
								<p style={{fontSize: '1.3rem'}}>Country</p>
								<p>{user.address.country}</p>
							</div>
							<div>
								<p style={{fontSize: '1.3rem'}}>City</p>
								<p>{user.address.city}</p>
							</div>
							<div>
								<p style={{fontSize: '1.3rem'}}>Street</p>
								<p>{user.address.street}</p>
							</div>
						</div>) : (<p style={{color: '#555', fontSize: '1.3rem'}}>No address provided</p>)}
						<h2>Date of birth</h2>
						<p>{user.dateOfBirth}</p>
						<p>Age: {calculateAge(user)}</p>

						<h2>Biography</h2>
						{(user.bio) ? 
							(<p className="vp-bio-section">{user.bio}</p>)
							: (<p style={{color: '#555'}}>No bio given</p>)
						}
					</div>
					<div className="image-section">
						{user.profileImgPath && (
							<img
    						    src={`http://localhost:8080/api/volunteer/${user.id}/image`}
    						    className="profile-image"
    						/>
						)}
					</div>
				</div>
			</div>

			<label className="addressLabelInre">Skill types</label>
			<div className="back-to-shore">
				{(user.skillTypes.length !== 0) ? (user.skillTypes.map((skill, index) =>
				(<div key={index} className="skill-card">
					<h3>{skill.name}</h3>
					<p>{skill.desc}</p>
				</div>)))
				: (<p style={{color: '#555', fontSize: '1.3rem'}}>No skill types set</p>)}
			</div>
			<label className="addressLabelInre">Skills</label>
			<div className="back-to-shore">
				{(user.skills.length !== 0) ? (user.skills.map((skill, index) =>
				(<div key={index} className="skill-card">
					<h3>{skill.name}</h3>
					<p>{skill.desc}</p>
				</div>)))
				: (<p style={{color: '#555', fontSize: '1.3rem'}}>No skills set</p>)}
			</div>
        </div>
    );
};

export default VolunterProfilePage;
