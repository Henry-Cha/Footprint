package com.meow.footprint.domain.member.dto;

public record PasswordUpdateRequest(String oldPassword,String newPassword) {
}
