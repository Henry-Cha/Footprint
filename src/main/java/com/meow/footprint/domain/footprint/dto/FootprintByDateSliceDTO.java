package com.meow.footprint.domain.footprint.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FootprintByDateSliceDTO {
    List<FootprintByDateDTO> footprintByDateDTOList;
    int pageNumber;
    int pageSize;
    boolean first;
    boolean last;
}
