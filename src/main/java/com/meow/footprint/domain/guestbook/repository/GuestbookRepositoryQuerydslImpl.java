package com.meow.footprint.domain.guestbook.repository;

import static com.meow.footprint.domain.footprint.entity.QFootprint.footprint;

import com.meow.footprint.domain.footprint.dto.FootprintResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GuestbookRepositoryQuerydslImpl implements GuestbookRepositoryQuerydsl {
    private final JPAQueryFactory jpaQueryFactory;
    private final static int RECENT_COUNT = 20;

    @Override
    public List<FootprintResponse> getFootprintListRecent(String memberId) {
        return jpaQueryFactory.select(Projections.fields(
                FootprintResponse.class
                , footprint.id
                , footprint.writer
                , footprint.content
                , footprint.createTime
                , footprint.isChecked
                , footprint.isSecret
            ))
            .from(footprint)
            .where(footprint.guestbook.host.id.eq(memberId))
            .limit(RECENT_COUNT)
            .orderBy(footprint.createTime.desc())
            .fetch();
    }
}
