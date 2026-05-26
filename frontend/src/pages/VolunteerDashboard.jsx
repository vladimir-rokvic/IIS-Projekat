import { useNavigate } from 'react-router-dom';
import './VolunteerDashboard.css';

const VolunteerDashboard = () => {
	const navigate = useNavigate();
	const user = JSON.parse(localStorage.getItem("user"));

	const goToProfile = () => {
		navigate('/profile');
	}

	return (
		<div className='volunteer-dash'>
			<div className='top-bar'>
                <span className='user-name' onClick={goToProfile}>
                    {user?.name} {user?.surname}
                </span>
            </div>

			<h1>Organization name</h1>
			<h2>Organization bio</h2>
			<div className='volunteer-dash-btns'>
				<button>See Tasks</button>
				<button>See Ratings</button>
				<button>See Training</button>
			</div>
		</div>
	);
};

export default VolunteerDashboard;
