
import { useEffect, useRef, useState } from "react";
import api from "../api/axios";
import "./VolunteerUpdatePage.css";
import { useNavigate } from "react-router-dom";

const VolunteerUpdatePage = () => {
    const [user, setUser] = useState(null);
    const navigate = useNavigate();

    const firstName = useRef();
    const lastName = useRef();
    const bio = useRef();
    const dateOfBirth = useRef();
    const street = useRef();
    const city = useRef();
    const country = useRef();
    const phone = useRef();
    const email = useRef();

    useEffect(() => {
        const fetchVolunteer = async () => {
            try {
                const id = JSON.parse(localStorage.getItem("user")).id;
                const res = await api.get("/volunteer/" + id);
                setUser(res.data);
            } catch (err) {
                console.log(err);
            }
        };
        fetchVolunteer();
    }, []);

    const handleSave = async () => {
        const id = JSON.parse(localStorage.getItem("user")).id;
        const body = {
            name: firstName.current.value,
            surname: lastName.current.value,
            bio: bio.current.value,
            dob: dateOfBirth.current.value,
            street: street.current.value,
            city: city.current.value,
            country: country.current.value,
            phone: phone.current.value,
            email: email.current.value,
			password: null,
        };
        console.log(body);
		
		try {
			await api.put("/volunteer/" + id, body);
			navigate("/profile");
		} catch (err) {
			console.log(err);
		}
    };

    if (!user) return <p>Loading...</p>;

    return (
        <div className="volunteer-update">
            <div className="header">
                <h2>Volunteer profile view</h2>
                <div className="header-buttons">
                    <button className="btn-save" onClick={handleSave}>Save</button>
                </div>
            </div>

            <div className="content">
                <div className="left">
                    <div className="row">
                        <div className="field">
                            <label>First name</label>
                            <input
                                ref={firstName}
                                type="text"
                                defaultValue={user.name}
                                placeholder="Enter your first name"
                            />
                        </div>
                        <div className="field">
                            <label>Last name</label>
                            <input
                                ref={lastName}
                                type="text"
                                defaultValue={user.surname}
                                placeholder="Enter your last name"
                            />
                        </div>
                    </div>

                    <div className="field">
                        <label>Bio</label>
                        <textarea
                            ref={bio}
                            defaultValue={user.bio || ""}
                            placeholder="Short volunteer biography"
                        />
                    </div>

                    <div className="field">
                        <label>Date of birth</label>
                        <input
                            ref={dateOfBirth}
                            type="date"
                            defaultValue={user.dateOfBirth || ""}
                        />
                    </div>

                    <div className="input-group">
                        <label className="group-label">Address</label>
                        <div className="row">
                            <div className="field">
                                <input
                                    ref={street}
                                    type="text"
                                    defaultValue={user.address?.street || ""}
                                    placeholder="Volunteer living address"
                                />
                            </div>
                            <div className="field">
                                <input
                                    ref={city}
                                    type="text"
                                    defaultValue={user.address?.city || ""}
                                    placeholder="City"
                                />
                            </div>
                            <div className="field">
                                <input
                                    ref={country}
                                    type="text"
                                    defaultValue={user.address?.country || ""}
                                    placeholder="Country"
                                />
                            </div>
                        </div>
                    </div>

                    <div className="field">
                        <label>Phone number</label>
                        <input
                            ref={phone}
                            type="text"
                            defaultValue={user.phone || ""}
                            placeholder="Volunteer phone number"
                        />
                    </div>

                    <div className="field">
                        <label>Email</label>
                        <input
                            ref={email}
                            type="text"
                            defaultValue={user.email || ""}
                            placeholder="Volunteer email"
                        />
                    </div>
                </div>

                <div className="avatar">
                    <div className="avatar-circle" />
                    <p>Upload photo</p>
                </div>
            </div>
        </div>
    );
};

export default VolunteerUpdatePage;
