import { useEffect, useState } from "react";
import api from "../api/axios";
import "./CreateTaskPage.css";
import { useNavigate } from "react-router-dom";

const SelectCertificatePage = () => {
	const [allCertificates, setAllCertificates] = useState([]);
	const navigate = useNavigate();
	
	useEffect(() => {
		const fetchAllCertificates = async () => {
			try {
				const res = await api.get('/regiment/allCertificates');
				setAllCertificates(res.data);
			} catch (err) {
				console.log(err);
			};
		};
		fetchAllCertificates();
	}, []);

	const handleSelect = (certificate) => {
		navigate('/manager/createRegiment', {state: {certificate: certificate}});
	}

	return (
		<div style={{padding: '10px'}}>
			{(allCertificates.length !== 0) && allCertificates.map((certificate) => (
				<div className="certificate-item">
					<h1>{certificate.name}</h1>
					<p style={{marginTop: '20px'}}>{certificate.description}</p>
					<button className="btn-select-certificate"
					onClick={() => handleSelect(certificate)}>Select</button>
				</div>
			))}
		</div>
	);
};

export default SelectCertificatePage;
