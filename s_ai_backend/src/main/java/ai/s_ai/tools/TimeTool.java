package ai.s_ai.tools;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class TimeTool {

    @Tool(description = "获取当前时间")
    public String getCurrentTime() {
        return java.time.LocalDateTime.now().toString();
    }

    @Tool(description = "获取当前日期")
    public String getCurrentDate() {
        return java.time.LocalDate.now().toString();
    }

    @Tool(description = "获取农历时间")
    public String getLunarTime(@ToolParam(description = "Unix 时间戳，支持 10 位秒级或 13 位毫秒级。不传则默认当前时间，可选") String ts,
                               @ToolParam(description = "时区名称。支持 IANA 时区（如 Asia/Shanghai）和别名（Shanghai、Beijing）。默认 Asia/Shanghai，可选") String timezone) throws IOException {
        String url = "https://uapis.cn/api/v1/misc/lunartime?ts=" + ts + "&timezone=" + timezone;
        return getString(url);
    }


    @NonNull
    private String getString(String urlStr) throws IOException {
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