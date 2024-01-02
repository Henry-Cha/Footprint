package com.meow.footprint.domain.footprint.dto;

import com.meow.footprint.domain.footprint.entity.Footprint;

import java.time.LocalDateTime;

public record FootprintResponse(long id, String writer, String content, LocalDateTime createTime,boolean isChecked,boolean isSecret) {
    public static FootprintResponse from(Footprint footprint){
        return new FootprintResponse(footprint.getId(),
                footprint.getWriter(),
                footprint.getContent(),
                footprint.getCreateTime(),
                footprint.isChecked(),
                footprint.isSecret());
    }
}
