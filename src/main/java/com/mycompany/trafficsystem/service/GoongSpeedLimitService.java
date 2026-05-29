package com.mycompany.trafficsystem.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoongSpeedLimitService {

    private static final String API_URL = "https://speedlimit.goong.io/api/v1/speedlimit";
    private static final String API_KEY_FILE = "API_Key";
    private static final String API_KEY_NAME = "GOONG_API_KEY";
    private static final Pattern SPEED_PATTERN = Pattern.compile("\"max_speed\"\\s*:\\s*(\\d+)");

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public Integer getSpeedLimit(double latitude, double longitude) {
        String apiKey = readApiKey();

        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.contains("your_goong_api_key_here")) {
            System.out.println("Chưa đọc được GOONG_API_KEY. Kiểm tra file API_Key ở thư mục chạy ứng dụng.");
            return null;
        }

        try {
            String url = API_URL
                    + "?lat=" + latitude
                    + "&lon=" + longitude
                    + "&api_key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Goong Speed Limit API trả status " + response.statusCode()
                        + ": " + response.body());
                return null;
            }

            Integer speed = extractSpeed(response.body());

            if (speed == null) {
                System.out.println("Không tìm thấy speed trong phản hồi Goong: " + response.body());
            } else {
                System.out.println("Đã lấy tốc độ tối đa từ Goong: " + speed + " km/h");
            }

            return speed;
        } catch (IOException e) {
            System.out.println("Không thể kết nối Goong Speed Limit API: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Đã hủy gọi Goong Speed Limit API: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Phản hồi Goong Speed Limit API không hợp lệ: " + e.getMessage());
        }

        return null;
    }

    private String readApiKey() {
        String keyFromFile = readApiKeyFromFile();

        if (keyFromFile != null && !keyFromFile.trim().isEmpty()) {
            return keyFromFile.trim();
        }

        String keyFromEnv = System.getenv(API_KEY_NAME);
        return keyFromEnv == null ? null : keyFromEnv.trim();
    }

    private String readApiKeyFromFile() {
        for (Path keyPath : getCandidateKeyPaths()) {
            if (!Files.exists(keyPath)) {
                continue;
            }

            try {
                for (String line : Files.readAllLines(keyPath, StandardCharsets.UTF_8)) {
                    String trimmedLine = line.trim();

                    if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                        continue;
                    }

                    if (trimmedLine.startsWith(API_KEY_NAME + "=")) {
                        String apiKey = trimmedLine.substring((API_KEY_NAME + "=").length()).trim();
                        System.out.println("Đã đọc Goong API key từ " + keyPath.toAbsolutePath()
                                + " (" + maskApiKey(apiKey) + ")");
                        return apiKey;
                    }
                }
            } catch (IOException e) {
                System.out.println("Không thể đọc file API_Key tại "
                        + keyPath.toAbsolutePath() + ": " + e.getMessage());
            }
        }

        return null;
    }

    private Path[] getCandidateKeyPaths() {
        return new Path[] {
                Path.of(API_KEY_FILE),
                Path.of(System.getProperty("user.dir"), API_KEY_FILE),
                Path.of(System.getProperty("user.dir")).getParent() == null
                        ? Path.of(API_KEY_FILE)
                        : Path.of(System.getProperty("user.dir")).getParent().resolve(API_KEY_FILE)
        };
    }

    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "********";
        }

        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }

    private Integer extractSpeed(String responseBody) {
        Matcher matcher = SPEED_PATTERN.matcher(responseBody);

        if (!matcher.find()) {
            return null;
        }

        return Integer.parseInt(matcher.group(1));
    }
}
