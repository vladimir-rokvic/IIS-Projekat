import "./CampaignCoordinatorDashboard.css";
import { useNavigate } from "react-router-dom";

import {
	LineChart,
	Line,
	XAxis,
	YAxis,
	CartesianGrid,
	Tooltip,
	ResponsiveContainer,
	PieChart,
	Pie,
	Cell,
	BarChart,
	Bar,
	Legend,
} from "recharts";

const CampaignCoordinatorStatisticsPage = () => {
	const navigate = useNavigate();

	// -----------------------
	// Dummy data (replace later with API)
	// -----------------------
	const donationTrendData = [
		{ month: "Jan", amount: 1200 },
		{ month: "Feb", amount: 1800 },
		{ month: "Mar", amount: 2400 },
		{ month: "Apr", amount: 2100 },
		{ month: "May", amount: 3200 },
		{ month: "Jun", amount: 4100 },
	];

	const categoryData = [
		{ name: "Food Aid", value: 4000 },
		{ name: "Education", value: 2500 },
		{ name: "Medical", value: 3000 },
		{ name: "Housing", value: 1500 },
	];

	const campaignComparisonData = [
		{ name: "Campaign A", raised: 12000, goal: 15000 },
		{ name: "Campaign B", raised: 8000, goal: 10000 },
		{ name: "Campaign C", raised: 15000, goal: 18000 },
		{ name: "Campaign D", raised: 6000, goal: 12000 },
	];

	const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042"];

	return (
		<div className="campaign-content">
			<div className="campaign-header" style={{ marginTop: 20 }}>
				<div
					style={{
						display: "flex",
						justifyContent: "space-between",
						alignItems: "center",
						marginBottom: 20,
					}}
				>
					<h1
						className="campaign-page-title"
						style={{ marginBottom: 34 }}
					>
						Campaign statistics
					</h1>

					<button
						className="btn-primary"
						onClick={() => navigate("/campaign-coordinator")}
					>
						Back to dashboard
					</button>
				</div>
			</div>

			<section className="campaign-stats-layout">
				{/* LINE CHART */}
				<div className="campaign-chart-card">
					<div className="campaign-chart-title">
						Donation trends
					</div>

					<ResponsiveContainer width="100%" height={300}>
						<LineChart data={donationTrendData}>
							<CartesianGrid strokeDasharray="3 3" />
							<XAxis dataKey="month" />
							<YAxis />
							<Tooltip />
							<Line
								type="monotone"
								dataKey="amount"
								stroke="#8884d8"
								strokeWidth={3}
							/>
						</LineChart>
					</ResponsiveContainer>
				</div>

				{/* PIE CHART */}
				<div className="campaign-chart-card">
					<div className="campaign-chart-title">
						Donations per category
					</div>

					<ResponsiveContainer width="100%" height={300}>
						<PieChart>
							<Pie
								data={categoryData}
								dataKey="value"
								nameKey="name"
								outerRadius={100}
								label
							>
								{categoryData.map((entry, index) => (
									<Cell
										key={`cell-${index}`}
										fill={
											COLORS[
												index % COLORS.length
											]
										}
									/>
								))}
							</Pie>
							<Tooltip />
							<Legend />
						</PieChart>
					</ResponsiveContainer>
				</div>

				{/* BAR CHART */}
				<div className="campaign-chart-card">
					<div className="campaign-chart-title">
						Campaign comparison
					</div>

					<ResponsiveContainer width="100%" height={300}>
						<BarChart data={campaignComparisonData}>
							<CartesianGrid strokeDasharray="3 3" />
							<XAxis dataKey="name" />
							<YAxis />
							<Tooltip />
							<Legend />
							<Bar dataKey="raised" fill="#82ca9d" />
							<Bar dataKey="goal" fill="#8884d8" />
						</BarChart>
					</ResponsiveContainer>
				</div>
			</section>
		</div>
	);
};

export default CampaignCoordinatorStatisticsPage;