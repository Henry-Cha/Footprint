package com.meow.footprint.domain.footprint.repository;

import static com.meow.footprint.domain.footprint.entity.QFootprint.footprint;

import com.meow.footprint.domain.footprint.dto.FootprintResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class FootprintRepositoryQuerydslImpl implements FootprintRepositoryQuerydsl {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<FootprintResponse> getFootprintListByDate(long guestbookId, Pageable pageable) {
        List<FootprintResponse> footprintResponseList = jpaQueryFactory.select(Projections.fields(
                FootprintResponse.class
                , footprint.id
                , footprint.writer
                , footprint.content
                , footprint.createTime
                , footprint.isChecked
                , footprint.isSecret
            ))
            .from(footprint)
            .where(footprint.guestbook.id.eq(guestbookId))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .orderBy(footprint.createTime.desc())
            .fetch();
        boolean hasNext = false;
        if (footprintResponseList.size() > pageable.getPageSize()) {
            footprintResponseList.remove(pageable.getPageSize());
            hasNext = true;
        }
        return new SliceImpl<>(footprintResponseList, pageable, hasNext);
    }
}
