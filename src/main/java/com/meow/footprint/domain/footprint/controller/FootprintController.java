package com.meow.footprint.domain.footprint.controller;

import com.meow.footprint.domain.footprint.dto.FootprintByDateSliceDTO;
import com.meow.footprint.domain.footprint.dto.FootprintPassword;
import com.meow.footprint.domain.footprint.dto.FootprintRequest;
import com.meow.footprint.domain.footprint.dto.FootprintResponse;
import com.meow.footprint.domain.footprint.service.FootprintService;
import com.meow.footprint.global.result.ResultCode;
import com.meow.footprint.global.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/footprints")
@RequiredArgsConstructor
public class FootprintController {
    private final FootprintService footprintService;

    @Operation(summary = "발자국 생성")
    @PostMapping("")
    public ResponseEntity<ResultResponse> createFootprint(FootprintRequest footprintRequest){
        footprintService.createFootprint(footprintRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResultResponse.of(ResultCode.CREATE_FOOTPRINT_SUCCESS));
    }

    @Operation(summary = "발자국 비밀글 조회")
    @PostMapping("/{footprintId}")
    public ResponseEntity<ResultResponse> getSecretFootprint(@PathVariable long footprintId, @RequestBody FootprintPassword footprintPassword){
        FootprintResponse footprintResponse = footprintService.getSecretFootprint(footprintId,footprintPassword);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_SECRET_FOOTPRINT_SUCCESS,footprintResponse));
    }

    @Operation(summary = "발자국 목록 조회 (날짜 별 묶음)")
    @GetMapping("")
    public ResponseEntity<ResultResponse> getFootprintListByDate(String guestbookId,int page,int size){
        FootprintByDateSliceDTO sliceDTO = footprintService.getFootprintListByDate(guestbookId,page,size);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.GET_FOOTPRINT_LIST_SUCCESS,sliceDTO));
    }

    @Operation(summary = "발자국 삭제")
    @DeleteMapping("/{footprintId}")
    public ResponseEntity<ResultResponse> deleteFootprint(@PathVariable long footprintId,FootprintPassword footprintPassword){
        footprintService.deleteFootprint(footprintId,footprintPassword);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.DELETE_FOOTPRINT_SUCCESS));
    }

    @Operation(summary = "발자국 읽음확인")
    @PatchMapping("/{footprintId}")
    public ResponseEntity<ResultResponse> readCheckFootprint(@PathVariable long footprintId){
        footprintService.readCheckFootprint(footprintId);
        return ResponseEntity.ok(ResultResponse.of(ResultCode.READ_FOOTPRINT_SUCCESS));
    }
}
