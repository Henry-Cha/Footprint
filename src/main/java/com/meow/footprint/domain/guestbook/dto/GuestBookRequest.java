package com.meow.footprint.domain.guestbook.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GuestBookRequest {
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private String addressSigungu;
    private String addressDong;
}
