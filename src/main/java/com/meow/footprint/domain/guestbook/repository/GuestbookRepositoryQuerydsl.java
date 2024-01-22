package com.meow.footprint.domain.guestbook.repository;

import com.meow.footprint.domain.footprint.dto.FootprintResponse;
import java.util.List;

public interface GuestbookRepositoryQuerydsl {
    List<FootprintResponse> getFootprintListRecent(String memberId);
}
