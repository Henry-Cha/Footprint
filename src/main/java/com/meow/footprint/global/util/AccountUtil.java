package com.meow.footprint.global.util;

import com.meow.footprint.domain.member.entity.Member;
import com.meow.footprint.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class AccountUtil {
    private final MemberRepository memberRepository;

    public String getLoginMemberId() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public Member getLoginMember() {
        try {
            final String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
            return memberRepository.findById(memberId).orElseThrow(RuntimeException::new);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public boolean checkLoginMember(String memberId) {
        try {
            String loginMemberId = SecurityContextHolder.getContext().getAuthentication().getName();
            return loginMemberId.equals(memberId);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
