package dev.sharkbox.api;

import java.util.Arrays;

import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import dev.sharkbox.api.security.SharkboxAuthenticationToken;

class WithMockSharkboxUserSecurityContextFactory implements WithSecurityContextFactory<WithSharkboxUser> {

    @Override
    public SecurityContext createSecurityContext(WithSharkboxUser mockUser) {
        var context = SecurityContextHolder.createEmptyContext();

        var auth = new SharkboxAuthenticationToken(
            Mockito.mock(Jwt.class),
            Arrays.stream(mockUser.roles()).map(SimpleGrantedAuthority::new).toList(),
            mockUser.username(),
            mockUser.emailAddress(),
            mockUser.givenName(),
            mockUser.familyName(),
            "127.0.0.1"
        );
        
        context.setAuthentication(auth);

        return context;
    }
}
