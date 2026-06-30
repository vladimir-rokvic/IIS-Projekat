package com.iis.projekat.dto.Beneficiary;

import com.iis.projekat.model.Beneficiary.DocumentTypeBeneficiary;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DocumentResponse {
    private Long id;
    private DocumentTypeBeneficiary tip;
    private String nazivFajla;
    private boolean aktivan;
    private LocalDateTime datumUploada;
}
