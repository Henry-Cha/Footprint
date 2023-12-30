package com.meow.footprint.domain.guestbook.controller;

import com.meow.footprint.domain.guestbook.dto.GuestBookRequest;
import com.meow.footprint.domain.guestbook.service.GuestbookService;
import com.meow.footprint.global.result.ResultCode;
import com.meow.footprint.global.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/guestbooks")
@RequiredArgsConstructor
public class GuestbookController {
    private final GuestbookService guestbookService;

    @Operation(summary = "방명록 생성")
    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> createGuestbook(@ModelAttribute GuestBookRequest guestBookRequest) {
        guestbookService.createGuestbook(guestBookRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResultResponse.of(ResultCode.CREATE_GUESTBOOK_SUCCESS));
    }
}
