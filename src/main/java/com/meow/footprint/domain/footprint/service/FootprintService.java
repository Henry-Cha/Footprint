package com.meow.footprint.domain.footprint.service;

import com.meow.footprint.domain.footprint.dto.FootprintByDateSliceDTO;
import com.meow.footprint.domain.footprint.dto.FootprintPassword;
import com.meow.footprint.domain.footprint.dto.FootprintRequest;
import com.meow.footprint.domain.footprint.dto.FootprintResponse;

public interface FootprintService {
    void createFootprint(FootprintRequest footprintRequest);

    FootprintResponse getSecretFootprint(long footprintId, FootprintPassword footprintPassword);

    FootprintByDateSliceDTO getFootprintListByDate(String guestbookId, int page, int size);

    void deleteFootprint(long footprintId, FootprintPassword footprintPassword);
}
