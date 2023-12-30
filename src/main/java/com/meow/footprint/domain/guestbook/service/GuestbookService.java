package com.meow.footprint.domain.guestbook.service;

import com.meow.footprint.domain.guestbook.dto.GuestBookRequest;
import com.meow.footprint.domain.guestbook.dto.GuestbookDTO;

import java.util.List;

public interface GuestbookService {
    void createGuestbook(GuestBookRequest guestBookRequest);

    List<GuestbookDTO> getGuestbookList(String memberId);
}
