package com.meow.footprint.domain.guestbook.service;

import com.meow.footprint.domain.guestbook.dto.*;
import com.meow.footprint.domain.guestbook.entity.Guestbook;
import com.meow.footprint.domain.guestbook.repository.GuestbookRepository;
import com.meow.footprint.domain.member.entity.Member;
import com.meow.footprint.global.result.error.ErrorCode;
import com.meow.footprint.global.result.error.exception.BusinessException;
import com.meow.footprint.global.util.AccountUtil;
import com.meow.footprint.global.util.ImageUploader;
import com.meow.footprint.global.util.QrCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final QrCodeUtil qrCodeUtil;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public void createGuestbook(GuestBookRequest guestBookRequest, MultipartFile photo) {
        Member member = accountUtil.getLoginMember();
        Guestbook guestbook = new Guestbook(guestBookRequest);
        guestbook.setHost(member);
        String uploadPath = imageUploader.upload(photo);
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

    @Transactional
    @Override
    public void deleteGuestbook(long guestbookId) {
        Guestbook guestbook = guestbookRepository.findById(guestbookId).orElseThrow(()-> new BusinessException(ErrorCode.GUESTBOOK_ID_NOT_EXIST));
        accountUtil.checkLoginMember(guestbook.getHost().getId()); // TODO: 2023-12-30 삭제할때 이미지파일도 삭제??
        guestbookRepository.delete(guestbook);
    }

    @Transactional
    @Override
    public void updateGuestbook(long guestbookId, GuestBookRequest guestBookRequest, MultipartFile photo) {
        Guestbook guestbook = guestbookRepository.findById(guestbookId).orElseThrow(()-> new BusinessException(ErrorCode.GUESTBOOK_ID_NOT_EXIST));
        accountUtil.checkLoginMember(guestbook.getHost().getId());
        guestbook.update(guestBookRequest);
        String uploadPath = imageUploader.upload(photo);
        guestbook.setPhoto(uploadPath);
    }

    @Override
    public GuestbookSimpleResponse getGuestbookSimple(long guestbookId) {
        Guestbook guestbook = guestbookRepository.findById(guestbookId).orElseThrow(()-> new BusinessException(ErrorCode.GUESTBOOK_ID_NOT_EXIST));
        return GuestbookSimpleResponse.from(guestbook);
    }

    @Transactional
    @Override
    public GuestbookQrResponse getGuestbookQr(long guestbookId, String qrLink) {
        Guestbook guestbook = guestbookRepository.findById(guestbookId).orElseThrow(()->new BusinessException(ErrorCode.GUESTBOOK_ID_NOT_EXIST));
        if(guestbook.getQrCode() == null || guestbook.getQrCode().isEmpty()){
            String qrCode = qrCodeUtil.qrCodeGenerate(guestbookId,qrLink);
            guestbook.setQrCode(qrCode);
        }
        return new GuestbookQrResponse(guestbook.getQrCode());
    }
}
