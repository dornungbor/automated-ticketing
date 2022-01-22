package com.callsign.ticketing.configurations;

import com.callsign.ticketing.domain.Role;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("ticketing")
@Data
public class AppConfiguration {

    private Redis redis;
    private Session session;
    private Security security;
    private List<DummyUsers> users;

    @Data
    public static class Redis {
        private String host;
        private int port;
        private int timeout;
    }

    @Data
    public static class Session {
        private long defaultAccessTokenExpirySeconds;
        private long defaultRefreshTokenExpirySeconds;
    }

    @Data
    public static class Security {
        private String privateKey;
        private String publicKey;
        private String issuer;
    }

    @Data
    public static class DummyUsers {
        private String username;
        private String hashedPassword;
        private Role role;
    }
}
