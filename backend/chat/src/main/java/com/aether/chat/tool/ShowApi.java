package com.aether.chat.tool;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
@Slf4j
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "config.showapi")
public class ShowApi {

    private static final DateTimeFormatter YMD = DateTimeFormatter.BASIC_ISO_DATE;

    private final HttpUtil httpUtil;

    private String appKey;
    private String huangli;

    @Tool(description = "查询指定公历日期的黄历信息，返回公历/农历日期、干支、纳音、生肖、星座、值神、#宜#忌、冲煞、吉神凶煞、二十四节气、节日、五行穿衣配色建议等。"
            + "当用户询问今天是几号农历、今天是否黄道吉日、宜忌、择日（嫁娶、搬家、开业、动土、出行等）、冲煞、穿什么颜色时调用。")
    public String getHuangli(
            @ToolParam(required = false,
                    description = "要查询的日期，公历，格式 yyyy-MM-dd（如 2026-02-11）或 yyyyMMdd（如 20260211）；不传或传空则查询今天，可选")
            String date) throws IOException {
        String ymd = normalizeYmd(date);
        String urlStr = huangli + "?appKey=" + appKey + "&ymd=" + ymd;
        log.info("ai调用了工具：查询黄历 {}", urlStr);
        String result = httpUtil.getString(urlStr);
        if (result.isEmpty()) {
            return "黄历查询失败：接口无响应，请稍后重试（ymd=" + ymd + "）";
        }
        return result;
    }

    /**
     * 格式化日期，确保为 yyyy-MM-dd 格式
     *
     * @param date 日期字符串
     * @return 格式化后的日期字符串，或当前日期字符串，如果输入为空或格式错误
     */
    private String normalizeYmd(String date) {
        if (date == null || date.isBlank()) {
            return LocalDate.now().format(YMD);
        }
        String value = date.trim().replace("-", "").replace("/", "").replace(".", "");
        if (value.length() != 8 || !value.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("日期格式不正确，请使用 yyyy-MM-dd 或 yyyyMMdd，当前值：" + date);
        }
        try {
            return LocalDate.parse(value, YMD).format(YMD);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日期不存在：" + date, e);
        }
    }

}
