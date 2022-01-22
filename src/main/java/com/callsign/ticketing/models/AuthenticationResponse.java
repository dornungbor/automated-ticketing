
package com.callsign.ticketing.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("access_token_expiry_seconds")
    private Long accessTokenExpirySeconds;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("refresh_token_expiry_seconds")
    private Long refreshTokenExpirySeconds;
}
