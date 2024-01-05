package com.meow.footprint.domain.footprint.service;

import com.meow.footprint.domain.footprint.dto.*;
import com.meow.footprint.domain.footprint.entity.Footprint;
import com.meow.footprint.domain.footprint.entity.Photo;
import com.meow.footprint.domain.footprint.repository.FootprintRepository;
import com.meow.footprint.domain.footprint.repository.PhotoRepository;
import com.meow.footprint.domain.guestbook.entity.Guestbook;
import com.meow.footprint.domain.guestbook.repository.GuestbookRepository;
import com.meow.footprint.domain.footprint.dto.PhotoRequest;
import com.meow.footprint.global.result.error.exception.BusinessException;
import com.meow.footprint.global.util.AccountUtil;
import com.meow.footprint.global.util.ImageUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.meow.footprint.global.result.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FootprintServiceImpl implements FootprintService{
    private final FootprintRepository footprintRepository;
    private final GuestbookRepository guestbookRepository;
    private final PhotoRepository photoRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountUtil accountUtil;
    private final ImageUploader imageUploader;
    private static final int DEGREE = 100; //발자국 작성 가능 범위

    @Transactional
    @Override
    public void createFootprint(FootprintRequest footprintRequest) {
        Footprint footprint = modelMapper.map(footprintRequest,Footprint.class);
        Guestbook guestbook = guestbookRepository
                .findById(footprintRequest.guestbook())
                .orElseThrow(()-> new BusinessException(GUESTBOOK_ID_NOT_EXIST));
        if(!checkLocation(guestbook.getLatitude()
                ,guestbook.getLongitude()
                ,footprintRequest.latitude()
                ,footprintRequest.longitude())){
            throw new BusinessException(OUT_OF_AREA);
        }
        footprint.encodingPassword(passwordEncoder);
        guestbook.setUpdate(true);
        footprint.setGuestbook(guestbook);
        footprintRepository.save(footprint);
    }

    @Transactional
    @Override
    public FootprintResponse getSecretFootprint(long footprintId, FootprintPassword footprintPassword) {
        Footprint footprint = checkFootprintAuthority(footprintId, footprintPassword);
        footprint.setChecked(true);
        return FootprintResponse.from(footprint);
    }

    @Override
    public FootprintByDateSliceDTO getFootprintListByDate(String guestbookId, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Slice<FootprintResponse> responseSlice = footprintRepository.getFootprintListByDate(guestbookId,pageable);

        List<FootprintByDateDTO> footprintByDateDTOList = responseSlice.stream()
                .collect(Collectors.groupingBy(FootprintResponse::getCreateDate))
                .entrySet().stream()
                .map(entry -> new FootprintByDateDTO(entry.getKey(),entry.getValue()))
                .collect(Collectors.toList());

        return new FootprintByDateSliceDTO(footprintByDateDTOList
                ,responseSlice.getNumber()
                ,responseSlice.getSize()
                ,responseSlice.isFirst()
                ,responseSlice.isLast());
    }

    @Transactional
    @Override
    public void deleteFootprint(long footprintId, FootprintPassword footprintPassword) {
        Footprint footprint = checkFootprintAuthority(footprintId, footprintPassword);
        footprintRepository.delete(footprint);
    }

    @Transactional
    @Override
    public void readCheckFootprint(long footprintId) {
        Footprint footprint = footprintRepository.findById(footprintId).orElseThrow(()->new BusinessException(FOOTPRINT_ID_NOT_EXIST));
        try {
            String loginMemberId = accountUtil.getLoginMemberId();
            if(footprint.getGuestbook().getHost().getId().equals(loginMemberId)){
                footprint.setChecked(true);
                footprintRepository.save(footprint);
            }
        }catch (RuntimeException e){
            log.info("토큰없음");
        }
    }

    @Transactional
    @Override
    public void createPhoto(PhotoRequest photoRequest, MultipartFile photo) {
        Photo photoEntity = modelMapper.map(photoRequest,Photo.class);
        Guestbook guestbook = guestbookRepository
                .findById(photoRequest.guestbook())
                .orElseThrow(()-> new BusinessException(GUESTBOOK_ID_NOT_EXIST));
        if(!checkLocation(guestbook.getLatitude()
                ,guestbook.getLongitude()
                ,photoRequest.latitude()
                ,photoRequest.longitude())){
            throw new BusinessException(OUT_OF_AREA);
        }
        photoEntity.encodingPassword(passwordEncoder);
        guestbook.setUpdate(true);
        photoEntity.setGuestbook(guestbook);
        String uploadPath = imageUploader.upload(photo);
        photoEntity.setFileName(uploadPath);
        photoRepository.save(photoEntity);
    }

    @Transactional
    @Override
    public void deletePhoto(long photoId, FootprintPassword footprintPassword) {
        Photo photo = photoRepository.findById(photoId).orElseThrow(()-> new BusinessException(PHOTO_ID_NOT_EXIST));
        String loginId = null;
        try {
            loginId = accountUtil.getLoginMemberId();
        }catch (RuntimeException e){
            log.info("토큰없음");
        }
        if(!passwordEncoder.matches(footprintPassword.password(), photo.getPassword())
                && !photo.getGuestbook().getHost().getId().equals(loginId)) {
            throw new BusinessException(FORBIDDEN_ERROR);
        }
        // TODO: 2024-01-05 코드 리팩토링 필요
        photoRepository.delete(photo);
    }

    @Override
    public FootprintByDateSliceDTO getPhotoListByDate(String guestbookId, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Slice<PhotoResponse> responseSlice = photoRepository.getPhotoListByDate(guestbookId,pageable);

        List<FootprintByDateDTO> footprintByDateDTOList = responseSlice.stream()
                .collect(Collectors.groupingBy(PhotoResponse::getCreateDate))
                .entrySet().stream()
                .map(entry -> new FootprintByDateDTO(entry.getKey(),entry.getValue()))
                .collect(Collectors.toList());

        return new FootprintByDateSliceDTO(footprintByDateDTOList
                ,responseSlice.getNumber()
                ,responseSlice.getSize()
                ,responseSlice.isFirst()
                ,responseSlice.isLast());
    }

    private Footprint checkFootprintAuthority(long footprintId, FootprintPassword footprintPassword) {
        Footprint footprint = footprintRepository.findById(footprintId).orElseThrow(()-> new BusinessException(FOOTPRINT_ID_NOT_EXIST));
        String loginId = null;
        try {
            loginId = accountUtil.getLoginMemberId();
        }catch (RuntimeException e){
            log.info("토큰없음");
        }
        if(!passwordEncoder.matches(footprintPassword.password(), footprint.getPassword())
                && !footprint.getGuestbook().getHost().getId().equals(loginId)) {
            throw new BusinessException(FORBIDDEN_ERROR);
        }
        return footprint;
    }

    //좌표(위도,경도)를 이용한 거리계산
    public boolean checkLocation(double latBook,double lonBook,double latFoot,double lonFoot){
        double theta = lonBook - lonFoot;
        double dist = Math.sin(Math.toRadians(latBook))
                * Math.sin(Math.toRadians(latFoot))
                + Math.cos(Math.toRadians(latBook))
                * Math.cos(Math.toRadians(latFoot))
                * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist *= 60*1.1515*1609.344;  //meter단위 거리
        return dist <= DEGREE;
    }
}
