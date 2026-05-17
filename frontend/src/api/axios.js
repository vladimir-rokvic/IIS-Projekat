import axios from "axios";

const api = axios.create({
	baseURL: "http://localhost:8080/api",
});

// Automatski dodaje JWT token na svaki zahtjev
api.interceptors.request.use((config) => {
	const user = localStorage.getItem("user");
	if (user) {
		const { token } = JSON.parse(user);
		if (token) {
			config.headers.Authorization = `Bearer ${token}`;
		}
	}
	return config;
});

export default api;
