package com.meow.footprint.domain.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Entity
@ToString
@EntityListeners(value = { AuditingEntityListener.class })
public class Member {
    @Id
    private String id;
    private String name;
    private String password;
    @CreatedDate
    private LocalDateTime joinDate;

    public void encodingPassword(PasswordEncoder encoder){
        this.password = encoder.encode(password);
    }
}
