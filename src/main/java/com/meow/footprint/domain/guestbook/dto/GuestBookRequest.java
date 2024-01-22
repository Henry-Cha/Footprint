package com.meow.footprint.domain.guestbook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GuestBookRequest {
    @NotBlank
    @Length(min = 2,max = 8)
    private String name;
    @NotBlank
    @Length(min = 2,max = 50)
    private String description;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    @NotBlank
    private String addressSigungu;
    @NotBlank
    private String addressDong;
}
