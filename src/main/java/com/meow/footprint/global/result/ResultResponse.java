package com.meow.footprint.global.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "결과 응답 데이터 모델")
@Getter
public class ResultResponse {

    @Schema(description = "Http 상태 코드")
    private final int status;
    @Schema(description = "응답 메세지")
    private final String message;
    @Schema(description = "응답 데이터")
    private final Object data;

    private ResultResponse(ResultCode resultCode, Object data) {
        this.status = resultCode.getStatus();
        this.message = resultCode.getMessage();
        this.data = data;
    }

    public static ResultResponse of(ResultCode resultCode, Object data) {
        return new ResultResponse(resultCode, data);
    }

    public static ResultResponse of(ResultCode resultCode) {
        return new ResultResponse(resultCode, "");
    }

}