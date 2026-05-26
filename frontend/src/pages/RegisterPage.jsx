import "./RegisterPage.css"
import api from "../api/axios";
import { useNavigate } from "react-router-dom";
import { useRef, useState } from "react";

const RegisterPage = () => {
	const email = useRef();
	const password = useRef();
	const confirmPassword = useRef();
	const name = useRef();
	const surname = useRef();
	const phone = useRef();
	const dateOfBirth = useRef();
	const [passwordError, setPasswordError] = useState('');
	const [fieldError, setFieldError] = useState('');

	const navigate = useNavigate()

	const register = async () => {
		if (!name.current.value) { setFieldError('Name is required.'); return; }
		if (!surname.current.value) { setFieldError('Surname is required.'); return; }
		if (!email.current.value) { setFieldError('Email is required.'); return; }
		if (!password.current.value) { setFieldError('Password is required.'); return; }
		if (password.current.value !== confirmPassword.current.value) {
			setPasswordError('Passwords do not match.');
			return;
		}
		setPasswordError('');
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
				<h1 className="org-title">Humanitarian organization</h1>
				<div className="box">
					<h2>Register</h2>

					<div className="form-group">
						<label>Name*</label>
						<input
							type="text"
							placeholder="Enter your name"
							ref={name}
						/>
						{fieldError === 'Name is required.' && <span className="error-text">{fieldError}</span>}
					</div>

					<div className="form-group">
						<label>Surname*</label>
						<input
							type="text"
							placeholder="Enter your surname"
							ref={surname}
						/>
						{fieldError === 'Surname is required.' && <span className="error-text">{fieldError}</span>}
					</div>

					<div className="form-group">
						<label>Email*</label>
						<input
							type="text"
							placeholder="Enter your email address"
							ref={email}
						/>
						{fieldError === 'Email is required.' && <span className="error-text">{fieldError}</span>}
					</div>

					<div className="form-group">
						<label>Password*</label>
						<input
							type="password"
							placeholder="Enter your password"
							ref={password}
						/>
						{fieldError === 'Password is required.' && <span className="error-text">{fieldError}</span>}
					</div>

					<div className="form-group">
						<label>Confirm password*</label>
						<input
							type="password"
							placeholder="Confirm your password"
							ref={confirmPassword}
						/>
						{passwordError && <span className="error-text">{passwordError}</span>}
						{fieldError === 'Confirming password is required.' && <span className="error-text">{fieldError}</span>}
					</div>

					<div className="form-group">
						<label>Phone</label>
						<input
							type="text"
							placeholder="Enter your phone number"
							ref={phone}
						/>
					</div>

					<div className="form-group">
						<label>Date of birth</label>
						<input
							type="date"
							ref={dateOfBirth}
						/>
					</div>

					<button className="login-btn" onClick={register}>Register</button>
				</div>
			</div>
		</>
	);
}

export default RegisterPage;
