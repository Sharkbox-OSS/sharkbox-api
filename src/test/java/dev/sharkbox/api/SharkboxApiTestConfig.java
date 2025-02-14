package dev.sharkbox.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@TestConfiguration
public class SharkboxApiTestConfig {
    
    @Bean
    JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue("token")
            .header("alg", "none")
            .claim("sub", "test")
            .claim("roles", "USER")
            .build();
    }

}
