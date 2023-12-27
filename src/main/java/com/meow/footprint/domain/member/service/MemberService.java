package com.meow.footprint.domain.member.service;


import com.meow.footprint.domain.member.dto.*;

public interface MemberService {
    void register(MemberJoinRequest joinRequest);

    boolean idCheck(String memberId);

    MemberResponse findMemberById(String memberId);

    void updateMember(MemberUpdateRequest memberUpdateRequest, String memberId);

    void deleteMember(String memberId);

    LoginTokenDTO login(LoginRequest loginRequest);

    void logout(String accessToken);

    LoginTokenDTO reissue(LoginTokenDTO loginTokenDTO);

    void updatePassword(PasswordUpdateRequest passwordUpdateRequest, String memberId);
}