package ai.s_ai.tools;

import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/*
    设定ai返回的字段名称和格式
 */
@Component
public class JsonBack {

    @Resource
    private ObjectMapper objectMapper;


    /**
     * 普通日常闲聊，返回html+tailwindcss格式
     *
     * @param userQuery 用户原始提问
     * @return json字符串 {type,msg,quote}
     */
    @Tool(description = "兜底工具：当用户消息不属于计划、任务规划、查询、工具调用等任何明确场景时调用，即普通日常闲聊、寒暄、情感交流、主观聊天（如问好、聊心情、随便聊）。")
    public String primaryChat(@ToolParam(description = "用户原始提问") String userQuery) {
        return "以json格式返回{msg:‘回答用户的内容，以html+tailwindcss格式返回’,qute:[为用户提供三条话题]}";
    }

    /**
     * 计划、任务、规划类对话，返回markdown格式
     *
     * @param userQuery 用户原始提问
     * @return json字符串 {type,msg,quote}
     */
    @Tool(description = "仅当用户明确提出计划、任务规划、方案编排类需求时调用（如：帮我安排周末计划、制定学习计划、规划旅行行程、列任务清单）")
    public String scheduleChat(@ToolParam(description = "用户原始提问") String userQuery) {
        return "以json格式返回{msg:‘回答用户的内容，以markdown格式返回’,qute:[为用户提供三条话题]}";
    }


}
