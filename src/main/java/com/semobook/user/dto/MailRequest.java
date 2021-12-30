package com.semobook.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class MailRequest {
    @NotEmpty
    private String address;
    @NotEmpty
    private String title;
    @NotEmpty
    private String message;
}
