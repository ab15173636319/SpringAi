package ai.s_ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

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

}
