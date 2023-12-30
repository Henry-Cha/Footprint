package com.meow.footprint.domain.guestbook.service;

import com.meow.footprint.domain.guestbook.dto.GuestBookRequest;
import com.meow.footprint.domain.guestbook.dto.GuestbookDTO;
import com.meow.footprint.domain.guestbook.dto.GuestbookSimpleResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GuestbookService {
    void createGuestbook(GuestBookRequest guestBookRequest, MultipartFile photo);

    List<GuestbookDTO> getGuestbookList(String memberId);

    void deleteGuestbook(long guestbookId);

    void updateGuestbook(long guestbookId, GuestBookRequest guestBookRequest, MultipartFile photo);

    GuestbookSimpleResponse getGuestbookSimple(long guestbookId);
}
