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
    @ManyToOne(fetch = FetchType.LAZY)
    private Member host;
    private String photo;
    @ColumnDefault("0")
    private int footprintCount;
    @ColumnDefault("0")
    private int updateCount;
    private double latitude;
    private double longitude;
    private String addressSigungu;
    private String addressDong;
    @CreatedDate
    private LocalDateTime createTime;
    private String qrCode;

    public Guestbook(GuestBookRequest guestBookRequest){
        this.name = guestBookRequest.getName();
        this.description = guestBookRequest.getDescription();
        this.latitude = guestBookRequest.getLatitude();
        this.longitude = guestBookRequest.getLongitude();
        this.addressSigungu = guestBookRequest.getAddressSigungu();
        this.addressDong = guestBookRequest.getAddressDong();
    }
    public void update(GuestBookRequest guestBookRequest){
        this.name = guestBookRequest.getName()==null?this.name:guestBookRequest.getName();
        this.description = guestBookRequest.getDescription()==null?this.description:guestBookRequest.getDescription();
        this.latitude = guestBookRequest.getLatitude()==null?this.latitude:guestBookRequest.getLatitude();
        this.longitude = guestBookRequest.getLongitude()==null?this.latitude:guestBookRequest.getLongitude();
        this.addressSigungu = guestBookRequest.getAddressSigungu()==null?this.addressSigungu:guestBookRequest.getAddressSigungu();
        this.addressDong = guestBookRequest.getAddressDong()==null?this.addressDong:guestBookRequest.getAddressDong();
    }
    public void countUp(){
        this.footprintCount+=1;
    }
    public void updateCountUp(){
        this.updateCount+=1;
    }
    public void updateCountDown(){
        this.updateCount-=1;
    }

}
