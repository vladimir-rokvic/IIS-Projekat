package com.iis.projekat.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "donors")
public class Donor extends User {

	@Column(name = "is_company", nullable = false)
	private boolean company;

	@Column(name = "company_name")
	private String companyName;

	    @OneToMany(mappedBy = "donor")
	    private List<Donation> donations = new ArrayList<>();

	public boolean isCompany() {
		return company;
	}

	public void setCompany(boolean company) {
		this.company = company;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<Donation> getDonations() {
		return donations;
	}

	public void setDonations(List<Donation> donations) {
		this.donations = donations;
	}
}
