package com.sai.device.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "config.limit")
public class DeviceCallLimitProperties {
    private String deviceCallCountPrefix;
    private Integer callCountExpireDay;
    private Integer maxCallCount;
}
