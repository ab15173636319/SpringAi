package ai.s_ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class EmotionUtil {

    public enum Emotion {
        HAPPY("开心", EmotionType.POSITIVE, new String[]{"开心", "快乐", "爽", "太棒了", "真好"}),
        ANGRY("愤怒", EmotionType.NEGATIVE, new String[]{"生气", "气死", "恼火", "离谱", "暴躁"}),
        SAD("悲伤", EmotionType.NEGATIVE, new String[]{"难过", "伤心", "想哭", "emo", "心酸"}),
        ANXIOUS("焦虑", EmotionType.NEGATIVE, new String[]{"焦虑", "紧张", "慌", "害怕", "忐忑"}),
        CALM("平静", EmotionType.NEUTRAL, new String[]{"还好", "还行", "一般", "正常"});

        private final String name;
        private final EmotionType type;
        private final String[] keywords;

        Emotion(String name, EmotionType type, String[] keywords) {
            this.name = name;
            this.type = type;
            this.keywords = keywords;
        }

        public String getName() {
            return name;
        }

        public EmotionType getType() {
            return type;
        }

        public String[] getKeywords() {
            return keywords;
        }
    }

    public enum EmotionType {
        POSITIVE, NEUTRAL, NEGATIVE
    }


    public static List<Emotion> getEmotions() {
        List<Emotion> emotions = new ArrayList<Emotion>();
        Collections.addAll(emotions, Emotion.values());
        return emotions;
    }


    public static Emotion parseTextEmotion(String text) {
        if (text == null || text.isBlank()) {
            return Emotion.CALM;
        }
        text = text.toLowerCase();
        for (Emotion emotion : Emotion.values()) {
            for (String keyword : emotion.getKeywords()) {
                if (text.contains(keyword)) {
                    return emotion;
                }
            }
        }
        return Emotion.CALM;
    }

    @Tool(description = "根据用户输入的文本判断用户当时的心情")
    public String getEmotion(@ToolParam(description = "需要分析用户的文本") String text) {
        return parseTextEmotion(text).getName();
    }
}
