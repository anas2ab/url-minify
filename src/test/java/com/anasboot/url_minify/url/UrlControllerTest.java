package com.anasboot.url_minify.url;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.anasboot.url_minify.TestcontainersConfiguration;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureMockMvc
@Transactional 
public class UrlControllerTest {
    
    @Autowired 
    MockMvc mockMvc;

    @Autowired 
    UrlRepository urlRepository;

    @Test
    void createsShortUrl() throws Exception {
        mockMvc.perform(post("/api/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"longUrl\": \"https://www.example.com\"}"))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.code").exists())
            .andExpect(jsonPath("$.longUrl").value("https://www.example.com"));
    }

    @Test 
    void redirectsToLongUrl() throws Exception {
        // First, create a short URL
        String response = mockMvc.perform(post("/api/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"longUrl\": \"https://www.example.com\"}"))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String code = JsonPath.read(response, "$.code");

        // Now, test the redirection
        mockMvc.perform(get("/" + code))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", "https://www.example.com"));
    }

    @Test 
    void returns404ForUnknownCode() throws Exception {
        mockMvc.perform(get("/nope123"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail").value("No URL found for code: nope123"));
    }

    @Test 
    void returns409ForTakenAlias() throws Exception {
        // First, create a short URL with a custom alias
        mockMvc.perform(post("/api/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"longUrl\": \"https://www.example.com\", \"customAlias\": \"my-alias\"}"))
            .andExpect(status().isCreated());

        // Now, try to create another short URL with the same alias
        mockMvc.perform(post("/api/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"longUrl\": \"https://www.example.org\", \"customAlias\": \"my-alias\"}"))
            .andExpect(status().isConflict());
    }

    @Test 
    void returns400ForInvalidUrl() throws Exception {
        mockMvc.perform(post("/api/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"longUrl\": \"not-a-url\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test 
    void returns410ForExpiredUrl() throws Exception {
        // First, create a short URL with an expiry in the past
        long id = urlRepository.nextId();
        Url expiredUrl = Url.generated(id, "https://www.example.com", Instant.now().minusSeconds(60));
        expiredUrl = urlRepository.save(expiredUrl);

        String code = expiredUrl.getCode();

        // Now, test the redirection for the expired URL
        mockMvc.perform(get("/" + code))
            .andExpect(status().isGone())
            .andExpect(jsonPath("$.detail").value("URL has expired for code: " + code));
    }

    @Test 
    void rejectsPastExpiry() throws Exception {
        mockMvc.perform(post("/api/urls")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"longUrl\": \"https://www.example.com\", \"expiresAt\": \"" + Instant.now().minusSeconds(60) + "\"}"))
            .andExpect(status().isBadRequest());
    }
}
