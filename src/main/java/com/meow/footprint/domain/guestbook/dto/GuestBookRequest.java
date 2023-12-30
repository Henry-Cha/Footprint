package com.meow.footprint.domain.guestbook.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GuestBookRequest {
    String name;
    String description;
    Double latitude;
    Double longitude;
    String address;
}
