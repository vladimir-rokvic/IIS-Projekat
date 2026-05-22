import './UserProfilePage.css';
import { useEffect, useState } from 'react';
import VolunterProfilePage from './VolunteerProfilePage';

const UserProfilePage = () => {
	const [role, setRole] = useState(null);

	useEffect(() => {
		const u = JSON.parse(localStorage.getItem("user"));
		setRole(u.role);
	}, []);

	//samo dodavajte ovde braco
	if (role === "VOLUNTEER") return <VolunterProfilePage />;
	else return <h1>Proveri UserProfile.jsx</h1>
}

export default UserProfilePage;
