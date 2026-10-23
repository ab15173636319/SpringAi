package com.aether.chat.tool;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class HttpUtil {

    @NonNull
    public String getString(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setConnectTimeout(5000);
        http.setReadTimeout(5000);
        int code = http.getResponseCode();
        StringBuilder sb = new StringBuilder();
        if (code == 200) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            bufferedReader.close();
        }
        http.disconnect();
        return sb.toString();
    }

}
