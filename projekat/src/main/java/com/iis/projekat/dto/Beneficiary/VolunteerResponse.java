package com.iis.projekat.dto.Beneficiary;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VolunteerResponse {
    private Long id;
    private String name;
    private String surname;
}
