package com.digitalmoneyhouse.wallet_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardDTO {

        private Long id;

        @NotBlank(message = "El número de tarjeta no puede estar vacío")
        @Size(min = 16, max = 16, message = "El número de tarjeta debe tener exactamente 16 dígitos")
        @Pattern(regexp = "\\d+", message = "El número de tarjeta solo puede contener números")
        private String cardNumber;

        @NotBlank(message = "El titular de la tarjeta no puede estar vacío")
        private String cardHolder;

        @NotBlank(message = "La fecha de expiración no puede estar vacía")
        @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Formato incorrecto. Use MM/YY")
        private String expirationDate;

        @NotBlank(message = "El tipo de tarjeta no puede estar vacío")
        @Pattern(regexp = "DEBIT|CREDIT", message = "El tipo de tarjeta debe ser DEBIT o CREDIT")
        private String cardType;

    public CardDTO() {
    }

    public CardDTO(Long id, String cardNumber, String cardHolder, String expirationDate, String cardType) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
        this.expirationDate = expirationDate;
        this.cardType = cardType;
    }
}
