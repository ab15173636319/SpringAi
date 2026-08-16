package ai.s_ai.components;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "config.limit")
public class DeviceCallCountProperties {

    private String deviceCallCountPrefix;
    private Integer CallCountExpireDay;
    private Integer maxCallCount;

}
