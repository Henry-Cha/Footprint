package com.meow.footprint.domain.footprint.dto;

public record FootprintRequest(long guestbook, String writer, String content, Boolean isSecret,
                               double latitude, double longitude) {
}
