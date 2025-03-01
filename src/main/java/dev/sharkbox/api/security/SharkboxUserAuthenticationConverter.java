package dev.sharkbox.api.security;

import java.util.Optional;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import jakarta.servlet.http.HttpServletRequest;

public class SharkboxUserAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final GrantedAuthoritiesExtractor jwtGrantedAuthoritiesExtractor;
    private final HttpServletRequest request;

    public SharkboxUserAuthenticationConverter(GrantedAuthoritiesExtractor jwtGrantedAuthoritiesExtractor, HttpServletRequest request) {
        this.jwtGrantedAuthoritiesExtractor = jwtGrantedAuthoritiesExtractor;
        this.request = request;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        var authorities = this.jwtGrantedAuthoritiesExtractor.convert(source);
        var given_name = source.getClaimAsString("given_name");
        var family_name = source.getClaimAsString("family_name");
        var email = source.getClaimAsString("email");
        var username = source.getClaimAsString("preferred_username");

        var ipAddress = Optional.ofNullable(request.getRemoteAddr()).orElse("UNKNOWN");

        return new SharkboxAuthenticationToken(source, authorities, username, email, given_name, family_name, ipAddress);
    }
}
