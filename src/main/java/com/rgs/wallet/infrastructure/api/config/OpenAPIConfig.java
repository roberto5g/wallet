package com.rgs.wallet.infrastructure.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "wallet-service - Digital Wallet Management API",
                version = "1.0",
                description = "API for managing digital wallets, balances, and wallet transactions",
                contact = @Contact(
                        name = "Wallet API Support",
                        email = "ccp.gualberto@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Development environment")
        }
)
public class OpenAPIConfig {
}