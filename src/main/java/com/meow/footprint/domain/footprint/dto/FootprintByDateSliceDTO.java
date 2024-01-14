package com.meow.footprint.domain.footprint.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FootprintByDateSliceDTO {
    private List<FootprintByDateDTO> footprintByDateDTOList;
    private int pageNumber;
    private int pageSize;
    private boolean first;
    private boolean last;
}
