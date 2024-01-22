package com.meow.footprint.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequest(@NotBlank @Email String email, @NotBlank String authCode) {
}
