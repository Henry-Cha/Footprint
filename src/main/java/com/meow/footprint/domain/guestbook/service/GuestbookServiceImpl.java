package com.meow.footprint.domain.guestbook.service;

import com.meow.footprint.domain.guestbook.dto.GuestBookRequest;
import com.meow.footprint.domain.guestbook.entity.Guestbook;
import com.meow.footprint.domain.guestbook.repository.GuestbookRepository;
import com.meow.footprint.domain.member.entity.Member;
import com.meow.footprint.global.util.AccountUtil;
import com.meow.footprint.global.util.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GuestbookServiceImpl implements GuestbookService{
    private final GuestbookRepository guestbookRepository;
    private final ImageUploader imageUploader;
    private final AccountUtil accountUtil;

    @Transactional
    @Override
    public void createGuestbook(GuestBookRequest guestBookRequest) {
        Member member = accountUtil.getLoginMember();
        Guestbook guestbook = new Guestbook(guestBookRequest);
        guestbook.setHost(member);
        String uploadPath = imageUploader.upload(guestBookRequest.getPhoto());
        guestbook.setPhoto(uploadPath);
        guestbookRepository.save(guestbook);
    }
}
