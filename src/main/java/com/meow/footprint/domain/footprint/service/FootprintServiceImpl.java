package com.meow.footprint.domain.footprint.service;

import com.meow.footprint.domain.footprint.dto.FootprintPassword;
import com.meow.footprint.domain.footprint.dto.FootprintRequest;
import com.meow.footprint.domain.footprint.dto.FootprintResponse;
import com.meow.footprint.domain.footprint.entity.Footprint;
import com.meow.footprint.domain.footprint.repository.FootprintRepository;
import com.meow.footprint.domain.guestbook.entity.Guestbook;
import com.meow.footprint.domain.guestbook.repository.GuestbookRepository;
import com.meow.footprint.global.result.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.meow.footprint.global.result.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FootprintServiceImpl implements FootprintService{
    private final FootprintRepository footprintRepository;
    private final GuestbookRepository guestbookRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private static final int DEGREE = 100; //발자국 작성 가능 범위

    @Transactional
    @Override
    public void createFootprint(FootprintRequest footprintRequest) {
        Footprint footprint = modelMapper.map(footprintRequest,Footprint.class);
        Guestbook guestbook = guestbookRepository
                .findById(footprintRequest.guestbook())
                .orElseThrow(()-> new BusinessException(GUESTBOOK_ID_NOT_EXIST));
        if(!checkLocation(guestbook,footprintRequest)){
            throw new BusinessException(OUT_OF_AREA);
        }
        if(footprintRequest.isSecret())
            footprint.encodingPassword(passwordEncoder);
        guestbook.setUpdate(true);
        footprint.setGuestbook(guestbook);
        footprintRepository.save(footprint);
    }

    @Transactional
    @Override
    public FootprintResponse getSecretFootprint(long footprintId, FootprintPassword footprintPassword) {
        Footprint footprint = footprintRepository.findById(footprintId).orElseThrow(()-> new BusinessException(FOOTPRINT_ID_NOT_EXIST));
        if(!passwordEncoder.matches(footprintPassword.password(), footprint.getPassword()))
            throw new BusinessException(FORBIDDEN_ERROR);
        footprint.setChecked(true);
        return FootprintResponse.from(footprint);
    }

    //좌표(위도,경도)를 이용한 거리계산
    public boolean checkLocation(Guestbook guestbook,FootprintRequest footprintRequest){
        double latBook = guestbook.getLatitude();
        double lonBook = guestbook.getLongitude();

        double latFoot = footprintRequest.latitude();
        double lonFoot = footprintRequest.longitude();

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
