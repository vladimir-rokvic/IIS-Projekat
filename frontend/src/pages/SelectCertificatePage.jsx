import { useEffect, useState } from "react";
import api from "../api/axios";
import "./CreateTaskPage.css";

const SelectCertificatePage = () => {
	const [allCertificates, setAllCertificates] = useState([]);
	
	useEffect(() => {
		const fetchAllCertificates = async () => {
			try {
				const res = await api.get('/regiment/allCertificate');
				setAllCertificates(res.data);
			} catch (err) {
				console.log(err);
			};
		};
		fetchAllCertificates();
	}, []);
	return (
		<div style={{padding: '10px'}}>
			{(allCertificates.length !== 0) && allCertificates.map((certificate) => (
				<div className="certificate-item">
					<h1>{certificate.name}</h1>
					<p>{certificate.description}</p>
				</div>
			))}
		</div>
	);
};

export default SelectCertificatePage;
