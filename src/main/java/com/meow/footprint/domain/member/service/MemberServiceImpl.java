package com.meow.footprint.domain.member.service;

import com.meow.footprint.domain.member.dto.*;
import com.meow.footprint.domain.member.entity.Member;
import com.meow.footprint.domain.member.repository.MemberRepository;
import com.meow.footprint.global.result.error.ErrorCode;
import com.meow.footprint.global.result.error.exception.EntityAlreadyExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public void register(MemberJoinRequest joinRequest) {
        Member member = modelMapper.map(joinRequest,Member.class);
        if(memberRepository.existsById(member.getId())) throw new EntityAlreadyExistException(ErrorCode.MEMBER_ID_ALREADY_EXIST);

        member.encodingPassword(passwordEncoder);
        memberRepository.save(member);
    }

    @Override
    public boolean idCheck(String memberId) {
        return false;
    }

    @Override
    public MemberResponse findMemberById(String memberId) {
        return null;
    }

    @Override
    public void updateMember(MemberUpdateRequest memberUpdateRequest) {

    }

    @Override
    public void deleteMember(String memberId) {

    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public void logout() {

    }

    @Override
    public void updatePassword(PasswordUpdateRequest passwordUpdateRequest) {

    }
}
