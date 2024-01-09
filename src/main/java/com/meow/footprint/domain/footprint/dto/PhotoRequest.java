package com.meow.footprint.domain.footprint.dto;

public record PhotoRequest(long guestbook, String writer, boolean secret, double latitude,
                           double longitude) {
}