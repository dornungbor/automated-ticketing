package com.callsign.ticketing.domain.commands;

import com.callsign.ticketing.domain.UserIdentity;
import com.callsign.ticketing.exceptions.CommandException;
import com.callsign.ticketing.exceptions.ErrorObject;
import com.callsign.ticketing.models.AuthenticationResponse;
import com.callsign.ticketing.models.GenericAuthenticateUserCommandRequest;
import com.callsign.ticketing.models.Token;
import com.callsign.ticketing.models.TokenGenerationParameters;
import com.callsign.ticketing.ports.outbound.PersistencePort;
import com.callsign.ticketing.util.AccessTokenUtil;
import com.callsign.ticketing.util.PasswordUtil;
import com.callsign.ticketing.validation.DefaultConstraintValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.callsign.ticketing.exceptions.ErrorCode.*;

@Service
public class AuthenticateUserCommand extends AbstractCommand<GenericAuthenticateUserCommandRequest, AuthenticationResponse, AuthenticationResponse> {

    private final PersistencePort persistencePort;
    private final DefaultConstraintValidator validator;
    private final PasswordUtil passwordUtil;
    private final AccessTokenUtil accessTokenUtil;

    public AuthenticateUserCommand(
            ObjectMapper mapper,
            PersistencePort persistencePort,
            DefaultConstraintValidator validator,
            PasswordUtil passwordUtil,
            AccessTokenUtil accessTokenUtil) {
        super("AuthenticateUserCommand", mapper);
        this.persistencePort = persistencePort;
        this.validator = validator;
        this.passwordUtil = passwordUtil;
        this.accessTokenUtil = accessTokenUtil;
    }

    @Override
    protected AuthenticationResponse handle(GenericAuthenticateUserCommandRequest request) {
        if (!"password".equalsIgnoreCase(request.getUserLoginApiModel().getGrantType())) {
            throw CommandException.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .errorCode(INVALID_GRANT_ERROR)
                    .message(INVALID_GRANT_ERROR.getDefaultMessage())
                    .build();
        }

        UserIdentity found = this.persistencePort.findByUsernameAndPassword(
                request.getUserLoginApiModel().getUsername().toLowerCase(),
                this.passwordUtil.toHashedPassword(request.getUserLoginApiModel().getPassword())
        ).orElseThrow(() -> CommandException.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .errorCode(INVALID_CREDENTIALS_ERROR)
                .message(INVALID_CREDENTIALS_ERROR.getDefaultMessage())
                .build());

        Optional<Token> tokenOptional = this.accessTokenUtil.generateToken(
                TokenGenerationParameters.builder()
                        .username(found.getUsername())
                        .roles(found.getRoles())
                        .build()
        );

        if (tokenOptional.isEmpty()) {
            throw CommandException.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(TOKEN_GENERATION_ERROR)
                    .message(TOKEN_GENERATION_ERROR.getDefaultMessage())
                    .build();
        }

        Token token = tokenOptional.get();
        return AuthenticationResponse.builder()
                .accessToken(token.getAccessToken())
                .accessTokenExpirySeconds(token.getAccessTokenExpiresIn())
                .refreshToken(token.getRefreshToken())
                .refreshTokenExpirySeconds(token.getRefreshTokenExpiresIn())
                .build();
    }

    @Override
    protected List<ErrorObject> validate(GenericAuthenticateUserCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected AuthenticationResponse apiResponse(AuthenticationResponse handlerResponse) {
        return handlerResponse;
    }

}
