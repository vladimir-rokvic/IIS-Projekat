import "./RegisterVolunteerPage.css"
import LabelInput from "../components/LabelInput";
import { useRef } from "react";
import api from "../api/axios";

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
        } catch (err) {
            console.log(err);
        }
    }

    return (
        <div className="registerVolunteer">
            <h1>Register Volunteer</h1>
            <div className="form">
                <div className="row">
                    <LabelInput 
						ref={firstName}
						placeholerText="Enter first name" 
						inputType="text">First name</LabelInput>
                    <LabelInput 
						ref={lastName}
						placeholerText="Enter last name" 
						inputType="text">Last name</LabelInput>
                </div>
                <div className="row">
                    <LabelInput 
						ref={dob}
						inputType="date"
					>Date of birth</LabelInput>
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
				<button 
					className="submitBtn"
					onClick={handleRegister}>
				Submit</button>
            </div>
        </div>
    );
}

export default RegisterVolunteerPage;
