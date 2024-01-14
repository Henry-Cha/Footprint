package com.meow.footprint.domain.footprint.dto;

import com.meow.footprint.domain.footprint.entity.Photo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhotoResponse {
    private long id;
    private String writer;
    private String fileName;
    private LocalDateTime createTime;
    private boolean isSecret;

    public static PhotoResponse from(Photo photo){
        return new PhotoResponse(photo.getId(),
                photo.getWriter(),
                photo.getFileName(),
                photo.getCreateTime(),
                photo.isSecret());
    }
    public LocalDate getCreateDate(){
        return this.createTime.toLocalDate();
    }
}
