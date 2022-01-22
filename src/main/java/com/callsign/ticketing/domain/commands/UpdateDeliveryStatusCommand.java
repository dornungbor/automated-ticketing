package com.callsign.ticketing.domain.commands;

import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.exceptions.CommandException;
import com.callsign.ticketing.exceptions.ErrorObject;
import com.callsign.ticketing.models.GenericDeliveryCommandResponse;
import com.callsign.ticketing.models.UpdateDeliveryStatusCommandRequest;
import com.callsign.ticketing.ports.outbound.OutboundMessagingPort;
import com.callsign.ticketing.ports.outbound.PersistencePort;
import com.callsign.ticketing.validation.DefaultConstraintValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.callsign.ticketing.exceptions.ErrorCode.REQUEST_NOT_FOUND;

@Slf4j
@Service
public class UpdateDeliveryStatusCommand extends AbstractCommand<UpdateDeliveryStatusCommandRequest, GenericDeliveryCommandResponse, Void>
        implements UpdatesPublisherCommand<GenericDeliveryCommandResponse> {

    private final DefaultConstraintValidator validator;
    private final PersistencePort persistencePort;
    private final OutboundMessagingPort outboundMessagingPort;

    public UpdateDeliveryStatusCommand(
            ObjectMapper mapper,
            DefaultConstraintValidator validator,
            PersistencePort persistencePort,
            OutboundMessagingPort outboundMessagingPort) {
        super("UpdateDeliveryStatusCommand", mapper);
        this.validator = validator;
        this.persistencePort = persistencePort;
        this.outboundMessagingPort = outboundMessagingPort;
    }

    @Override
    protected GenericDeliveryCommandResponse handle(UpdateDeliveryStatusCommandRequest request) {
        Delivery found = this.persistencePort.findDeliveryByDeliveryId(request.getDeliveryId())
                .orElseThrow(() -> CommandException.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .errorCode(REQUEST_NOT_FOUND)
                        .message(REQUEST_NOT_FOUND.getDefaultMessage())
                        .build());

        found.setDeliveryStatus(request.getDeliveryStatus());

        Delivery response = this.persistencePort.updateDeliveryEntry(found);

        return GenericDeliveryCommandResponse.builder()
                .deliveryId(response.getDeliveryId())
                .delivery(response)
                .build();
    }

    @Override
    protected List<ErrorObject> validate(UpdateDeliveryStatusCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected Void apiResponse(GenericDeliveryCommandResponse handlerResponse) {
        return null;
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
