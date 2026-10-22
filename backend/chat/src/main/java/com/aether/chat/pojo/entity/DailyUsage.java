package com.aether.chat.pojo.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "daily_usage")
@CompoundIndex(name = "usage_user_day_unique", def = "{'userId': 1, 'day': 1}", unique = true)
public class DailyUsage {
    @Id
    private String id;
    private String userId;
    private String day;
    private int count;
}



