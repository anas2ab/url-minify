package com.anasboot.url_minify.url;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByCode(String code);
    boolean existsByCode(String code);
    @Query(value = "select nextval('url_id_seq')", nativeQuery = true)
    long nextId();
}
