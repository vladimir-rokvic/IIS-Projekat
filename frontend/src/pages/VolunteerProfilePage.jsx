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
				//console.log(res.data);
				//console.log(id);
				setUser(res.data);
			} catch(err) {
				console.log(err);
			}
		};
		fetchVolunteer();
	}, []);

	const navigate = useNavigate();
	const editClick = () => {
		navigate('/volunteer/update');
	}

	if (!user) return <p>Loading...</p>;

    return (
        <div className="volunteer-profile">
            <div className="header">
                <h2>Volunteer profile view</h2>
                <div className="header-buttons">
                    <button 
						className="btn-edit"
						onClick={editClick}>Edit</button>
                </div>
            </div>

            <div className="content">
                <div className="left">
                    <div className="row">
                        <div className="field">
                            <label>First name</label>
                            <span>{user.name}</span>
                        </div>
                        <div className="field">
                            <label>Last name</label>
                            <span>{user.surname}</span>
                        </div>
                    </div>

                    <div className="field">
                        <label>Bio</label>
                        <span>{user.bio || "No bio added yet"}</span>
                    </div>

                    <div className="field">
                        <label>Date of birth</label>
                        <span>{user.dateOfBirth}</span>
                    </div>

                    <div className="field">
                        <label>Address</label>
                        <span>
                            {user.address
                                ? `${user.address.street}, ${user.address.city}, ${user.address.country}`
                                : "No address added"}
                        </span>
                    </div>

                    <div className="field">
                        <label>Phone number</label>
                        <span>{user.phone}</span>
                    </div>

                    <div className="field">
                        <label>Email</label>
                        <span>{user.email}</span>
                    </div>
                </div>

                <div className="avatar">
                    <div className="avatar-circle" />
                </div>
            </div>
        </div>
    );
}

export default VolunterProfilePage;
