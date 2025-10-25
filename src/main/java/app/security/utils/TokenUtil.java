package app.security.utils;

import app.security.dtos.UserDTO;
import app.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

import java.util.Base64;

public class TokenUtil {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String SECRET = Utils.getPropertyValue("SECRET_KEY", "config.properties");
    private static final long EXPIRE_MS = Long.parseLong(Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties"));

    public static String createToken(UserDTO user) {
        try {
            Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
            Map<String, Object> payload = new HashMap<>();
            payload.put("sub", user.getUsername());
            payload.put("roles", user.getRoles());
            long now = System.currentTimeMillis();
            payload.put("iat", now);
            payload.put("exp", now + EXPIRE_MS);

            String headerJson = mapper.writeValueAsString(header);
            String payloadJson = mapper.writeValueAsString(payload);

            String headerB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(headerJson.getBytes(StandardCharsets.UTF_8));
            String payloadB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            String unsigned = headerB64 + "." + payloadB64;
            String sig = hmacSha256(unsigned, SECRET);
            return unsigned + "." + sig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static UserDTO verifyToken(String token) {
        try {
            if (token == null || token.isBlank()) return null;
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String headerB64 = parts[0];
            String payloadB64 = parts[1];
            String signature = parts[2];
            String unsigned = headerB64 + "." + payloadB64;
            String expected = hmacSha256(unsigned, SECRET);
            if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), signature.getBytes(StandardCharsets.UTF_8))) {
                return null;
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(payloadB64), StandardCharsets.UTF_8);
            Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);
            long exp = ((Number) payload.getOrDefault("exp", 0)).longValue();
            if (System.currentTimeMillis() > exp) return null;
            String username = (String) payload.get("sub");
            Set<String> roles = new HashSet<>();
            Object r = payload.get("roles");
            if (r instanceof Collection) {
                ((Collection<?>) r).forEach(o -> roles.add(String.valueOf(o)));
            }
            return new UserDTO(username, roles);
        } catch (Exception e) {
            return null;
        }
    }

    private static String hmacSha256(String data, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] macData = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(macData);
    }
}
