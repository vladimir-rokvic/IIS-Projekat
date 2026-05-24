import { useEffect, useState } from "react";
import api from "../api/axios";
import "./DonorDashboard.css";

const DonorProfilePage = () => {
	const [donor, setDonor] = useState(null);

	useEffect(() => {
		const fetchDonor = async () => {
			try {
				const savedUser = JSON.parse(localStorage.getItem("user"));
				if (!savedUser?.id) return;

				const res = await api.get(`/donors/${savedUser.id}`);
				setDonor(res.data);
			} catch (error) {
				console.log(error);
			}
		};

		fetchDonor();
	}, []);

	if (!donor) {
		return (
			<div className="donor-content">
				<h1 className="donor-page-title">My profile</h1>
				<p className="donor-small-text">Loading profile...</p>
			</div>
		);
	}

	return (
		<div className="donor-content">
			<h1 className="donor-page-title">My profile</h1>

			<section className="donor-panel donor-profile-card">
				<div className="donor-profile-top">
					<div className="donor-avatar" />
					<div>
						<div className="donor-profile-name">{donor.name} {donor.surname}</div>
						<div className="donor-profile-email">{donor.email}</div>
						<div className="donor-profile-meta">member since 1/1/2001</div>
						<div className="donor-profile-stats">
							<div className="donor-profile-stat">
								<div>
									<span className="donor-small-text">Total donated</span>
									<strong>{new Intl.NumberFormat("en-US").format(donor.totalDonated || 0)}$</strong>
								</div>
							</div>
							<div className="donor-profile-stat">
								<div>
									<span className="donor-small-text">Donations made</span>
									<strong>{donor.donationsMade || 0}</strong>
								</div>
							</div>
							<div className="donor-profile-stat">
								<div>
									<span className="donor-small-text">Last donation</span>
									<strong>{donor.lastDonationDate ? new Date(donor.lastDonationDate).toLocaleDateString("en-GB") : "-"}</strong>
								</div>
							</div>
						</div>
					</div>
				</div>
			</section>

			<section className="donor-panel donor-history-panel">
				<h2 className="donor-section-title">Donation history</h2>
				<table className="donor-history-table">
					<thead>
						<tr>
							<th>Project</th>
							<th>Amount</th>
							<th>Date</th>
							<th>Status</th>
						</tr>
					</thead>
					<tbody>
						{(donor.donationHistory || []).map((row, index) => (
							<tr key={`${row.project}-${index}`}>
								<td>{row.project}</td>
								<td>{row.amount != null ? `$${new Intl.NumberFormat("en-US").format(row.amount)}` : "-"}</td>
								<td>{row.paymentDate ? new Date(row.paymentDate).toLocaleDateString("en-GB") : "-"}</td>
								<td>{row.status || "-"}</td>
							</tr>
						))}
						{Array.from({ length: Math.max(0, 5 - (donor.donationHistory || []).length) }).map((_, index) => (
							<tr key={`empty-${index}`}>
								<td>-</td>
								<td>-</td>
								<td>-</td>
								<td>-</td>
							</tr>
						))}
					</tbody>
				</table>
			</section>
		</div>
	);
};

export default DonorProfilePage;