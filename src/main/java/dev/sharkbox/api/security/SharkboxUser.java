package dev.sharkbox.api.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SharkboxUser implements UserDetails {
    private String userId;
    private String username;
    private String emailAddress;
    private String givenName;
    private String familyName;
    private Collection<? extends GrantedAuthority> roles;
    private String ipAddress;

    public SharkboxUser(String userId, String username, String emailAddress, String givenName, String familyName, String ipAddress, Collection<? extends GrantedAuthority> roles) {
        this.userId = userId;
        this.username = username;
        this.emailAddress = emailAddress;
        this.givenName = givenName;
        this.familyName = familyName;
        this.ipAddress = ipAddress;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
