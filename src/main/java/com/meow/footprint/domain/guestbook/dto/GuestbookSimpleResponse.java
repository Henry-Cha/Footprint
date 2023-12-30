package com.meow.footprint.domain.guestbook.dto;

import com.meow.footprint.domain.guestbook.entity.Guestbook;
public record GuestbookSimpleResponse(String name, String description, String hostId, String photo, int footprintCount,
                                      String address) {
    public static GuestbookSimpleResponse from(Guestbook guestbook) {
        return new GuestbookSimpleResponse(guestbook.getName()
                , guestbook.getDescription()
                , guestbook.getHost().getId()
                , guestbook.getPhoto()
                , guestbook.getFootprintCount()
                , guestbook.getAddress());
    }
}