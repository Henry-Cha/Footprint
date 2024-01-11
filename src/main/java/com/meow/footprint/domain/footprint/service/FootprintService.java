package com.meow.footprint.domain.footprint.service;

import com.meow.footprint.domain.footprint.dto.FootprintByDateSliceDTO;
import com.meow.footprint.domain.footprint.dto.FootprintRequest;
import com.meow.footprint.domain.footprint.dto.FootprintResponse;
import com.meow.footprint.domain.footprint.dto.PhotoRequest;
import org.springframework.web.multipart.MultipartFile;

public interface FootprintService {
    void createFootprint(FootprintRequest footprintRequest);

    FootprintResponse getSecretFootprint(long footprintId);

    FootprintByDateSliceDTO getFootprintListByDate(long guestbookId, int page, int size);

    void deleteFootprint(long footprintId);

    void readCheckFootprint(long footprintId);

    void createPhoto(PhotoRequest photoRequest, MultipartFile photo);

    void deletePhoto(long photoId);

    FootprintByDateSliceDTO getPhotoListByDate(long guestbookId, int page, int size);
}
