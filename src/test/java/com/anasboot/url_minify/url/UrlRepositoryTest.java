package com.anasboot.url_minify.url;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import com.anasboot.url_minify.Base62;
import com.anasboot.url_minify.TestcontainersConfiguration;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(TestcontainersConfiguration.class)
public class UrlRepositoryTest {
    @Autowired
    UrlRepository urlRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void savesGeneratedUrlAndFindsItByCode() {
        long id = urlRepository.nextId();
        Url save = Url.generated(id, "https://www.example.com", null);
        save = urlRepository.save(save);
        assertTrue(id >= 100000);
        String code = save.getCode();
        assertEquals(Base62.encode(id), code);
        entityManager.flush();
        entityManager.clear();
        Optional<Url> found = urlRepository.findByCode(code);
        assertTrue(found.isPresent());
        assertEquals(save.getLongUrl(), found.get().getLongUrl());
        assertEquals(code, found.get().getCode());
        assertFalse(found.get().isCustom());
    }

    @Test 
    void savesCustomUrlAndFindsItByCode() {
        long id = urlRepository.nextId();
        Url save = Url.custom(id, "my-alias", "https://www.example.com", null);
        save = urlRepository.save(save);
        String code = save.getCode();
        entityManager.flush();
        entityManager.clear();
        Optional<Url> found = urlRepository.findByCode(code);
        assertTrue(found.isPresent());
        assertEquals(save.getLongUrl(), found.get().getLongUrl());
        assertEquals(code, found.get().getCode());
        assertTrue(found.get().isCustom());
    }

    @Test 
    void rejectsDuplicateCode() {
        long id1 = urlRepository.nextId();
        Url save1 = Url.custom(id1, "taken", "https://www.example.com", null);
        urlRepository.save(save1);
        long id2 = urlRepository.nextId();
        Url save2 = Url.custom(id2, "taken", "https://www.example.org", null);
        assertThrows(DataIntegrityViolationException.class, () -> urlRepository.saveAndFlush(save2));
    }
}
