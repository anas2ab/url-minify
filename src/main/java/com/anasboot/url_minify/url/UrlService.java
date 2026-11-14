package com.anasboot.url_minify.url;

import java.time.Instant;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.anasboot.url_minify.Base62;

import org.springframework.transaction.annotation.Transactional;

@Service 
public class UrlService {
    
    private final UrlRepository urlRepository;
    
    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Transactional 
    public Url shorten(String longUrl, String customAlias, Instant expiresAt) {
        if(customAlias != null && !customAlias.isEmpty()) {
            if(urlRepository.existsByCode(customAlias)) {
                throw new AliasTakenException("Alias already exists: " + customAlias);
            }
            long id = urlRepository.nextId();
            Url url = Url.custom(id, customAlias, longUrl, expiresAt);
            try {
                url = urlRepository.saveAndFlush(url);
            } catch (DataIntegrityViolationException e) {
                throw new AliasTakenException("Alias already exists: " + customAlias, e);
            }
            return url;
        }
        else {
            while(true) {
                long id = urlRepository.nextId();
                String code = Base62.encode(id);
                if(urlRepository.existsByCode(code)) {
                    // Collision occurred, try again with a new ID
                    continue;
                }

                Url url = Url.generated(id, longUrl, expiresAt);
                url = urlRepository.saveAndFlush(url);
                return url;
            }
        }
    }
}
