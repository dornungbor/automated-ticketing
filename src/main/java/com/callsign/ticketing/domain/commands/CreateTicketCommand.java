package com.callsign.ticketing.domain.commands;

import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.domain.Ticket;
import com.callsign.ticketing.domain.TicketStatus;
import com.callsign.ticketing.exceptions.CommandException;
import com.callsign.ticketing.exceptions.ErrorObject;
import com.callsign.ticketing.models.CreateTicketCommandRequest;
import com.callsign.ticketing.models.CreateTicketCommandResponse;
import com.callsign.ticketing.ports.outbound.PersistencePort;
import com.callsign.ticketing.validation.DefaultConstraintValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.callsign.ticketing.exceptions.ErrorCode.REQUEST_NOT_FOUND;

@Slf4j
@Service
public class CreateTicketCommand extends AbstractCommand<CreateTicketCommandRequest, CreateTicketCommandResponse, CreateTicketCommandResponse> {

    private final DefaultConstraintValidator validator;
    private final PersistencePort persistencePort;

    public CreateTicketCommand(
            ObjectMapper mapper,
            DefaultConstraintValidator validator,
            PersistencePort persistencePort) {
        super("CreateTicketCommand", mapper);
        this.validator = validator;
        this.persistencePort = persistencePort;
    }

    @Override
    protected CreateTicketCommandResponse handle(CreateTicketCommandRequest request) {
        Delivery found = this.persistencePort.findDeliveryByDeliveryId(request.getCreateTicketApiModel().getDeliveryId())
                .orElseThrow(() -> CommandException.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .errorCode(REQUEST_NOT_FOUND)
                        .message(REQUEST_NOT_FOUND.getDefaultMessage())
                        .build());

        Optional<Ticket> optionalTicket = this.persistencePort.findTicketByDeliveryId(request.getCreateTicketApiModel().getDeliveryId());

        Ticket response;
        if (optionalTicket.isPresent()) {
            Ticket ticket = optionalTicket.get();
            ticket.setPriority(request.getCreateTicketApiModel().getPriority());
            response = this.persistencePort.updateTicketEntry(ticket);
        } else {
            Ticket ticket = Ticket.builder()
                    .deliveryId(found.getDeliveryId())
                    .status(TicketStatus.OPEN)
                    .priority(request.getCreateTicketApiModel().getPriority())
                    .build();
            response = this.persistencePort.createTicketEntry(ticket);
        }

        return CreateTicketCommandResponse.builder()
                .ticketId(response.getDeliveryId())
                .build();
    }

    @Override
    protected List<ErrorObject> validate(CreateTicketCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected CreateTicketCommandResponse apiResponse(CreateTicketCommandResponse handlerResponse) {
        return handlerResponse;
    }

}
