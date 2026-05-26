import "./DonorDashboard.css";
import { useNavigate } from "react-router-dom";

const stats = [
	{ label: "Total donated", value: "6,400$" },
	{ label: "Projects funded", value: "8" },
	{ label: "People helped", value: "120" },
	{ label: "Donations made", value: "23" },
];

const projects = [
	{ name: "Project name", raised: "Raised: $$", goal: "Goal: $$", description: "Provided x for 500 people in rural communities." },
	{ name: "Project name", raised: "Raised: $$", goal: "Goal: $$", description: "Provided x for 500 people in rural communities." },
	{ name: "Project name", raised: "Raised: $$", goal: "Goal: $$", description: "Provided x for 500 people in rural communities." },
];

const donorBars = [72, 58, 81];

const DonorHomePage = () => {
	const navigate = useNavigate();
	return (
		<div className="donor-content">
			<div style={{display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 28}}>
				<h1 className="donor-page-title" style={{marginBottom: 0}}>Welcome back!</h1>
			</div>

			<section className="donor-stats-grid" aria-label="Donor summary stats">
				{stats.map((stat) => (
					<div key={stat.label} className="donor-stat-card">
						<div className="donor-stat-label">{stat.label}</div>
						<div className="donor-stat-value">{stat.value}</div>
					</div>
				))}
			</section>

			<section className="donor-section">
				<h2 className="donor-section-title">Projects you made possible</h2>
				<div className="donor-project-grid">
					{projects.map((project) => (
						<div key={project.name + project.description} className="donor-project-card donor-panel">
							<div className="donor-project-name">{project.name}</div>
							<div className="donor-progress-row">
								<span>{project.raised}</span>
								<span>{project.goal}</span>
							</div>
							<div className="donor-progress-bar">
								<div className="donor-progress-fill" style={{ width: "68%" }} />
							</div>
							<p className="donor-small-text">{project.description}</p>
						</div>
					))}
				</div>
			</section>

			<section className="donor-section donor-chart-panel donor-panel">
				<h2 className="donor-section-title">Where your help was distributed</h2>
				<div className="donor-chart">
					<div className="donor-chart-bars">
						{donorBars.map((value) => (
							<div key={value} className="donor-bar-row">
								<div className="donor-bar-track">
									<div className="donor-bar-fill" style={{ width: `${value}%` }} />
								</div>
							</div>
						))}
					</div>
					<div className="donor-bar-labels">
						<span>Education</span>
						<span>Healthcare</span>
						<span>Community support</span>
					</div>
				</div>
			</section>
		</div>
	);
};

export default DonorHomePage;