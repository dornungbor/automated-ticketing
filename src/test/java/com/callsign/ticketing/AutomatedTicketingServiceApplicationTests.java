package com.callsign.ticketing;

import com.callsign.ticketing.domain.CustomerType;
import com.callsign.ticketing.domain.commands.AuthenticateUserCommand;
import com.callsign.ticketing.domain.commands.CreateDeliveryCommand;
import com.callsign.ticketing.exceptions.CommandException;
import com.callsign.ticketing.models.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.callsign.ticketing.exceptions.ErrorCode.INVALID_DELIVERY_DATE_ERROR;
import static com.callsign.ticketing.exceptions.ErrorCode.VALIDATION_ERROR;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest
class AutomatedTicketingServiceApplicationTests {

    @Autowired
    private AuthenticateUserCommand authenticateUserCommand;
    @Autowired
    private CreateDeliveryCommand createDeliveryCommand;

    @Test
    @DisplayName("can login with valid credentials")
    void can_login_with_valid_credentials() {
        // prepare
        UserLoginApiModel userLoginApiModel = new UserLoginApiModel();
        userLoginApiModel.setUsername("dornu");
        userLoginApiModel.setPassword("super-secret");
        userLoginApiModel.setGrantType("password");
        GenericAuthenticateUserCommandRequest authenticateUserCommandRequest = GenericAuthenticateUserCommandRequest.builder()
                .userLoginApiModel(userLoginApiModel)
                .build();

        // act
        AuthenticationResponse response = this.authenticateUserCommand.execute(authenticateUserCommandRequest);

        // assert
        assertAll(
                () -> assertNotNull(response, () -> "response should NOT be NULL"),
                () -> assertTrue(StringUtils.hasText(response.getAccessToken()), () -> "access token should NOT be EMPTY")
        );
    }

    @Test
    @DisplayName("can fail login with invalid credentials")
    void can_fail_login_with_invalid_credentials() {
        // prepare
        UserLoginApiModel userLoginApiModel = new UserLoginApiModel();
        userLoginApiModel.setUsername("invalid");
        userLoginApiModel.setPassword("super-secret");
        userLoginApiModel.setGrantType("password");
        GenericAuthenticateUserCommandRequest authenticateUserCommandRequest = GenericAuthenticateUserCommandRequest.builder()
                .userLoginApiModel(userLoginApiModel)
                .build();

        // act & assert
        CommandException commandException = assertThrows(
                CommandException.class,
                () -> this.authenticateUserCommand.execute(authenticateUserCommandRequest)
        );

        // assert
        assertAll(
                () -> assertNotNull(commandException, () -> "commandException should NOT be NULL"),
                () -> assertEquals(commandException.getStatus(), UNAUTHORIZED, () -> "http status should be UNAUTHORIZED")
        );
    }

    @Test
    @DisplayName("should create delivery with valid data")
    void should_create_delivery_with_valid_data() {
        // prepare
        CreateDeliveryApiModel createDeliveryApiModel = new CreateDeliveryApiModel();
        createDeliveryApiModel.setCustomerType(CustomerType.VIP);
        createDeliveryApiModel.setExpectedDeliveryTime(Instant.now().plus(30, ChronoUnit.MINUTES).toString());
        createDeliveryApiModel.setCurrentDistanceFromDestinationInMeters(500);
        createDeliveryApiModel.setTimeToReachDestination(Instant.now().plus(30, ChronoUnit.MINUTES).toString());
        createDeliveryApiModel.setRiderRating(5);
        createDeliveryApiModel.setRestaurantMeanTimeToPrepareFoodInMinutes(10);

        CreateDeliveryCommandRequest createDeliveryCommandRequest = CreateDeliveryCommandRequest.builder()
                .createDeliveryApiModel(createDeliveryApiModel)
                .build();

        // act
        CreateDeliveryCommandResponse response = this.createDeliveryCommand.execute(createDeliveryCommandRequest);

        // assert
        assertAll(
                () -> assertNotNull(response, () -> "response should NOT be NULL"),
                () -> assertTrue(response.getDeliveryId() > 0l, () -> "delivery id should be non-zero")
        );
    }

    @Test
    @DisplayName("should not create delivery with validation errors")
    void should_not_create_delivery_with_validation_error() {
        // prepare
        CreateDeliveryApiModel createDeliveryApiModel = new CreateDeliveryApiModel();
        createDeliveryApiModel.setCustomerType(CustomerType.VIP);

        CreateDeliveryCommandRequest createDeliveryCommandRequest = CreateDeliveryCommandRequest.builder()
                .createDeliveryApiModel(createDeliveryApiModel)
                .build();

        // act & assert
        CommandException commandException = assertThrows(
                CommandException.class,
                () -> this.createDeliveryCommand.execute(createDeliveryCommandRequest)
        );

        // assert
        assertAll(
                () -> assertNotNull(commandException, () -> "commandException should NOT be NULL"),
                () -> assertEquals(commandException.getStatus(), BAD_REQUEST, () -> "http status should be BAD_REQUEST"),
                () -> assertEquals(commandException.getErrorCode(), VALIDATION_ERROR, () -> "http status should be VALIDATION_ERROR")
        );
    }

    @Test
    @DisplayName("should not create delivery with invalid data")
    void should_not_create_delivery_with_invalid_data() {
        // prepare
        CreateDeliveryApiModel createDeliveryApiModel = new CreateDeliveryApiModel();
        createDeliveryApiModel.setCustomerType(CustomerType.VIP);
        createDeliveryApiModel.setExpectedDeliveryTime(Instant.now().minus(30, ChronoUnit.MINUTES).toString());
        createDeliveryApiModel.setCurrentDistanceFromDestinationInMeters(500);
        createDeliveryApiModel.setTimeToReachDestination(Instant.now().plus(30, ChronoUnit.MINUTES).toString());
        createDeliveryApiModel.setRiderRating(5);
        createDeliveryApiModel.setRestaurantMeanTimeToPrepareFoodInMinutes(10);

        CreateDeliveryCommandRequest createDeliveryCommandRequest = CreateDeliveryCommandRequest.builder()
                .createDeliveryApiModel(createDeliveryApiModel)
                .build();

        // act & assert
        CommandException commandException = assertThrows(
                CommandException.class,
                () -> this.createDeliveryCommand.execute(createDeliveryCommandRequest)
        );

        // assert
        assertAll(
                () -> assertNotNull(commandException, () -> "commandException should NOT be NULL"),
                () -> assertEquals(commandException.getStatus(), UNPROCESSABLE_ENTITY, () -> "http status should be UNPROCESSABLE_ENTITY"),
                () -> assertEquals(commandException.getErrorCode(), INVALID_DELIVERY_DATE_ERROR, () -> "http status should be INVALID_DELIVERY_DATE_ERROR")
        );
    }
}
