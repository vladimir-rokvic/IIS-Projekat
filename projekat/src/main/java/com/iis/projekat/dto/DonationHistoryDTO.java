package com.iis.projekat.dto;

import java.time.LocalDate;

public class DonationHistoryDTO {
	private String project;
	private Double amount;
	private LocalDate paymentDate;
	private String status;

	public DonationHistoryDTO() {
	}

	public DonationHistoryDTO(String project, Double amount, LocalDate paymentDate, String status) {
		this.project = project;
		this.amount = amount;
		this.paymentDate = paymentDate;
		this.status = status;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}