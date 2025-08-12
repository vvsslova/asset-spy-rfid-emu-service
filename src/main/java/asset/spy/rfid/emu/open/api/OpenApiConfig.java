package asset.spy.rfid.emu.open.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Asset-spy-rfid-emu-service")
                        .description("Service for emulating the movement of products")
                        .version("1.0.0"));
    }
}
