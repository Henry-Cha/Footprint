package com.meow.footprint.domain.guestbook.service;

import com.meow.footprint.domain.guestbook.dto.GuestBookRequest;
import com.meow.footprint.domain.guestbook.dto.GuestbookDTO;
import com.meow.footprint.domain.guestbook.entity.Guestbook;
import com.meow.footprint.domain.guestbook.repository.GuestbookRepository;
import com.meow.footprint.domain.member.entity.Member;
import com.meow.footprint.global.util.AccountUtil;
import com.meow.footprint.global.util.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GuestbookServiceImpl implements GuestbookService{
    private final GuestbookRepository guestbookRepository;
    private final ImageUploader imageUploader;
    private final AccountUtil accountUtil;
    private final ModelMapper modelMapper;

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

    @Override
    public List<GuestbookDTO> getGuestbookList(String memberId) {
        accountUtil.checkLoginMember(memberId);
        List<Guestbook> guestbooks = guestbookRepository.findByHostId(memberId);
        return guestbooks.stream()
                .map(guestbook -> {
                    GuestbookDTO dto = modelMapper.map(guestbook,GuestbookDTO.class);
                    dto.setHostId(guestbook.getHost().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
