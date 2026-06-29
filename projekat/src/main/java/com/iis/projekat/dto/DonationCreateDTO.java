package com.iis.projekat.dto;

import com.iis.projekat.model.DonationType;
import com.iis.projekat.model.NotificationChannel;
import com.iis.projekat.model.NotificationFrequency;
import com.iis.projekat.model.Periodicity;

import java.time.LocalDate;

public class DonationCreateDTO {
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


    public DonationCreateDTO() {}

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
