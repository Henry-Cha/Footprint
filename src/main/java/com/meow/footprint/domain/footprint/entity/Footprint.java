package com.meow.footprint.domain.footprint.entity;

import com.meow.footprint.domain.guestbook.entity.Guestbook;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(value = { AuditingEntityListener.class })
public class Footprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Guestbook guestbook;
    private String writer;
    private String content;
    @ColumnDefault("false")
    private boolean isSecret;
    private String password;
    @ColumnDefault("false")
    private boolean isChecked;
    @CreatedDate
    private LocalDateTime createTime;

    public void encodingPassword(PasswordEncoder encoder){
        this.password = encoder.encode(password);
    }
}
