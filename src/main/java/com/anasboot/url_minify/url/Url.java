package com.anasboot.url_minify.url;
import jakarta.persistence.*;
import java.time.Instant;

import com.anasboot.url_minify.Base62;

@Entity
@Table(name = "urls")
public class Url {
    @Id
    private Long id;
    private String code;
    private String longUrl;
    private boolean isCustom;
    private Instant createdAt;
    private Instant expiresAt;
    private long clickCount;

    protected Url() {}

    private Url(long id, String longUrl, boolean isCustom, Instant expiresAt, String code) {
        this.id = id;
        this.longUrl = longUrl;
        this.isCustom = isCustom;
        this.createdAt = Instant.now();
        this.expiresAt = expiresAt;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public boolean isCustom() {
        return isCustom;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public long getClickCount() {
        return clickCount;
    }

    public static Url custom(long id, String code, String longUrl, Instant expiresAt) {
        Url url = new Url(id, longUrl, true, expiresAt, code);
        return url;
    }

    public static Url generated(long id, String longUrl, Instant expiresAt) {
        Url url = new Url(id, longUrl, false, expiresAt, Base62.encode(id));
        return url;
    }
}
