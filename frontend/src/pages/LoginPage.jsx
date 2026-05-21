import { useState } from "react";
import api from "../api/axios";
import "./LoginPage.css";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const LoginPage = () => {
	const [email, setEmail] = useState('');
	const [password, setPassword] = useState('');
	const [error, setError] = useState('');

	const navigate = useNavigate();
	const { login } = useAuth();

	const LoginClick = async () => {
		setError('');
		try {
			const res = await api.post("/auth/login", { email, password });
			login(res.data);
			const role = res.data.role;
			if (role === "COORDINATOR") navigate('/');
			else if (role === "MANAGER") navigate('/manager');
			else if (role === "VOLUNTEER") navigate('/volunteer')
			else navigate('/');
		} catch (e) {
			setError('Invalid email or password.');
		}
	};

	return (
		<div className="login-page">
			<h1 className="org-title">Humanitarian organization</h1>
			<div className="box">
				<h2>Log in</h2>
				<div className="form-group">
					<label>Email</label>
					<input type="text" placeholder="Enter your email address" value={email} onChange={(e) => setEmail(e.target.value)} />
				</div>
				<div className="form-group">
					<label>Password</label>
					<input type="password" placeholder="Enter your password" value={password} onChange={(e) => setPassword(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && LoginClick()} />
				</div>
				{error && <span className="error-text">{error}</span>}
				<button className="login-btn" onClick={LoginClick}>Login</button>
			</div>
		</div>
	);
};

export default LoginPage;
