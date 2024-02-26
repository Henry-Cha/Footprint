package com.meow.footprint.domain.guestbook.controller;

import static com.meow.footprint.global.result.ResultCode.DELETE_GUESTBOOK_SUCCESS;
import static com.meow.footprint.global.result.ResultCode.GET_GUESTBOOK_LIST_SUCCESS;
import static com.meow.footprint.global.result.ResultCode.GET_GUESTBOOK_QR_SUCCESS;
import static com.meow.footprint.global.result.ResultCode.GET_GUESTBOOK_SIMPLE_SUCCESS;
import static com.meow.footprint.global.result.ResultCode.GET_RECENT_FOOTPRINT_LIST_SUCCESS;
import static com.meow.footprint.global.result.ResultCode.UPDATE_GUESTBOOK_SUCCESS;

import com.meow.footprint.domain.footprint.dto.FootprintResponse;
import com.meow.footprint.domain.guestbook.dto.GuestBookRequest;
import com.meow.footprint.domain.guestbook.dto.GuestbookDTO;
import com.meow.footprint.domain.guestbook.dto.GuestbookQrResponse;
import com.meow.footprint.domain.guestbook.dto.GuestbookSimpleResponse;
import com.meow.footprint.domain.guestbook.service.GuestbookService;
import com.meow.footprint.global.result.ResultCode;
import com.meow.footprint.global.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/guestbooks")
@RequiredArgsConstructor
public class GuestbookController {
    private final GuestbookService guestbookService;

    @Operation(summary = "방명록 생성")
    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> createGuestbook(@RequestPart @Valid GuestBookRequest guestBookRequest
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
    public ResponseEntity<ResultResponse> updateGuestbook(@PathVariable long guestbookId,@RequestPart @Valid GuestBookRequest guestBookRequest
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
    @Operation(summary = "방명록 QR 조회")
    @GetMapping("/{guestbookId}/qr")
    public ResponseEntity<ResultResponse> getGuestbookQr(@PathVariable long guestbookId, String qrLink){
        GuestbookQrResponse qrResponse = guestbookService.getGuestbookQr(guestbookId,qrLink);
        return ResponseEntity.ok(ResultResponse.of(GET_GUESTBOOK_QR_SUCCESS,qrResponse));
    }
    @Operation(summary = "최근 발자국 목록(20개)")
    @GetMapping("/recent")
    public ResponseEntity<ResultResponse> getFootprintListRecent(){
        List<FootprintResponse> responseList = guestbookService.getFootprintListRecent();
        return ResponseEntity.ok(ResultResponse.of(GET_RECENT_FOOTPRINT_LIST_SUCCESS,responseList));
    }
}
