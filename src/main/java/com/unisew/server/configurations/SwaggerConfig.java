package com.unisew.server.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "UniSew",
                version = "1.0",
                description = "API for dev UniSew"
        ),
        servers = {
                @Server(
                        description = "Local host",
                        url = "http://localhost:8080/"
                )
        }
)
public class SwaggerConfig {
}
