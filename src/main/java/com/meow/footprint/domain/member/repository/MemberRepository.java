package com.meow.footprint.domain.member.repository;

import com.meow.footprint.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
}
