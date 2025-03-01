package dev.sharkbox.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

import dev.sharkbox.api.auth.AuthConfig;
import dev.sharkbox.api.security.GrantedAuthoritiesExtractor;
import dev.sharkbox.api.security.SharkboxUserAuthenticationConverter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(AuthConfig.class)
@OpenAPIDefinition(
    info = @Info(
        title = "Sharkbox API",
        description = "Sharkbox API Description",
        version = "${info.app.version:LOCAL-SNAPSHOT}"
    ),
    security = {
        @SecurityRequirement(name = "sharkbox")
    }
)
@SecurityScheme(
    name = "sharkbox",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
        authorizationCode = @OAuthFlow(
            authorizationUrl = "${sharkbox.auth.stsServer}/protocol/openid-connect/auth",
            tokenUrl = "${sharkbox.auth.stsServer}/protocol/openid-connect/token")
    )
)
public class SharkboxConfiguration {

    private final Logger logger = LoggerFactory.getLogger(SharkboxConfiguration.class);

    private final AuthConfig authConfig;
    private final HttpServletRequest request;

    SharkboxConfiguration(final AuthConfig authConfig, final HttpServletRequest request) {
        this.authConfig = authConfig;
        this.request = request;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(request -> request
                // Docs
                .requestMatchers(
                    "/api/v1/auth/config",
                    "/api/docs/**"
                ).permitAll()
                // Most requests are public, we'll have to check auth for specific requests
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(sharkboxAuthenticationConverter())))
            .build();
    }

    @Bean
    @Profile("!test")
    JwtDecoder jwtDecoder() {
        logger.info("Configuring OAuth2 via {}", authConfig.getStsServer());
        
        // Read the configuration from the STS server
        return JwtDecoders.fromOidcIssuerLocation(authConfig.getStsServer());
    }

    private Converter<Jwt, AbstractAuthenticationToken> sharkboxAuthenticationConverter() {
        return new SharkboxUserAuthenticationConverter(new GrantedAuthoritiesExtractor(authConfig), request);
    }
}
