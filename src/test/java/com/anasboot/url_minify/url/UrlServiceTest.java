package com.anasboot.url_minify.url;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}