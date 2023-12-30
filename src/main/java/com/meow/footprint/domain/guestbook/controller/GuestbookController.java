package com.meow.footprint.domain.guestbook.controller;

import com.meow.footprint.domain.guestbook.dto.GuestBookRequest;
import com.meow.footprint.domain.guestbook.dto.GuestbookDTO;
import com.meow.footprint.domain.guestbook.dto.GuestbookSimpleResponse;
import com.meow.footprint.domain.guestbook.service.GuestbookService;
import com.meow.footprint.global.result.ResultCode;
import com.meow.footprint.global.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.meow.footprint.global.result.ResultCode.*;

@RestController
@RequestMapping("/guestbooks")
@RequiredArgsConstructor
public class GuestbookController {
    private final GuestbookService guestbookService;

    // TODO: 2023-12-30 스웨거에서 테스트 하면 dto가 json으로 안날라가서 테스트 안됨. 포스트맨에서는 됨
    @Operation(summary = "방명록 생성")
    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> createGuestbook(@RequestPart GuestBookRequest guestBookRequest
            , @RequestPart(value = "photo", required = false) MultipartFile photo){
        guestbookService.createGuestbook(guestBookRequest,photo);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResultResponse.of(ResultCode.CREATE_GUESTBOOK_SUCCESS));
    }
    @Operation(summary = "방명록 목록 조회")
    @GetMapping("")
    public ResponseEntity<ResultResponse> getGuestbookList(String memberId) {
        List<GuestbookDTO> list = guestbookService.getGuestbookList(memberId);
        return ResponseEntity.ok(ResultResponse.of(GET_GUESTBOOK_LIST_SUCCESS,list));
    }
    @Operation(summary = "방명록 삭제")
    @DeleteMapping("{guestbookId}")
    public ResponseEntity<ResultResponse> deleteGuestbook(@PathVariable long guestbookId) {
        guestbookService.deleteGuestbook(guestbookId);
        return ResponseEntity.ok(ResultResponse.of(DELETE_GUESTBOOK_SUCCESS));
    }
    @Operation(summary = "방명록 수정")
    @PatchMapping(value = "{guestbookId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> updateGuestbook(@PathVariable long guestbookId,@RequestPart GuestBookRequest guestBookRequest
            , @RequestParam(required = false) MultipartFile photo){
        guestbookService.updateGuestbook(guestbookId,guestBookRequest,photo);
        return ResponseEntity.ok(ResultResponse.of(UPDATE_GUESTBOOK_SUCCESS));
    }
    @Operation(summary = "방명록 개별 조회")
    @GetMapping("{guestbookId}/simple")
    public ResponseEntity<ResultResponse> getGuestbookSimple(@PathVariable long guestbookId){
        GuestbookSimpleResponse simpleResponse = guestbookService.getGuestbookSimple(guestbookId);
        return ResponseEntity.ok(ResultResponse.of(GET_GUESTBOOK_SIMPLE_SUCCESS,simpleResponse));
    }
}
