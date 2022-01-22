package com.callsign.ticketing.adapters.inbound;

import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.domain.TicketPriority;
import com.callsign.ticketing.domain.commands.CreateTicketCommand;
import com.callsign.ticketing.exceptions.CommandException;
import com.callsign.ticketing.models.CreateTicketApiModel;
import com.callsign.ticketing.models.CreateTicketCommandRequest;
import com.callsign.ticketing.ports.inbound.InboundMessagingPort;
import com.callsign.ticketing.ports.outbound.PersistencePort;
import com.callsign.ticketing.util.TicketPriorityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.callsign.ticketing.domain.DeliveryStatus.ORDER_DELIVERED;
import static com.callsign.ticketing.domain.DeliveryStatus.ORDER_PICKED_UP;
import static com.callsign.ticketing.exceptions.ErrorCode.INVALID_MESSAGE_ERROR;
import static com.callsign.ticketing.exceptions.ErrorCode.REQUEST_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RedisInboundAdapter implements InboundMessagingPort {

    protected final ObjectMapper mapper;
    private final PersistencePort persistencePort;
    private final CreateTicketCommand createTicketCommand;
    private final TicketPriorityUtil ticketPriorityUtil;

    @Override
    public void onDeliveryUpdate(String delivery) {
        Delivery mapped;
        try {
            mapped = this.mapper.readValue(delivery, Delivery.class);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(CommandException.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode(INVALID_MESSAGE_ERROR)
                    .message(exception.getMessage())
                    .cause(exception)
                    .build());
        }

        if (mapped.getDeliveryStatus() == ORDER_DELIVERED) {
            return;
        }

        this.handleIncorrectEstimation(mapped);

        this.scheduleOrderNotPickedUpCheck(mapped);

        this.scheduleOrderNotDeliveredCheck(mapped);
    }

    private CreateTicketCommandRequest createTicketCommandRequest(Delivery mapped) {
        TicketPriority priority = this.ticketPriorityUtil.determinePriority(mapped);

        CreateTicketApiModel createTicketApiModel = new CreateTicketApiModel();
        createTicketApiModel.setDeliveryId(mapped.getDeliveryId());
        createTicketApiModel.setPriority(priority);

        return CreateTicketCommandRequest.builder()
                .createTicketApiModel(createTicketApiModel)
                .build();
    }

    private void handleIncorrectEstimation(Delivery delivery) {
        int prepTimeInMinutes = delivery.getRestaurantMeanTimeToPrepareFoodInMinutes();
        Instant estimatedTime = Instant.parse(delivery.getTimeToReachDestination()).plus(prepTimeInMinutes, ChronoUnit.MINUTES);

        if (estimatedTime.isAfter(Instant.parse(delivery.getExpectedDeliveryTime()))) {
            CreateTicketCommandRequest createTicketCommandRequest = createTicketCommandRequest(delivery);
            this.createTicketCommand.execute(createTicketCommandRequest);
        }
    }

    private void scheduleOrderNotPickedUpCheck(Delivery delivery) {
        Executors.newScheduledThreadPool(1).schedule(
                () -> {
                    Delivery found = getDeliveryOrElseThrow(delivery);
                    if (found.getDeliveryStatus() != ORDER_PICKED_UP && found.getDeliveryStatus() != ORDER_DELIVERED) {
                        CreateTicketCommandRequest createTicketCommandRequest = createTicketCommandRequest(found);
                        this.createTicketCommand.execute(createTicketCommandRequest);
                    }
                },
                ChronoUnit.MINUTES.between(
                        Instant.now(),
                        Instant.now().plus(delivery.getRestaurantMeanTimeToPrepareFoodInMinutes(), ChronoUnit.MINUTES)
                ),
                TimeUnit.MINUTES
        );
    }

    private void scheduleOrderNotDeliveredCheck(Delivery delivery) {
        Executors.newScheduledThreadPool(1).schedule(
                () -> {
                    Delivery found = getDeliveryOrElseThrow(delivery);
                    if (found.getDeliveryStatus() != ORDER_DELIVERED) {
                        CreateTicketCommandRequest createTicketCommandRequest = createTicketCommandRequest(found);
                        this.createTicketCommand.execute(createTicketCommandRequest);
                    }
                },
                ChronoUnit.MINUTES.between(
                        Instant.now(),
                        Instant.parse(delivery.getExpectedDeliveryTime())
                ),
                TimeUnit.MINUTES
        );
    }

    private Delivery getDeliveryOrElseThrow(Delivery delivery) {
        return this.persistencePort.findDeliveryByDeliveryId(delivery.getDeliveryId())
                .orElseThrow(() -> CommandException.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .errorCode(REQUEST_NOT_FOUND)
                        .message(REQUEST_NOT_FOUND.getDefaultMessage())
                        .build());
    }
}
