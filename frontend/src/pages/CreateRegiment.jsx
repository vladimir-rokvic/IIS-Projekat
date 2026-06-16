import { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./CreateTaskPage.css";

const CreateRegiment = () => {
	const navigate = useNavigate();
	const [volunteer, setVolunteer] = useState(null);
	const [certificate, setCertificate] = useState(null);

	const description = useRef('');
	const startDate = useRef(null);
	const endDate = useRef(null);
	const groupSize = useRef(null);

	const location = useLocation();

	useEffect(() => {
		if(location.state?.certificate) {
			setCertificate(location.state?.certificate);
		}
		if(location.state?.trainer) {
			setVolunteer(location.state?.trainer);
		}
	}, []);

	const handleSave = async () => {
		body = {
			description: description.current.value,
			startDate: startDate.current.value,
			endDate: endDate.current.value,
			numOfTrainees: groupSize.current.value,
			certificate: certificate,
			trainer: volunteer
		}
		try {
		} catch (err) {
			console.log(err);
		}
	}

	const handleAddVolunteer = () => {
		navigate('/coord/createTask/addVolunteer', {state: {from: "training-create"}})
	}

	return (
		<div className="create-task-page">
            <button className="create-back-btn" style={{width: '140px'}} onClick={() => navigate('/manager')}>
                ← Back to Dashboard
            </button>

            <h1 className="create-title">Create a new training regiment</h1>
            <p className="create-subtitle">Fill in the required information to create a new training regiment.</p>

			{ (certificate) ? (<>

			<label className="addressLabelInre">Trainer information</label>
            <div className="volunteer-section">
                <button className="btn-add-volunteer" style={{marginBottom: '5px'}}
					onClick={handleAddVolunteer}>Select trainer +</button>
                <div className="volunteer-row">
				    {volunteer ? (
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
				    ) : (
					<>
                    	<div className="avatar-small" />
				        <span className="volunteer-placeholder">No trainer selected</span>
					</>
				    )}
                </div>
            </div>

			<label className="addressLabelInre">Regiment description</label>
				<div className="volunteer-section">
				<label>Description</label>
            	<textarea
            	    ref={description}
            	    className="description-input"
					style={{width: '740px'}}
            	    placeholder="Enter the description of the regiment here"
            	/>

            	<div className="date-row">
            	    <div className="v-field">
            	        <label>Start date</label>
            	        <input ref={startDate} type="date" />
            	    </div>
            	    <div className="v-field">
            	        <label>End date</label>
            	        <input ref={endDate} type="date" />
            	    </div>
            	    <div className="v-field" style={{width: '100px'}}>
            	        <label>Group size</label>
            	        <input placeholder="<20" style={{width: '100px'}} ref={groupSize} type="text" />
            	    </div>
            	</div>
			</div>

            <div className="form-actions">
                <button className="btn-cancel" onClick={() => navigate('/manager')}>
                    Cancel
                </button>
                <button className="btn-save" onClick={handleSave}>Save</button>
            </div> </>) : (
			<div className="dashboard-certificate">
				<h1>Select a certificate</h1>
				<p style={{marginTop: '10px', fontSize: '1.3rem'}}>
					Select a certificate to continue creating a regiment
				</p>
				<button className="create-back-btn"
				style={{alignSelf: 'center', marginTop: '15px'}}
				onClick={() => navigate('/selectCertificate')}>Select a certificate</button>
			</div>)}

		</div>
	);
};

export default CreateRegiment;
