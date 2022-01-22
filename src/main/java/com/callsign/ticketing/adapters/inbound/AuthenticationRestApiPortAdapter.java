package com.callsign.ticketing.adapters.inbound;

import com.callsign.ticketing.domain.commands.AuthenticateUserCommand;
import com.callsign.ticketing.models.AuthenticatedUser;
import com.callsign.ticketing.models.AuthenticationResponse;
import com.callsign.ticketing.models.GenericAuthenticateUserCommandRequest;
import com.callsign.ticketing.models.UserLoginApiModel;
import com.callsign.ticketing.ports.inbound.AuthenticationRestApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.callsign.ticketing.helpers.Helper.fromHeaders;


@RestController
@RequestMapping("/identity/auth")
@RequiredArgsConstructor
public class AuthenticationRestApiPortAdapter implements AuthenticationRestApiPort {

    private final AuthenticateUserCommand authenticateUserCommand;

    @PostMapping("/login")
    @ResponseBody
    @Override
    public AuthenticationResponse authenticate(
            @RequestBody UserLoginApiModel userLoginApiModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GenericAuthenticateUserCommandRequest authenticateUserCommandRequest = GenericAuthenticateUserCommandRequest.builder()
                .userLoginApiModel(userLoginApiModel)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.authenticateUserCommand.execute(authenticateUserCommandRequest);
    }

}
