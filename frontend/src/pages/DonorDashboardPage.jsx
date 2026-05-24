import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import SidebarMenu from "../components/SidebarMenu";
import "./DonorDashboard.css";

const donorMenuItems = [
	{ label: "Home", to: "/donor", end: true },
	{ label: "Explore campaigns", to: "/donor/campaigns" },
	{ label: "My profile", to: "/donor/profile" },
];

const DonorDashboardPage = () => {
	const { logout } = useAuth();
	const navigate = useNavigate();

	const handleLogout = () => {
		logout();
		navigate("/login");
	};

	return (
		<div className="donor-shell">
			<SidebarMenu
				title="Donor"
				items={donorMenuItems}
				onLogout={handleLogout}
			/>
			<main className="donor-main">
				<Outlet />
			</main>
		</div>
	);
};

export default DonorDashboardPage;