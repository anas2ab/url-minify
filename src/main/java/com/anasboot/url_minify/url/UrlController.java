package com.anasboot.url_minify.url;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
@RestControllerAdvice 
public class UrlController {
    
    private final UrlService urlService;
    
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/urls")
    public ResponseEntity<ShortenResponse> shortenUrl(@Valid @RequestBody ShortenRequest request) {
        Url url = urlService.shorten(request.longUrl(), request.customAlias(), request.expiresAt());
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{code}").buildAndExpand(url.getCode()).toUri();
        return ResponseEntity.created(uri).body(ShortenResponse.from(url, uri.toString()));
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String code) {
        String longUrl = urlService.resolve(code);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
    }
}
