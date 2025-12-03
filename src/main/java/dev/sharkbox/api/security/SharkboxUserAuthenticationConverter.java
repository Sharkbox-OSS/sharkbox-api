package dev.sharkbox.api.security;

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import jakarta.servlet.http.HttpServletRequest;

public class SharkboxUserAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final GrantedAuthoritiesExtractor jwtGrantedAuthoritiesExtractor;
    private final HttpServletRequest request;

    public SharkboxUserAuthenticationConverter(GrantedAuthoritiesExtractor jwtGrantedAuthoritiesExtractor,
            HttpServletRequest request) {
        this.jwtGrantedAuthoritiesExtractor = jwtGrantedAuthoritiesExtractor;
        this.request = request;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        var authorities = this.jwtGrantedAuthoritiesExtractor.convert(source);
        var givenName = source.getClaimAsString("given_name");
        var familyName = source.getClaimAsString("family_name");
        var email = source.getClaimAsString("email");
        var username = source.getClaimAsString("preferred_username");

        // Use 'preferred_username' as userId
        var userId = username;

        var ipAddress = Optional.ofNullable(request.getRemoteAddr()).orElse("UNKNOWN");

        return new SharkboxAuthenticationToken(source, authorities, userId, username, email, givenName, familyName,
                ipAddress);
    }
}
