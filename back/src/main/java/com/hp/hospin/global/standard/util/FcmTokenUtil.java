package com.hp.hospin.global.standard.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FcmTokenUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * JSON 문자열에서 fcmToken 필드 값만 추출
     * @param jsonToken {"fcmToken":"토큰값"} 형태의 문자열
     * @return 순수 토큰 문자열, 예외 발생 시 null
     */
    public static String parseFcmToken(String jsonToken) {
        try {
            JsonNode node = objectMapper.readTree(jsonToken);
            JsonNode tokenNode = node.get("token");
            if (tokenNode != null) {
                return tokenNode.asText().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // 파싱 실패 시 null 반환
    }
}