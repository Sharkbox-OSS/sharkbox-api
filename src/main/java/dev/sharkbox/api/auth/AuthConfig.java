package dev.sharkbox.api.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sharkbox.auth")
public class AuthConfig {
    
    private String stsServer;

    private String clientId;

    private String rolesLocation;

    public String getStsServer() {
        return stsServer;
    }

    public void setStsServer(String stsServer) {
        this.stsServer = stsServer;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRolesLocation() {
        return rolesLocation;
    }

    public void setRolesLocation(String rolesLocation) {
        this.rolesLocation = rolesLocation;
    }
}
