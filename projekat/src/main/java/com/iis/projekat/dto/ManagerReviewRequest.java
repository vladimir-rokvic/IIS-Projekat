package com.iis.projekat.dto;

/**
 * Menadžer šalje ovu odluku kad pregleda projekat.
 * status mora biti: ODOBREN, NEOPHODNA_IZMENA ili ODBIJEN
 * razlog je obavezan za NEOPHODNA_IZMENA i ODBIJEN.
 */
public class ManagerReviewRequest {
    public String status;
    public String razlog;
}
