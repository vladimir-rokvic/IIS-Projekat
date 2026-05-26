import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import SidebarMenu from "../components/SidebarMenu";
import "./CampaignCoordinatorDashboard.css";

const CampaignCoordinatorDashboard = () => {
	const { user, logout } = useAuth();
	const navigate = useNavigate();

	const menuItems = [
		{ label: "Dashboard", to: "/campaign-coordinator", end: true },
		{ label: "All campaigns", to: "/campaign-coordinator/all-campaigns" },
		{ label: "Create campaign", to: "/campaign-coordinator/create-campaign" },
		{ label: "Statistics", to: "/campaign-coordinator/statistics" },
	];

	return (
		<div className="campaign-shell">
			<SidebarMenu
				title="Campaign manager"
				items={menuItems}
				onLogout={() => { logout(); navigate('/login'); }}
			/>
			<main className="campaign-main">
				<Outlet />
			</main>
		</div>
	);
};

export default CampaignCoordinatorDashboard;