package com.meow.footprint.domain.member.service;


import com.meow.footprint.domain.member.dto.*;

public interface MemberService {
    void register(MemberJoinRequest joinRequest);

    boolean idCheck(String memberId);

    MemberResponse findMemberById(String memberId);

    void updateMember(MemberUpdateRequest memberUpdateRequest, String memberId);

    void deleteMember(String memberId);

    LoginResponse login(LoginRequest loginRequest);

    void logout();

    void updatePassword(PasswordUpdateRequest passwordUpdateRequest);
}