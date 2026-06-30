import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import api from "../api/axios";
import "./VolunteerSelectPage.css";

const VolunteerSelectPage = () => {
    const [volunteers, setVolunteers] = useState([]);
	const [ratedVolunteers, setRatedVolunteers] = useState([]);
	const [cert, setCert] = useState([]);
    const navigate = useNavigate();

	const location = useLocation();

    useEffect(() => {
        const fetchVolunteers = async () => {
            try {
                const res = await api.get("/volunteer");
                setVolunteers(res.data);
            } catch (err) {
                console.log(err);
            }
        };

		const recommendVolunteers = async () => {
			try {
				const taskId = location.state?.taskId;
				if(!taskId) return;
				const res = await api.get(`/volunteer/rank/${taskId}`);
				console.log(res.data);
				setRatedVolunteers(res.data.sort((a,b) => b.predictedGrade - a.predictedGrade));
				//setRatedVolunteers(ratedVolunteers.sort((a,b) => b.predictedGrade - a.predictedGrade));
			} catch (err) {
				console.log(err);
			}
		};

		if(location.state?.cert) {
			setCert(location.state?.cert);
		}

        fetchVolunteers();
		if(location.state?.from === "edit") {
			recommendVolunteers();
		}
    }, []);

	const handleDetails = (id) => {
		navigate(`/volunteer/details/${id.id}`)
	}

	const handleSelect = (v) => {
		if(location.state?.from === "edit") {
			navigate(`/coord/tasksEdit/${location.state?.taskId}`, { state: {v_id: v.id, volunteer: v} });
		} else if(location.state?.from === "create") {
			navigate('/coord/createTask', { state: {v_id: v.id} });
		} else if(location.state?.from === "training-create") {
			navigate('/manager/createRegiment', {state: {trainer: v, certificate: cert}});
		}
	}

    return (
        <div className="volunteer-select-page">
            <div className="header">
				<label className="addressLabelInre">Volunteers</label>
            </div>
            <div className="volunteer-list">
                {volunteers.map((v) => (
					<div key={v.id} className="onajnajveci" style={{display: 'flex'}}>
                    <div className="volunteer-row-1">
						<div style={{display: 'flex', marginBottom: '5px'}}>
                        	<div className="avatar-small" />
                        	<span className="volunteer-name">{v.name} {v.surname}</span>
						</div>

						<div style={{display: 'flex', marginBottom: '5px', gap: '10px'}}>
                        	<span className="volunteer-email">{v.email} </span>
                        	<span className="volunteer-phone">{v.phone || "No phone"}</span>
						</div>
                        <span className="volunteer-address">
                            {v.address ? `${v.address.city}, ${v.address.country}` : "No address"}
                        </span>
                    </div>
					<div style={{gap: '20px', margin: '10px'}}>
                        <button
                            className="btn-details"
                            onClick={() => handleSelect(v)}
                        >Select</button>
                        <button
                            className="btn-details"
                            onClick={() => handleDetails(v)}
							style={{marginLeft: '20px'}}
                        >Details</button>
					</div>
					</div>
                ))}
            </div>

		{(location.state?.from === "edit" && ratedVolunteers.length != 0) && 
		<>
			<label className="addressLabelInre">Volunteer recommendation</label>
            <div className="volunteer-list">
                {ratedVolunteers.map((v) => (
					<div key={v.id} className="onajnajveci" style={{display: 'flex'}}>
                    <div className="volunteer-row-1">
						<div style={{display: 'flex', marginBottom: '5px', justifyContent: 'space-between'}}>
							<div style={{display: 'flex'}}>
                        	<div className="avatar-small" />
                        	<p className="volunteer-name">{v.name} {v.surname}</p>
							</div>
							<p style={{fontWeight: 'bold', marginLeft: '120px'}}>Predicted performance: {Math.round(v.predictedGrade * 100)/100}</p>
						</div>

						<div style={{display: 'flex', marginBottom: '5px', gap: '10px'}}>
                        	<span className="volunteer-email">{v.email} </span>
                        	<span className="volunteer-phone">{v.phone || "No phone"}</span>
						</div>
                        <span className="volunteer-address">
                            {v.address ? `${v.address.city}, ${v.address.country}` : "No address"}
                        </span>
                    </div>
					<div style={{gap: '20px', margin: '10px'}}>
                        <button
                            className="btn-details"
                            onClick={() => handleSelect(v)}
                        >Select</button>
                        <button
                            className="btn-details"
                            onClick={() => handleDetails(v)}
							style={{marginLeft: '20px'}}
                        >Details</button>
					</div>
					</div>
                ))}
            </div>
		</>}
        </div>
    );
};

export default VolunteerSelectPage;
