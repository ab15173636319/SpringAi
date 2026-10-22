package com.aether.chat.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class UtilityTools {
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Shanghai");

    @Tool(description = "获取用户当前时区的日期和时间；用户询问今天、现在或时间相关问题时使用")
    public String currentDateTime(
            @ToolParam(description = "IANA 时区，例如 Asia/Shanghai；不传时使用 Asia/Shanghai", required = false)
            String timezone
    ) {
        ZoneId zone = timezone == null || timezone.isBlank()
                ? DEFAULT_ZONE
                : ZoneId.of(timezone);
        return LocalDateTime.now(zone).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Tool(description = "获取用户当前时区的日期；用户询问今天是几号或星期几时使用")
    public String currentDate(
            @ToolParam(description = "IANA 时区，例如 Asia/Shanghai；不传时使用 Asia/Shanghai", required = false)
            String timezone
    ) {
        ZoneId zone = timezone == null || timezone.isBlank()
                ? DEFAULT_ZONE
                : ZoneId.of(timezone);
        return LocalDate.now(zone).toString();
    }

    @Tool(description = "粗略判断用户文本表达的情绪，仅供表达方式参考，不用于医疗或心理诊断")
    public String detectEmotion(
            @ToolParam(description = "需要分析的用户文本") String text
    ) {
        String normalized = text == null ? "" : text.toLowerCase(Locale.ROOT);
        if (containsAny(normalized, "焦虑", "紧张", "害怕", "担心", "慌")) return "焦虑";
        if (containsAny(normalized, "难过", "伤心", "失落", "想哭", "失望")) return "低落";
        if (containsAny(normalized, "生气", "愤怒", "离谱", "恼火", "烦")) return "不满";
        if (containsAny(normalized, "开心", "高兴", "太棒", "喜欢", "谢谢")) return "积极";
        return "平静";
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }
}


