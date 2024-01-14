package com.meow.footprint.domain.guestbook.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GuestbookDTO {
    private long id;
    private String name;
    private String description;
    private String hostId;
    private String photo;
    private int footprintCount;
    private boolean isUpdate;
    private double latitude;
    private double longitude;
    private String addressSigungu;
    private String addressDong;
    private LocalDateTime createTime;
}
