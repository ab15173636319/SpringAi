package ai.s_ai.utils;

import ai.s_ai.components.PromptXmlReader;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReadXmlUtil {

    @Resource
    private PromptXmlReader promptXmlReader;

    public String getPrompts() {
        String[] names = {"chat", "schedule", "path"};
        StringBuilder s = new StringBuilder();
        for (String name : names) {
            s.append(getPrompt(name));
        }
        return s.toString();
    }


    public String getPrompt(String prompt) {
        Map<String, String> p = promptXmlReader.readPrompt(prompt);
        return "如果用户期望是：" + p.get("identify") + ",就使用以下方式回答用户（不能提及方式）：" +
                p.get("content");
    }

}
