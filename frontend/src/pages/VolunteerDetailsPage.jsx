import { useEffect, useState } from "react"
import { useParams } from "react-router-dom";
import api from "../api/axios";
import "./VolunteerDetailsPage.css";
import * as Slider from '@radix-ui/react-slider';

const VolunteerDetailsPage = () => {
	const { id } = useParams();
	const [volunteer, setVolunteer] = useState();

	const DAY_LABELS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];

	useEffect(() => {
		const fetchVolunteer = async () => {
			try {
				const res = await api.get(`/volunteer/${id}`);
				setVolunteer(res.data)
				console.log(res.data);
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
				<div style={{display: 'flex', gap: '100px'}}>
					<div>
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
					<div className='v-availability-dash'>
						{volunteer.availabilities && 
						(volunteer.availabilities.map(a => (
							<div key={a.id} className="day-card"
								style={{width: '300px', height: '200px'}}>
                            	<h2>{a.day}</h2>
                            {a.enabled && (
                                <>
                                    <div style={{display: 'flex', justifyContent: 'space-between', marginBottom: '15px'}}>
                                        <p>{a.startHour}:00</p>
                                        <p>{a.endHour}:00</p>
                                    </div>
                                    <Slider.Root
                                        className="slider-root"
                                        min={7}
                                        max={22}
                                        step={1}
                                        value={[a.startHour, a.endHour]}
										disabled
										style={{pointerEvents: 'none'}}
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
						)
						))}
					</div>
				</div>
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
