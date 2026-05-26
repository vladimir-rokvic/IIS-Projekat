import "./CampaignCoordinatorDashboard.css";

const CampaignCoordinatorStatisticsPage = () => {
	return (
		<div className="campaign-content">
			<h1 className="campaign-page-title" style={{ marginBottom: 30 }}>Campaign statistics</h1>
			<section className="campaign-stats-layout">
				<div className="campaign-chart-card">
					<div className="campaign-chart-title">Donation trends</div>
					<div className="campaign-line-chart" />
				</div>
				<div className="campaign-chart-card">
					<div className="campaign-chart-title">Donations per category</div>
					<div className="campaign-pie-chart" />
				</div>
				<div className="campaign-chart-card" style={{ gridColumn: "1 / span 1" }}>
					<div className="campaign-chart-title">Campaign comparison</div>
					<div className="campaign-bar-chart">
						<div className="campaign-bar-group">
							<div className="campaign-bar" style={{ height: 92 }} />
							<div className="campaign-bar alt" style={{ height: 112 }} />
							<div className="campaign-bar" style={{ height: 132 }} />
							<div className="campaign-bar alt" style={{ height: 120 }} />
						</div>
					</div>
				</div>
			</section>
		</div>
	);
};

export default CampaignCoordinatorStatisticsPage;