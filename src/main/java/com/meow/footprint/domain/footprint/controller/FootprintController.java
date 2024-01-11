package com.meow.footprint.domain.footprint.controller;

import com.meow.footprint.domain.footprint.dto.FootprintByDateSliceDTO;
import com.meow.footprint.domain.footprint.dto.FootprintRequest;
import com.meow.footprint.domain.footprint.dto.FootprintResponse;
import com.meow.footprint.domain.footprint.dto.PhotoRequest;
import com.meow.footprint.domain.footprint.service.FootprintService;
import com.meow.footprint.global.result.ResultCode;
import com.meow.footprint.global.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/footprints")
@RequiredArgsConstructor
public class FootprintController {
    private final FootprintService footprintService;

    @Operation(summary = "발자국 생성")
    @PostMapping("")
    public ResponseEntity<ResultResponse> createFootprint(@RequestBody FootprintRequest footprintRequest){
        footprintService.createFootprint(footprintRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResultResponse.of(ResultCode.CREATE_FOOTPRINT_SUCCESS));
    }

    @Operation(summary = "발자국 비밀글 조회")
    @PostMapping("/{footprintId}")
    public ResponseEntity<ResultResponse> getSecretFootprint(@PathVariable long footprintId){
        FootprintResponse footprintResponse = footprintService.getSecretFootprint(footprintId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_SECRET_FOOTPRINT_SUCCESS,footprintResponse));
    }

    @Operation(summary = "발자국 목록 조회 (날짜 별 묶음)")
    @GetMapping("")
    public ResponseEntity<ResultResponse> getFootprintListByDate(long guestbookId,int page,int size){
        FootprintByDateSliceDTO sliceDTO = footprintService.getFootprintListByDate(guestbookId,page,size);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_FOOTPRINT_LIST_SUCCESS,sliceDTO));
    }

    @Operation(summary = "발자국 삭제")
    @DeleteMapping("/{footprintId}")
    public ResponseEntity<ResultResponse> deleteFootprint(@PathVariable long footprintId){
        footprintService.deleteFootprint(footprintId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_FOOTPRINT_SUCCESS));
    }

    @Operation(summary = "발자국 읽음확인")
    @PatchMapping("/{footprintId}")
    public ResponseEntity<ResultResponse> readCheckFootprint(@PathVariable long footprintId){
        footprintService.readCheckFootprint(footprintId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.READ_FOOTPRINT_SUCCESS));
    }

    @Operation(summary = "발자국 사진 등록")
    @PostMapping(value = "/photos",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> createPhoto(@RequestPart PhotoRequest photoRequest
            , @RequestPart(value = "photo", required = false) MultipartFile photo){
        footprintService.createPhoto(photoRequest,photo);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResultResponse.of(ResultCode.CREATE_PHOTO_SUCCESS));
    }

    @Operation(summary = "발자국 사진 삭제")
    @PostMapping(value = "/photos/{photoId}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultResponse> deletePhoto(@PathVariable long photoId){
        footprintService.deletePhoto(photoId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_PHOTO_SUCCESS));
    }

    @Operation(summary = "발자국 사진 목록 조회 (날짜 별 묶음)")
    @GetMapping("/photos")
    public ResponseEntity<ResultResponse> getPhotoListByDate(long guestbookId,int page,int size){
        FootprintByDateSliceDTO sliceDTO = footprintService.getPhotoListByDate(guestbookId,page,size);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_PHOTO_LIST_SUCCESS,sliceDTO));
    }
}
