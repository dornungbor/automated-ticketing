package com.callsign.ticketing.helpers;

import com.callsign.ticketing.models.JwtPayload;
import com.callsign.ticketing.models.JwtToken;
import org.springframework.stereotype.Service;

import java.util.Base64;

import static com.callsign.ticketing.helpers.Helper.fromJson;

@Service
public class JwtHelper {

    public JwtToken toJwtToken(String accessToken) {
        String[] chunks = accessToken.split("\\.");

        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        String header = new String(decoder.decode(chunks[0]));
        String signature = chunks[2];

        return JwtToken.builder()
                .header(header)
                .payload(payload)
                .signature(signature)
                .build();
    }

    public JwtPayload getJwtPayload(String accessToken) {
        JwtToken jwtToken = this.toJwtToken(accessToken);
        return fromJson(jwtToken.getPayload(), JwtPayload.class);
    }

    public String getUsername(String accessToken) {
        JwtPayload payload = this.getJwtPayload(accessToken);
        return payload.getSub();
    }
}
