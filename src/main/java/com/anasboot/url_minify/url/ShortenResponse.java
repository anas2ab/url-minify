package com.anasboot.url_minify.url;

import java.time.Instant;

public record ShortenResponse (
    String code,
    String shortUrl,
    String longUrl,
    Instant expiresAt
) {
    public static ShortenResponse from(Url url, String shortUrl) {
        return new ShortenResponse(url.getCode(), shortUrl, url.getLongUrl(), url.getExpiresAt());
    }
}