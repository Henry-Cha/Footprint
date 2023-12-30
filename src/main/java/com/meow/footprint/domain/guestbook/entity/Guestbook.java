package com.meow.footprint.domain.guestbook.entity;

import com.meow.footprint.domain.guestbook.dto.GuestBookRequest;
import com.meow.footprint.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@EntityListeners(value = { AuditingEntityListener.class })
public class Guestbook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    @ManyToOne
    private Member host;
    private String photo;
    @ColumnDefault("0")
    private int footprintCount;
    @ColumnDefault("false")
    private boolean isUpdate;
    private double latitude;
    private double longitude;
    private String address;
    @CreatedDate
    private LocalDateTime createTime;

    public Guestbook(GuestBookRequest guestBookRequest){
        this.name = guestBookRequest.getName();
        this.description = guestBookRequest.getDescription();
        this.latitude = guestBookRequest.getLatitude();
        this.longitude = guestBookRequest.getLongitude();
        this.address = guestBookRequest.getAddress();
    }
}
