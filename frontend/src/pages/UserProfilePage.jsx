import './UserProfilePage.css';
import LabelField from '../components/LabelField';
import { useEffect, useState } from 'react';
import api from '../api/axios';

const UserProfilePage = () => {
	const [user, setUser] = useState(null);

	const onLoad = () => {
	};

	useEffect(() => {
		const getUser = async () => {
			try{
				const res = await api.get('');
				console.log(res.data);
				setUser(res.data);
			} catch(err) {
				console.log(err);
			}
		};
	});

	return (
		<div className='userProfilePage'>
			<LabelField
				field={}>First name</LabelField>
		</div>
	);
}

export default UserProfilePage;
