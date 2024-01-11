package com.meow.footprint.domain.footprint.repository;

import com.meow.footprint.domain.footprint.dto.PhotoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.meow.footprint.domain.footprint.entity.QPhoto.photo;

@RequiredArgsConstructor
public class PhotoRepositoryQuerydslImpl implements PhotoRepositoryQuerydsl {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<PhotoResponse> getPhotoListByDate(long guestbookId, Pageable pageable) {
        List<PhotoResponse> photoResponseList = jpaQueryFactory.select(Projections.fields(
                        PhotoResponse.class
                        , photo.id
                        , photo.writer
                        , photo.fileName
                        , photo.createTime
                        , photo.isSecret
                ))
                .from(photo)
                .where(photo.guestbook.id.eq(guestbookId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .orderBy(photo.createTime.desc())
                .fetch();
        boolean hasNext = false;
        if (photoResponseList.size() > pageable.getPageSize()) {
            photoResponseList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(photoResponseList, pageable,hasNext);
    }
}
