package com.anasboot.url_minify.url;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import com.anasboot.url_minify.Base62;
import com.anasboot.url_minify.TestcontainersConfiguration;

@SpringBootTest 
@Import(TestcontainersConfiguration.class)
@Transactional
public class UrlServiceTest {

    @Autowired 
    UrlService urlService;

    @Autowired 
    UrlRepository urlRepository;

    @Test
    void shortensWithGeneratedCode() {
        Url url = urlService.shorten("https://www.example.com", null, null);
        assertEquals(Base62.encode(url.getId()), url.getCode());
        assertFalse(url.isCustom());
        assertTrue(urlRepository.findByCode(url.getCode()).isPresent());
    }

    @Test
    void shortensWithCustomAlias() {
        Url url = urlService.shorten("https://www.example.com", "my-alias", null);
        assertEquals("my-alias", url.getCode());
        assertTrue(url.isCustom());
    }

    @Test 
    void rejectsTakenAlias() {
        urlService.shorten("https://www.example.com", "taken", null);
        assertThrows(AliasTakenException.class, () -> urlService.shorten("https://www.example.org", "taken", null));
    }

    @Test 
    void resolvesExistingCode() {
        Url url = urlService.shorten("https://www.example.com", null, null);
        String longUrl = urlService.resolve(url.getCode());
        assertEquals("https://www.example.com", longUrl);
    }

    @Test 
    void throwsNotFoundForUnknownCode() {
        assertThrows(UrlNotFoundException.class, () -> urlService.resolve("unknown"));
    }

    @Test 
    void throwsExpiredForPastExpiry() {
        Url url = urlService.shorten("https://www.example.com", null, Instant.now().minusSeconds(60));
        assertThrows(UrlExpiredException.class, () -> urlService.resolve(url.getCode()));
    }

    @Test 
    void resolvesWhenExpiryInFuture() {
        Url url = urlService.shorten("https://www.example.com", null, Instant.now().plusSeconds(3600));
        String longUrl = urlService.resolve(url.getCode());
        assertEquals("https://www.example.com", longUrl);
    }

    @Test 
    void rejectsReservedAlias() {
        assertThrows(AliasTakenException.class, () -> urlService.shorten("https://www.example.com", "actuator", null));
        assertThrows(AliasTakenException.class, () -> urlService.shorten("https://www.example.com", "api", null));
        assertThrows(AliasTakenException.class, () -> urlService.shorten("https://www.example.com", "health", null));
        assertThrows(AliasTakenException.class, () -> urlService.shorten("https://www.example.com", "Actuator", null));
    }
}