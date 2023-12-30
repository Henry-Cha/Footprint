package com.meow.footprint.domain.guestbook.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuestBookRequest {
    String name;
    String description;
    MultipartFile photo;
    double latitude;
    double longitude;
    String address;
}
