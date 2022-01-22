package com.callsign.ticketing.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RsaKeys {
    private String base64PrivateKey;
    private String base64PublicKey;
}
