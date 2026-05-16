import { useRef } from "react";
import "./RegisterPage.css"
import api from "../api/axios";
import { useNavigate } from "react-router-dom";


const RegisterPage = () => {
	//moze i useState, nisam siguran oko razlika
	const email = useRef();
	const password = useRef();
	const confirmPassword = useRef();
	const name = useRef();
	const surname = useRef();
	const phone = useRef();
	const dateOfBirth = useRef();

	const navigate = useNavigate()

	const register = async () => {
		try {
			const res = await api.post('/auth/register', {
				email: email.current.value,
				password: password.current.value,
				confirmPassword: confirmPassword.current.value,
				name: name.current.value,
				surname: surname.current.value,
				phone: phone.current.value,
				dateOfBirth: dateOfBirth.current.value,
			});
			console.log(res.data);
			navigate('/login');
		} catch (err) {
			console.log(err);
		}
	}

	return (
		<>
			<div className="register-page">
				<div className="box">
					<label>Name</label>
					<input 
						type="text"
						ref={name}
					/>

					<label>Surname</label>
					<input 
						type="text" 
						ref={surname}
					/>

					<label>Email</label>
					<input 
						type="text" 
						ref={email}
					/>

					<label>Password</label>
					<input 
						type="password"
						ref={password}
					/>

					<label>Confirm password</label>
					<input 
						type="password"
						ref={confirmPassword}
					/>

					<label>Phone</label>
					<input 
						type="text" 
						ref={phone}
					/>

					<label>Date of birth</label>
					<input 
						type="date" 
						ref={dateOfBirth}
					/>

					<button onClick={register} >Register</button>
				</div>
			</div>
		</>
	);
}

export default RegisterPage;
