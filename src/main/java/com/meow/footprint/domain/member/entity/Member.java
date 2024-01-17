package com.meow.footprint.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter @Setter
@Entity
@ToString
@Builder
@EntityListeners(value = { AuditingEntityListener.class })
public class Member {
    @Id
    private String id;
    private String name;
    private String password;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    private Set<Role> role = new HashSet<>();
    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    private String oauthEmail;
    @CreatedDate
    private LocalDateTime joinDate;

    public void encodingPassword(PasswordEncoder encoder){
        this.password = encoder.encode(password);
    }
    public void addRole(Role role){
        this.role.add(role);
    }
}
