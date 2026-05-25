import { NavLink } from "react-router-dom";
import "./SidebarMenu.css";

const SidebarMenu = ({ title, items, onLogout }) => {
	return (
		<aside className="sidebar-menu">
			<div className="sidebar-title">{title}</div>
			<nav className="sidebar-nav">
				{items.map((item) => (
					<NavLink
						key={item.to}
						to={item.to}
						end={item.end}
						className={({ isActive }) => `sidebar-link${isActive ? " active" : ""}`}
					>
						<span>{item.label}</span>
						<span className="sidebar-arrow">→</span>
					</NavLink>
				))}
			</nav>
			<div className="sidebar-footer">
				<button className="sidebar-logout" onClick={onLogout} type="button">
					Log out
				</button>
			</div>
		</aside>
	);
};

export default SidebarMenu;