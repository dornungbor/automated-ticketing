package com.callsign.ticketing.util;

import com.callsign.ticketing.configurations.AppConfiguration;
import com.callsign.ticketing.models.RsaKeys;
import com.callsign.ticketing.models.Token;
import com.callsign.ticketing.models.TokenGenerationParameters;
import com.callsign.ticketing.models.TokenType;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.callsign.ticketing.models.TokenType.ACCESS_TOKEN;
import static com.callsign.ticketing.models.TokenType.REFRESH_TOKEN;

@Service
@Slf4j
public class AccessTokenUtil {

    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS512;
    private PrivateKey privateKey;
    private RSAPublicKey publicKey;
    private final AppConfiguration.Security security;
    private final AppConfiguration.Session session;

    public AccessTokenUtil(AppConfiguration appConfiguration) {
        this.security = appConfiguration.getSecurity();
        this.session = appConfiguration.getSession();
        this.initKeyPair(RsaKeys.builder()
                .base64PrivateKey(this.security.getPrivateKey())
                .base64PublicKey(this.security.getPublicKey())
                .build());
    }

    private void initKeyPair(RsaKeys rsaKeys) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(rsaKeys.getBase64PrivateKey());
            privateKey = getPrivateKey(privateKeyBytes);

            byte[] publicKeyBytes = Base64.getDecoder().decode(rsaKeys.getBase64PublicKey());
            publicKey = getPublicKey(publicKeyBytes);
        } catch (Exception exception) {
            throw new RuntimeException("Unable to parse public/private key pair", exception);
        }
    }

    private RSAPublicKey getPublicKey(byte[] publicKeyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(publicKeyBytes);
        return (RSAPublicKey) keyFactory.generatePublic(keySpecX509);
    }

    private PrivateKey getPrivateKey(byte[] privateKeyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(keySpecPKCS8);
    }

    public Optional<Token> generateToken(TokenGenerationParameters tokenGenerationParameters) {
        try {
            String accessToken = generateToken(ACCESS_TOKEN, tokenGenerationParameters);
            String refreshToken = generateToken(REFRESH_TOKEN, tokenGenerationParameters);
            return Optional.of(Token.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiresIn(this.session.getDefaultAccessTokenExpirySeconds())
                    .refreshToken(refreshToken)
                    .refreshTokenExpiresIn(this.session.getDefaultRefreshTokenExpirySeconds())
                    .build());
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    private String generateToken(TokenType tokenType, TokenGenerationParameters tokenGenerationParameters) {
        Date now = Date.from(ZonedDateTime.now().toInstant());
        JwtBuilder builder = Jwts.builder()
                .signWith(signatureAlgorithm, privateKey)
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("kid", UUID.randomUUID().toString())
                .setId(UUID.randomUUID().toString())
                .setIssuer(this.security.getIssuer())
                .claim("azp", "dummy-client")
                .claim("typ", tokenType.getType())
                .claim("auth_time", now)
                .setSubject(tokenGenerationParameters.getUsername())
                .setAudience("dummy-client")
                .setIssuedAt(now);

        long tokenExpirySeconds;
        if (tokenType == ACCESS_TOKEN) {
            builder.claim("roles", tokenGenerationParameters.getRoles());
            tokenExpirySeconds = this.session.getDefaultAccessTokenExpirySeconds();
        } else {
            tokenExpirySeconds = this.session.getDefaultRefreshTokenExpirySeconds();
        }

        builder.setExpiration(Date.from(ZonedDateTime.now()
                .plusSeconds(tokenExpirySeconds)
                .toInstant()));

        return builder.compact();
    }

    public boolean verifyToken(String accessToken) {
        try {
            Jwts.parser().setSigningKey(this.publicKey).parseClaimsJws(accessToken).getBody();
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException |
                UnsupportedJwtException | IllegalArgumentException exception) {
            return false;
        }
    }
}



