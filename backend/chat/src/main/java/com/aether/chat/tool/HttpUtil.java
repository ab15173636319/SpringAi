package com.aether.chat.tool;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class HttpUtil {
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    public String getString(String urlStr) throws IOException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(urlStr))
                .timeout(TIMEOUT)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            return response.statusCode() >= 200 && response.statusCode() < 300
                    ? response.body()
                    : "";
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IOException("HTTP 请求被中断", exception);
        }
    }
}
