package com.iis.projekat.dto;

import com.iis.projekat.model.Donation;
import com.iis.projekat.model.DonationType;
import com.iis.projekat.model.NotificationChannel;
import com.iis.projekat.model.NotificationFrequency;
import com.iis.projekat.model.Periodicity;

import java.time.LocalDate;

public class DonationDTO {
    private Long id;
    private Double amount;
    private DonationType donationType;
    private LocalDate paymentDate;
    private Periodicity periodicity;
    private boolean wantsNotifications;
    private NotificationFrequency notificationFrequency;
    private NotificationChannel notificationChannel;
    private Long donorId;

    private Long projectId;
    private Long campaignId;


    public DonationDTO() {}

    public DonationDTO(Donation d) {
        this.id = d.getId();
        this.amount = d.getAmount();
        this.donationType = d.getDonationType();
        this.paymentDate = d.getPaymentDate();
        this.periodicity = d.getPeriodicity();
        this.wantsNotifications = d.isWantsNotifications();
        this.notificationFrequency = d.getNotificationFrequency();
        this.notificationChannel = d.getNotificationChannel();
        if(d.getDonor() != null) this.donorId = d.getDonor().getId();
        if (d.getProject() != null) this.projectId = d.getProject().getId();
        if (d.getCampaign() != null) this.campaignId = d.getCampaign().getId();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public DonationType getDonationType() { return donationType; }
    public void setDonationType(DonationType donationType) { this.donationType = donationType; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public Periodicity getPeriodicity() { return periodicity; }
    public void setPeriodicity(Periodicity periodicity) { this.periodicity = periodicity; }

    public boolean isWantsNotifications() { return wantsNotifications; }
    public void setWantsNotifications(boolean wantsNotifications) { this.wantsNotifications = wantsNotifications; }

    public NotificationFrequency getNotificationFrequency() { return notificationFrequency; }
    public void setNotificationFrequency(NotificationFrequency notificationFrequency) { this.notificationFrequency = notificationFrequency; }

    public NotificationChannel getNotificationChannel() { return notificationChannel; }
    public void setNotificationChannel(NotificationChannel notificationChannel) { this.notificationChannel = notificationChannel; }

    public Long getDonorId() { return donorId; }
    public void setDonorId(Long donorId) { this.donorId = donorId; }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }
}
