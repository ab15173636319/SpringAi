package ai.s_ai.tools;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@ConfigurationProperties("config.gaode")
@Component
@Data
@Slf4j
public class GaodeTools {
    private String apiKey;
    private String secretKey;
    private String amapIpUrl;
    private String amapWeather;

    @Tool(description = "获取用户定位或位置时使用，未传入ip时获取ip")
    public String getUserIp(@ToolParam(description = "用户ip，获取用户所在位置，未传入则获取用户ip") String ip) throws IOException {
        String urlStr = amapIpUrl + "?output=xml&key=" + apiKey;
        if (ip != null && !ip.isEmpty()) {
            urlStr += "&ip=" + ip;
        }
        log.info("ai调用了工具：{}", urlStr);
        return getString(urlStr);

    }

    @Tool(description = "获取用户所在位置的天气情况，需要先获取Ip信息，当用户需要天气信息、用户提到未来/过去的穿着、前往某地、进行某些室外活动时调用，给出穿着、装备建议。")
    public String getIpWeather(@ToolParam(description = "传入用户所在城市的城市编码（adcode），获取用户所在位置信息") String adcode) throws IOException {
        String urlStr = amapWeather + "?output=xml&key=" + apiKey + "&city=" + adcode;
        return getString(urlStr);
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
