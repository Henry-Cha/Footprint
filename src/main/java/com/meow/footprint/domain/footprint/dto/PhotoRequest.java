package com.meow.footprint.domain.footprint.dto;

public record PhotoRequest(long guestbook, String writer, boolean isSecret, double latitude,
                           double longitude) {
}