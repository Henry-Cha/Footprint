package com.meow.footprint.domain.footprint.repository;

import com.meow.footprint.domain.footprint.dto.PhotoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PhotoRepositoryQuerydsl {
    Slice<PhotoResponse> getPhotoListByDate(long guestbookId, Pageable pageable);
}
