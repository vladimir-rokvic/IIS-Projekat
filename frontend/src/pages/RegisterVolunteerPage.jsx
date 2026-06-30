import "./RegisterVolunteerPage.css"
import LabelInput from "../components/LabelInput";
import { useRef } from "react";
import api from "../api/axios";
import { useNavigate } from "react-router-dom";

const RegisterVolunteerPage = () => {

	const firstName = useRef();
    const lastName = useRef();
    const dob = useRef();
    const street = useRef();
    const city = useRef();
    const country = useRef();
    const phone = useRef();
    const email = useRef();
    const password = useRef();
    const confirmPassword = useRef();

	const navigate = useNavigate();

	const handleRegister = async () => {
        if (password.current.value !== confirmPassword.current.value) {
            alert("Passwords do not match");
            return;
        }

        try {
            const body = {
                name: firstName.current.value,
                surname: lastName.current.value,
                dob: dob.current.value,
                street: street.current.value,
                city: city.current.value,
                country: country.current.value,
                phone: phone.current.value,
                email: email.current.value,
                password: password.current.value,
            };
			const res = await api.post("/volunteer", body);
            console.log(res.data);
			navigate(-1);
        } catch (err) {
            console.log(err);
        }
    }

    return (
        <div className="registerVolunteer">
			<div className="headeridk">
			<div>
            <h1>Register a volunteer</h1>
			<label className="labelidk">Register a volunteer to work for our organization</label>
			</div>
			<button onClick={() => navigate(-1)} className="btn-primary" style={{
				margin: '25px',
			}}>
				← Back to homepage
			</button>
			</div>
            <div className="form">
				<div className="input-group">
				<label className="addressLabelInre">Basic information</label>
                <div className="row">
                    <LabelInput 
						ref={firstName}
						placeholerText="Enter first name" 
						inputType="text">First name</LabelInput>
                    <LabelInput 
						ref={lastName}
						placeholerText="Enter last name" 
						inputType="text">Last name</LabelInput>
					<br />
                    <LabelInput 
						ref={dob}
						inputType="date"
					>Date of birth</LabelInput>
                </div>
				</div>

				<div className="input-group">
					<label className="addressLabelInre">Address</label>
                	<div className="row">
                    	<LabelInput 
							ref={street}
							placeholerText="Enter street" 
							inputType="text">Street</LabelInput>
                    	<LabelInput 
							ref={city}
							placeholerText="Enter city" 
							inputType="text">City</LabelInput>
                    	<LabelInput 
							ref={country}
							placeholerText="Enter country" 
							inputType="text">Country</LabelInput>
					</div>
                </div>

				<div className="input-group">
				<label className="addressLabelInre">Contact information</label>
                <div className="row">
                    <LabelInput 
						ref={phone}
						placeholerText="Enter phone number" 
						inputType="text">Phone</LabelInput>
                    <LabelInput 
						ref={email}
						placeholerText="Enter email" 
						inputType="text">Email</LabelInput>
                </div>
				</div>

				<div className="input-group">
				<label className="addressLabelInre">Password</label>
				<div className="row">
					<LabelInput
						ref={password}
						inputType={"password"}
						placeholerText={"Enter password here"}>Password</LabelInput>
					<LabelInput
						ref={confirmPassword}
						inputType={"password"}
						placeholerText={"Please confirm password"}>
					Confirm Password</LabelInput>
				</div>
				</div>
            </div>
				<button 
					className="submitBtn"
					onClick={handleRegister}>
				Submit</button>
        </div>
    );
}

export default RegisterVolunteerPage;
