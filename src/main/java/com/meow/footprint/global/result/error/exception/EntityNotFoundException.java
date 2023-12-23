package com.meow.footprint.global.result.error.exception;

import com.meow.footprint.global.result.error.ErrorCode;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}