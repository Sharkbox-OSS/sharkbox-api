package dev.sharkbox.api.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth")
public class AuthController {
    
    private final AuthConfig authConfig;

    AuthController(final AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @GetMapping("/config")
    @Operation(summary = "Retrieve auth configuration")
    public AuthConfig getAuthConfig() {
        return authConfig;
    }
}
