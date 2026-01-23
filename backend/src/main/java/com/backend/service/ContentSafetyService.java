package com.backend.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ContentSafetyService {
    private final List<String> BAD_WORDS = List.of("나쁜말", "욕설");

    public String validateAndSanitize(String content) {
        if (content == null || content.isBlank()) return "";
        for (String badWord : BAD_WORDS) {
            if (content.contains(badWord)) throw new IllegalArgumentException("비속어가 포함되어 있습니다.");
        }
        return Jsoup.clean(content, Safelist.none());
    }
}