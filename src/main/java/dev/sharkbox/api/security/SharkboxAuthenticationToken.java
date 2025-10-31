package dev.sharkbox.api.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

@Transient
public class SharkboxAuthenticationToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

    private static final long serialVersionUID = 1L;

    public SharkboxAuthenticationToken(Jwt token, Collection<? extends GrantedAuthority> authorities, String userId, String username, String emailAddress, String givenName, String familyName, String ipAddress) {
        super(token, new SharkboxUser(userId, username, emailAddress, givenName, familyName, ipAddress, authorities), null, authorities);
        this.setAuthenticated(true);
    }

    @Override
    public String getName() {
        return getPrincipal().getUsername();
    }

    @Override
    public SharkboxUser getPrincipal() {
        return (SharkboxUser) super.getPrincipal();
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return this.getToken().getClaims();
    }
}
