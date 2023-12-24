package com.meow.footprint.global.result.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Global
    INTERNAL_SERVER_ERROR(500,  "내부 서버 오류입니다."),
    METHOD_NOT_ALLOWED(405,  "허용되지 않은 HTTP method입니다."),
    INPUT_VALUE_INVALID(400,  "유효하지 않은 입력입니다."),
    INPUT_TYPE_INVALID(400, "입력 타입이 유효하지 않습니다."),
    HTTP_MESSAGE_NOT_READABLE(400, "request message body가 없거나, 값 타입이 올바르지 않습니다."),
    HTTP_HEADER_INVALID(400, "request header가 유효하지 않습니다."),
    ENTITY_NOT_FOUNT(500, "존재하지 않는 Entity입니다."),

    //JWT
    JWT_INVALID(401, "유효하지 않은 토큰입니다."),
    JWT_BADTYPE(401, "Bearer 타입 토큰이 아닙니다."),
    JWT_EXPIRED(403, "만료된 토큰입니다."),
    JWT_MALFORM(401, "토큰값이 올바르지 않습니다."),

    // Member
    WRONG_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    MEMBER_ID_ALREADY_EXIST(400, "회원 id가 이미 존재합니다."),
    MEMBER_ID_NOT_EXIST(400, "회원 id가 존재하지 않습니다.");

    private final int status;
    private final String message;

}