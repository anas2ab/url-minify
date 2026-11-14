package com.anasboot.url_minify.url;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ShortenRequest (

    @NotBlank
    @Pattern(regexp = "^https?://\\S+$")
    @Size(max = 2048)
    String longUrl,

    @Pattern(regexp = "^[A-Za-z0-9_-]{3,32}$")
    String customAlias,

    @Future
    Instant expiresAt
) {}
