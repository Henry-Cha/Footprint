package com.meow.footprint.domain.guestbook.service;

import com.meow.footprint.domain.guestbook.dto.GuestBookRequest;

public interface GuestbookService {
    void createGuestbook(GuestBookRequest guestBookRequest);
}
