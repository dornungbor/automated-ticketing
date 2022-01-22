package com.callsign.ticketing.domain.commands;

import com.callsign.ticketing.domain.Ticket;
import com.callsign.ticketing.exceptions.CommandException;
import com.callsign.ticketing.exceptions.ErrorObject;
import com.callsign.ticketing.models.UpdateTicketStatusCommandRequest;
import com.callsign.ticketing.ports.outbound.PersistencePort;
import com.callsign.ticketing.validation.DefaultConstraintValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.callsign.ticketing.exceptions.ErrorCode.REQUEST_NOT_FOUND;

@Slf4j
@Service
public class UpdateTicketStatusCommand extends AbstractCommand<UpdateTicketStatusCommandRequest, Void, Void> {

    private final DefaultConstraintValidator validator;
    private final PersistencePort persistencePort;

    public UpdateTicketStatusCommand(
            ObjectMapper mapper,
            DefaultConstraintValidator validator,
            PersistencePort persistencePort) {
        super("UpdateTicketStatusCommand", mapper);
        this.validator = validator;
        this.persistencePort = persistencePort;
    }

    @Override
    protected Void handle(UpdateTicketStatusCommandRequest request) {
        Ticket found = this.persistencePort.findTicketByTicketId(request.getTicketId())
                .orElseThrow(() -> CommandException.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .errorCode(REQUEST_NOT_FOUND)
                        .message(REQUEST_NOT_FOUND.getDefaultMessage())
                        .build());

        found.setStatus(request.getTicketStatus());
        this.persistencePort.updateTicketEntry(found);
        return null;                
    }

    @Override
    protected List<ErrorObject> validate(UpdateTicketStatusCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected Void apiResponse(Void handlerResponse) {
        return null;
    }    

}
