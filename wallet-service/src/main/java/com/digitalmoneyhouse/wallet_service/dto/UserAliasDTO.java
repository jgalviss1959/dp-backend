package com.digitalmoneyhouse.wallet_service.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAliasDTO {
    private Long id;
    private String alias;
    private String cvu;
}