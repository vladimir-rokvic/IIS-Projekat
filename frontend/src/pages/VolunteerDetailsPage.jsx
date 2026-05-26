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

	if(!volunteer) return<p>heh</p>;
	return (
        <div className="volunteer-details-page">
            <div className="header">
                <h2>Volunteer profile view</h2>
            </div>

            <div className="content">
                <div className="left">
                    <div className="row">
                        <div className="field">
                            <label>First name</label>
                            <div className="value-box">{volunteer.name}</div>
                        </div>
                        <div className="field">
                            <label>Last name</label>
                            <div className="value-box">{volunteer.surname}</div>
                        </div>
                    </div>

                    <div className="field">
                        <label>Bio</label>
                        <div className="value-box bio-box">{volunteer.bio || "No bio added yet"}</div>
                    </div>

                    <div className="field">
                        <label>Date of birth</label>
                        <input type="date" value={volunteer.dateOfBirth || ""} readOnly />
                    </div>

                    <div className="field">
                        <label>Address</label>
                        <div className="value-box">
                            {volunteer.address
                                ? `${volunteer.address.street}, ${volunteer.address.city}, ${volunteer.address.country}`
                                : "No address"}
                        </div>
                    </div>

                    <div className="field">
                        <label>Phone number</label>
                        <div className="value-box">{volunteer.phone || "No phone"}</div>
                    </div>

                    <div className="field">
                        <label>Email</label>
                        <div className="value-box">{volunteer.email}</div>
                    </div>

                    <div className="skills-section">
                        {volunteer.skills && volunteer.skills.length > 0 ? (
                            <div className="skills-grid">
                                {volunteer.skills.map((s, index) => (
                                    <div key={index} className="skill-entry">
                                        <span className="skill-name">{s.name}</span>
                                        <span className="skill-desc">{s.desc || ""}</span>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <span>No skills added</span>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default VolunteerDetailsPage;
