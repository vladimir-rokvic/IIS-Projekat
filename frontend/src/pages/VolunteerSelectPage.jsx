import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./VolunteerSelectPage.css";

const VolunteerSelectPage = () => {
    const [volunteers, setVolunteers] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchVolunteers = async () => {
            try {
                const res = await api.get("/volunteer");
                setVolunteers(res.data);
            } catch (err) {
                console.log(err);
            }
        };
        fetchVolunteers();
    }, []);

	const handleDetails = (id) => {
		navigate(`/volunteer/details/${id}`)
	}

	const handleSelect = (id) => {
		navigate('/coord/createTask', { state: {v_id: id} });
	}

    return (
        <div className="volunteer-select-page">
            <div className="header">
                <h2>Volunteers</h2>
            </div>
            <div className="volunteer-list">
                {volunteers.map((v) => (
                    <div key={v.id} className="volunteer-row">
                        <div className="avatar-small" />
                        <span className="volunteer-name">{v.name} {v.surname}</span>
                        <span className="volunteer-email">{v.email} </span>
                        <span className="volunteer-address">
                            {v.address ? `${v.address.city}, ${v.address.country}` : "No address"}
                        </span>
                        <span className="volunteer-phone">{v.phone || "No phone"}</span>
                        <button
                            className="btn-details"
                            onClick={() => handleSelect(v.id)}
                        >Select</button>
                        <button
                            className="btn-details"
                            onClick={() => handleDetails(v.id)}
                        >Details</button>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default VolunteerSelectPage;
