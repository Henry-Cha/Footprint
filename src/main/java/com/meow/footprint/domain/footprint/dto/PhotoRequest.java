package com.meow.footprint.domain.footprint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record PhotoRequest(
    @NotNull
    long guestbook,
    @NotBlank @Length(min = 2,max = 8)
    String writer,
    boolean isSecret,
    @NotNull
    double latitude,
    @NotNull
    double longitude) {
}