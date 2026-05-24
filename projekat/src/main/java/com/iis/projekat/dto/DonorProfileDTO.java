package com.iis.projekat.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonorProfileDTO {
	private Long id;
	private String name;
	private String surname;
	private String email;
	private String phone;
	private boolean company;
	private String companyName;
	private Double totalDonated;
	private Long donationsMade;
	private LocalDate lastDonationDate;
	private List<DonationHistoryDTO> donationHistory = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

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

	public Double getTotalDonated() {
		return totalDonated;
	}

	public void setTotalDonated(Double totalDonated) {
		this.totalDonated = totalDonated;
	}

	public Long getDonationsMade() {
		return donationsMade;
	}

	public void setDonationsMade(Long donationsMade) {
		this.donationsMade = donationsMade;
	}

	public LocalDate getLastDonationDate() {
		return lastDonationDate;
	}

	public void setLastDonationDate(LocalDate lastDonationDate) {
		this.lastDonationDate = lastDonationDate;
	}

	public List<DonationHistoryDTO> getDonationHistory() {
		return donationHistory;
	}

	public void setDonationHistory(List<DonationHistoryDTO> donationHistory) {
		this.donationHistory = donationHistory;
	}
}