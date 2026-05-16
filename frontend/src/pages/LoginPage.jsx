import { useState } from "react";
import api from "../api/axios";
import "./LoginPage.css"
import { useNavigate } from "react-router-dom";

const LoginPage = () => {
	const [email, setEmail] = useState('');
	const [password, setPassword] = useState('');

	const navigate = useNavigate();

	const LoginClick = async () => {
		try{
			const res = await api.post("/auth/login", {email, password});
			console.log(res.data);
			navigate('/');
		} catch (e){
			console.log(e);
		}
	}

	return (
		<>
			<div className="login-page">
				<div className="box">
					<label>Email: </label>
					<input 
						type="text" 
						value={email}
						onChange={(e) => {setEmail(e.target.value)}}
					/>

					<label>Password: </label>
					<input 
						type="password" 
						value={password}
						onChange={(e) => {setPassword(e.target.value)}}

					/>
					<button onClick={LoginClick}>Log in</button>
				</div>
			</div>
		</>
	);
}

export default LoginPage;
