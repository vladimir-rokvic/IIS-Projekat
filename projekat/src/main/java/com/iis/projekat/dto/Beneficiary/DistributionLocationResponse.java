package com.iis.projekat.dto.Beneficiary;

import com.iis.projekat.model.Address;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SecondaryRow;

import java.time.LocalTime;

@Getter
@Setter
@Builder
public class DistributionLocationResponse {
    private Long id;
    private String name;
    private int capacity;
    private String type;
    private String city;
    private String street;
    private String country;
    private String contactName;
    private String contactNumber;
    private LocalTime workHoursBegin;
    private LocalTime workHoursEnd;
}
