package dev.sharkbox.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import dev.sharkbox.api.auth.AuthConfig;

public class GrantedAuthoritiesExtractor implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final AuthConfig authConfig;

    GrantedAuthoritiesExtractor(final AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> roles = (List<String>) mapSearch(jwt.getClaims(), authConfig.getRolesLocation());

        if (null == roles) {
            return Collections.emptyList();
        }

        return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role))
            .collect(Collectors.toList());
    }

    public Object mapSearch(final Map<String, ?> map, final String search) {
        var keys = Arrays.asList(search.split("\\."));

        return getValueForKeyPath(map, keys);
    }

    public Object getValueForKeyPath(Map<String, ?> map, List<String> keys) {
        if (keys.size() == 1) {
            return map.get(keys.get(0));
        }
        return getValueForKeyPath((Map<String, ?>) map.get(keys.get(0)), keys.subList(1, keys.size()));
    }
}
