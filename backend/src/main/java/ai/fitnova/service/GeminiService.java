package ai.fitnova.service;

import ai.fitnova.api.ApiDtos.*;
import ai.fitnova.domain.User;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {
    private final ObjectMapper json;
    @Value("${fitnova.gemini-key:}") private String apiKey;
    @Value("${fitnova.gemini-model:gemini-flash-lite-latest}") private String model;

    public AiStatusView status() {
        return new AiStatusView(apiKey != null && !apiKey.isBlank(), model);
    }

    public AskFitNovaResponse ask(User user, AskFitNovaRequest request) {
        String prompt = "You are FitNova, a concise personal fitness coach. Give practical, conservative general-fitness guidance. " +
            "Do not diagnose or treat medical conditions. Recommend professional care for pain, injury, breathing difficulty, " +
            "dizziness, or other red flags. Address the user as " + user.getFullName() + ". Question: " + request.message();
        return new AskFitNovaResponse(generateText(prompt, null));
    }

    public MealRequest meal(User user, KitchenRequest request) {
        String prompt = "Create one realistic meal using primarily these ingredients: " + String.join(", ", request.ingredients()) +
            ". User goal: " + request.goal() + ". Return safe, ordinary cooking guidance. Do not make medical claims.";
        Map<String,Object> schema = Map.of(
            "type", "object",
            "properties", Map.of(
                "name", Map.of("type", "string"),
                "ingredients", Map.of("type", "array", "items", Map.of("type", "string")),
                "instructions", Map.of("type", "array", "items", Map.of("type", "string")),
                "calories", Map.of("type", "integer", "minimum", 0),
                "proteinG", Map.of("type", "integer", "minimum", 0),
                "prepMinutes", Map.of("type", "integer", "minimum", 1)
            ),
            "required", List.of("name", "ingredients", "instructions", "calories", "proteinG", "prepMinutes")
        );
        try {
            JsonNode value = json.readTree(generateText(prompt, schema));
            return new MealRequest(
                requiredText(value, "name"), request.goal(), stringList(value, "ingredients"),
                stringList(value, "instructions"), value.path("calories").asInt(),
                value.path("proteinG").asInt(), value.path("prepMinutes").asInt());
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Gemini returned an invalid meal. Please try again.");
        }
    }

    private String generateText(String prompt, Map<String,Object> schema) {
        if (apiKey == null || apiKey.isBlank()) throw new IllegalStateException("Gemini is not configured.");
        try {
            Map<String,Object> body = new java.util.LinkedHashMap<>();
            body.put("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));
            if (schema != null) body.put("generationConfig", Map.of("responseMimeType", "application/json", "responseSchema", schema));
            byte[] payload = json.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
            HttpURLConnection connection = (HttpURLConnection) URI.create(
                "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent").toURL().openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15_000);
            connection.setReadTimeout(45_000);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("x-goog-api-key", apiKey);
            connection.setDoOutput(true);
            try (var output = connection.getOutputStream()) { output.write(payload); }
            int status = connection.getResponseCode();
            var stream = status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream();
            String responseBody;
            try (stream) { responseBody = new String(stream.readAllBytes(), StandardCharsets.UTF_8); }
            connection.disconnect();
            if (status < 200 || status >= 300) {
                JsonNode error = json.readTree(responseBody).path("error");
                String providerStatus = error.path("status").asText("UNKNOWN");
                String providerMessage = error.path("message").asText("Request rejected by Gemini.");
                if (status == 400 && "INVALID_ARGUMENT".equals(providerStatus) &&
                    providerMessage.toLowerCase(java.util.Locale.ROOT).contains("api key")) {
                    throw new IllegalStateException("AI Kitchen needs a valid Gemini API key. Update GEMINI_API_KEY in .env.local and restart the backend.");
                }
                throw new IllegalStateException("Gemini request failed (" + status + ", " + providerStatus + "): " + providerMessage);
            }
            JsonNode root = json.readTree(responseBody);
            String text = root.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText();
            if (text.isBlank()) throw new IllegalStateException("Gemini returned no content.");
            return text;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Gemini is temporarily unavailable.");
        }
    }

    private String requiredText(JsonNode value, String field) {
        String text = value.path(field).asText();
        if (text.isBlank()) throw new IllegalArgumentException(field);
        return text;
    }

    private List<String> stringList(JsonNode value, String field) {
        if (!value.path(field).isArray()) throw new IllegalArgumentException(field);
        return java.util.stream.StreamSupport.stream(value.path(field).spliterator(), false)
            .map(JsonNode::asText).filter(x -> !x.isBlank()).toList();
    }
}
