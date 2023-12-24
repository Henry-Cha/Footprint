package com.meow.footprint.domain.member.service;

import com.meow.footprint.domain.member.dto.*;
import com.meow.footprint.domain.member.entity.Member;
import com.meow.footprint.domain.member.repository.MemberRepository;
import com.meow.footprint.global.result.error.exception.BusinessException;
import com.meow.footprint.global.result.error.exception.EntityAlreadyExistException;
import com.meow.footprint.global.result.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.meow.footprint.global.result.error.ErrorCode.*;

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
        if(memberRepository.existsById(member.getId())) throw new EntityAlreadyExistException(MEMBER_ID_ALREADY_EXIST);

        member.encodingPassword(passwordEncoder);
        memberRepository.save(member);
    }

    @Override
    public boolean idCheck(String memberId) {
        return memberRepository.existsById(memberId);
    }

    @Override
    public MemberResponse findMemberById(String memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(MEMBER_ID_NOT_EXIST));
        return MemberResponse.from(member);
    }

    @Transactional
    @Override
    public void updateMember(MemberUpdateRequest memberUpdateRequest, String memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(MEMBER_ID_NOT_EXIST));
        member.setName(memberUpdateRequest.name());
    }

    @Override
    public void deleteMember(String memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(MEMBER_ID_NOT_EXIST));
        memberRepository.delete(member);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public void logout() {

    }

    @Transactional
    @Override
    public void updatePassword(PasswordUpdateRequest passwordUpdateRequest, String memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(MEMBER_ID_NOT_EXIST));
        if(!passwordEncoder.matches(passwordUpdateRequest.oldPassword(), member.getPassword())){
            throw new BusinessException(WRONG_PASSWORD);
        }
        member.setPassword(passwordUpdateRequest.newPassword());
        member.encodingPassword(passwordEncoder);
    }
}
