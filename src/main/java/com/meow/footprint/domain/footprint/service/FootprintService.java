package com.meow.footprint.domain.footprint.service;

import com.meow.footprint.domain.footprint.dto.FootprintByDateSliceDTO;
import com.meow.footprint.domain.footprint.dto.FootprintPassword;
import com.meow.footprint.domain.footprint.dto.FootprintRequest;
import com.meow.footprint.domain.footprint.dto.FootprintResponse;
import com.meow.footprint.domain.footprint.dto.PhotoRequest;
import org.springframework.web.multipart.MultipartFile;

public interface FootprintService {
    void createFootprint(FootprintRequest footprintRequest);

    FootprintResponse getSecretFootprint(long footprintId, FootprintPassword footprintPassword);

    FootprintByDateSliceDTO getFootprintListByDate(String guestbookId, int page, int size);

    void deleteFootprint(long footprintId, FootprintPassword footprintPassword);

    void readCheckFootprint(long footprintId);

    void createPhoto(PhotoRequest photoRequest, MultipartFile photo);

    void deletePhoto(long photoId, FootprintPassword footprintPassword);
}
