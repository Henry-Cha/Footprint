package com.meow.footprint.domain.member.controller;

import com.meow.footprint.domain.member.dto.*;
import com.meow.footprint.domain.member.service.MemberService;
import com.meow.footprint.global.result.ResultCode;
import com.meow.footprint.global.result.ResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.meow.footprint.global.result.ResultCode.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;
	
	@Operation(summary = "회원가입",description = "회원 정보를 입력받아 가입합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "성공"),
			@ApiResponse(responseCode = "500", description = "실패."),
	})
	@PostMapping("")
	public ResponseEntity<ResultResponse> register(@RequestBody MemberJoinRequest joinRequest) {
		memberService.register(joinRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(ResultResponse.of(ResultCode.REGISTER_SUCCESS));
	}

	@Operation(summary = "id중복 체크")
	@GetMapping("/idcheck/{memberId}")
	public ResponseEntity<ResultResponse> idCheck(@PathVariable String memberId) {
		if(memberService.idCheck(memberId)) //true : 존재  // TODO: 2023-12-24 id 중복 시 상태코드 200 or 400??
			return ResponseEntity.ok(ResultResponse.of(MEMBER_ID_EXIST,false));
		return ResponseEntity.ok(ResultResponse.of(MEMBER_ID_NOT_EXIST,true));
	}

	@Operation(summary = "회원 조회.")
	@GetMapping("/{memberId}")
	public ResponseEntity<ResultResponse> findMemberById(@PathVariable String memberId) {
		MemberResponse member = memberService.findMemberById(memberId);
		return ResponseEntity.ok(ResultResponse.of(MEMBER_FIND_SUCCESS,member));
	}

	@Operation(summary = "회원정보 수정.")
	@PutMapping("/{memberId}")
	public ResponseEntity<ResultResponse> updateMember(@RequestBody MemberUpdateRequest memberUpdateRequest,@PathVariable String memberId) {
		memberService.updateMember(memberUpdateRequest,memberId);
		return ResponseEntity.ok(ResultResponse.of(MEMBER_UPDATE_SUCCESS));
	}

	@Operation(summary = "회원 탈퇴.")
	@DeleteMapping("/{memberId}")
	public ResponseEntity<ResultResponse> deleteMember(@PathVariable String memberId) {
		memberService.deleteMember(memberId);
		return ResponseEntity.ok(ResultResponse.of(MEMBER_DELETE_SUCCESS));
	}

	@Operation(summary = "로그인.")
	@PostMapping("/login")
	public ResponseEntity<ResultResponse> login(@RequestBody LoginRequest loginRequest) {
		LoginResponse token = memberService.login(loginRequest);
		return ResponseEntity.ok(ResultResponse.of(LOGIN_SUCCESS,token));
	}

	@Operation(summary = "로그아웃.")
	@PostMapping("/logout")
	public ResponseEntity<ResultResponse> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
		memberService.logout(accessToken);
		return ResponseEntity.ok(ResultResponse.of(LOGOUT_SUCCESS));
	}

	@Operation(summary = "비밀번호 수정.")
	@PatchMapping("/{memberId}/password")
	public ResponseEntity<ResultResponse> updatePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest, @PathVariable String memberId) {
		memberService.updatePassword(passwordUpdateRequest,memberId);
		return ResponseEntity.ok(ResultResponse.of(PASSWORD_UPDATE_SUCCESS));
	}
}
