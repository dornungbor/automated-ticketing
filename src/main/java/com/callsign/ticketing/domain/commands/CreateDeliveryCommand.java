package com.callsign.ticketing.domain.commands;

import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.domain.DeliveryStatus;
import com.callsign.ticketing.exceptions.CommandException;
import com.callsign.ticketing.exceptions.ErrorCode;
import com.callsign.ticketing.exceptions.ErrorObject;
import com.callsign.ticketing.models.CreateDeliveryCommandRequest;
import com.callsign.ticketing.models.CreateDeliveryCommandResponse;
import com.callsign.ticketing.models.GenericDeliveryCommandResponse;
import com.callsign.ticketing.ports.outbound.OutboundMessagingPort;
import com.callsign.ticketing.ports.outbound.PersistencePort;
import com.callsign.ticketing.validation.DefaultConstraintValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static com.callsign.ticketing.common.Constants.ERROR_SOURCE;
import static com.callsign.ticketing.exceptions.ErrorCode.VALIDATION_ERROR;

@Slf4j
@Service
public class CreateDeliveryCommand extends AbstractCommand<CreateDeliveryCommandRequest, GenericDeliveryCommandResponse, CreateDeliveryCommandResponse>
        implements UpdatesPublisherCommand<GenericDeliveryCommandResponse> {

    private final DefaultConstraintValidator validator;
    private final PersistencePort persistencePort;
    private final OutboundMessagingPort outboundMessagingPort;

    public CreateDeliveryCommand(
            ObjectMapper mapper,
            DefaultConstraintValidator validator,
            PersistencePort persistencePort,
            OutboundMessagingPort outboundMessagingPort) {
        super("CreateDeliveryCommand", mapper);
        this.validator = validator;
        this.persistencePort = persistencePort;
        this.outboundMessagingPort = outboundMessagingPort;
    }

    @Override
    protected GenericDeliveryCommandResponse handle(CreateDeliveryCommandRequest request) {
        if (Instant.parse(request.getCreateDeliveryApiModel().getExpectedDeliveryTime()).isBefore(Instant.now())) {
            throw CommandException.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .errorCode(ErrorCode.INVALID_DELIVERY_DATE_ERROR)
                    .message(ErrorCode.INVALID_DELIVERY_DATE_ERROR.getDefaultMessage())
                    .build();
        }
        if (Instant.parse(request.getCreateDeliveryApiModel().getTimeToReachDestination()).isBefore(Instant.now())) {
            throw CommandException.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .errorCode(ErrorCode.INVALID_EXPECTED_DATE_ERROR)
                    .message(ErrorCode.INVALID_EXPECTED_DATE_ERROR.getDefaultMessage())
                    .build();
        }

        Delivery delivery = Delivery.builder()
                .customerType(request.getCreateDeliveryApiModel().getCustomerType())
                .deliveryStatus(DeliveryStatus.ORDER_RECEIVED)
                .expectedDeliveryTime(request.getCreateDeliveryApiModel().getExpectedDeliveryTime())
                .currentDistanceFromDestinationInMeters(request.getCreateDeliveryApiModel().getCurrentDistanceFromDestinationInMeters())
                .timeToReachDestination(request.getCreateDeliveryApiModel().getTimeToReachDestination())
                .riderRating(request.getCreateDeliveryApiModel().getRiderRating())
                .restaurantMeanTimeToPrepareFoodInMinutes(request.getCreateDeliveryApiModel().getRestaurantMeanTimeToPrepareFoodInMinutes())
                .build();

        Delivery response = this.persistencePort.createDeliveryEntry(delivery);

        return GenericDeliveryCommandResponse.builder()
                .deliveryId(response.getDeliveryId())
                .delivery(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(CreateDeliveryCommandRequest request) {
        List<ErrorObject> errors = new ArrayList<>();

        if (StringUtils.hasText(request.getCreateDeliveryApiModel().getExpectedDeliveryTime())) {
            try {
                Instant.parse(request.getCreateDeliveryApiModel().getExpectedDeliveryTime());
            } catch (DateTimeParseException exception) {
                errors.add(errorObject("expected_delivery_time must be a valid date"));
            }
        }

        if (StringUtils.hasText(request.getCreateDeliveryApiModel().getTimeToReachDestination())) {
            try {
                Instant.parse(request.getCreateDeliveryApiModel().getTimeToReachDestination());
            } catch (DateTimeParseException exception) {
                errors.add(errorObject("time_to_reach_destination must be a valid date"));
            }
        }

        errors.addAll(this.validator.validate(request));
        return errors;
    }

    @Override
    protected CreateDeliveryCommandResponse apiResponse(GenericDeliveryCommandResponse handlerResponse) {
        return CreateDeliveryCommandResponse.builder().deliveryId(handlerResponse.getDeliveryId()).build();
    }

    private ErrorObject errorObject(String message) {
        return ErrorObject.builder()
                .code(VALIDATION_ERROR)
                .message(message)
                .source(ERROR_SOURCE)
                .build();
    }


    @Override
    public OutboundMessagingPort getOutboundMessagingPort() {
        return this.outboundMessagingPort;
    }

    @Override
    public String convertToUpdatesMessage(Delivery delivery) throws JsonProcessingException {
        return this.mapper.writeValueAsString(delivery);
    }

}
