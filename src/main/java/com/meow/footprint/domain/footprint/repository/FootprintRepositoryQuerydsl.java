package com.meow.footprint.domain.footprint.repository;

import com.meow.footprint.domain.footprint.dto.FootprintResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FootprintRepositoryQuerydsl {
    Slice<FootprintResponse> getFootprintListByDate(long guestbookId, Pageable pageable);
}
