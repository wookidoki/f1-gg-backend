package com.f1gg.backend.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class TranslationService {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 구글 무료 번역 API 활용
    public String translateToKorean(String text) {
        try {
            // URL 인코딩 (공백이나 특수문자 처리)
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=ko&dt=t&q=" + encodedText;

            String response = restClient.get().uri(url).retrieve().body(String.class);
            
            JsonNode root = objectMapper.readTree(response);
            return root.get(0).get(0).get(0).asText();
            
        } catch (Exception e) {
            // 실패하면 에러 내지 말고 원본 영어 리턴
            System.out.println("번역 실패: " + text);
            return text;
        }
    }
}