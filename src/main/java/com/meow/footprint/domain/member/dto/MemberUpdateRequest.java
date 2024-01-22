package com.meow.footprint.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberUpdateRequest(@NotBlank String name) {
}
