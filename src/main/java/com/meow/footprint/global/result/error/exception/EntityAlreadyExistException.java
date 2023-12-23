package com.meow.footprint.global.result.error.exception;


import com.meow.footprint.global.result.error.ErrorCode;

public class EntityAlreadyExistException extends BusinessException {

    public EntityAlreadyExistException(ErrorCode errorCode) {
        super(errorCode);
    }
}