package ai.s_ai.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("SpringAi API 文档")       // 文档标题
                                .version("0.01")                    // 接口版本
                                .description("API 接口说明文档")    // 文档描述
                                .contact(
                                        new Contact()
                                                .name("ab15173636319")       // 联系人
                                                .url("https://github.com/ab15173636319") // 地址
                                )
                );
    }

}
