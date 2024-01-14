package com.meow.footprint.domain.footprint.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FootprintByDateDTO<T> {
    private LocalDate date;
    private List<T> footprintResponses;
}
