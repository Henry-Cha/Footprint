package com.meow.footprint.domain.footprint.entity;

import com.meow.footprint.domain.guestbook.entity.Guestbook;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(value = { AuditingEntityListener.class })
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Guestbook guestbook;
    private String writer;
    private String memberId;
    private boolean isSecret;
    private String fileName;
    @CreatedDate
    private LocalDateTime createTime;

}
