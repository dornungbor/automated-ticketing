package com.callsign.ticketing.ports.inbound;

import com.callsign.ticketing.models.AuthenticationResponse;
import com.callsign.ticketing.models.UserLoginApiModel;

import java.util.Map;

public interface AuthenticationRestApiPort {
    AuthenticationResponse authenticate(UserLoginApiModel userLoginApiModel, Map<String, String> headers);
}
