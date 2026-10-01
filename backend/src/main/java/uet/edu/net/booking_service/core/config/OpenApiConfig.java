package uet.edu.net.booking_service.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình Swagger / OpenAPI.
 * Truy cập tài liệu API tại: http://localhost:8080/swagger-ui.html
 *
 * Yêu cầu dependency trong pom.xml:
 *   springdoc-openapi-starter-webmvc-ui
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Thông tin hiển thị trên trang Swagger UI
                .info(new Info().title("Hotel Booking API").version("1.0"))

                // Áp dụng scheme "bearerAuth" cho toàn bộ API mặc định
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

                // Định nghĩa scheme "bearerAuth":
                // → Người dùng paste JWT vào ô "Authorize" trên Swagger UI
                // → Swagger tự động thêm header: Authorization: Bearer <token>
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)   // dùng HTTP header
                                .scheme("bearer")                 // scheme name
                                .bearerFormat("JWT")));           // hint cho UI hiển thị
    }
}