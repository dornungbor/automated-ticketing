package com.callsign.ticketing.domain.commands;

import com.callsign.ticketing.domain.Ticket;
import com.callsign.ticketing.exceptions.ErrorObject;
import com.callsign.ticketing.models.GetAllTicketsCommandRequest;
import com.callsign.ticketing.ports.outbound.PersistencePort;
import com.callsign.ticketing.validation.DefaultConstraintValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GetAllTicketsCommand extends AbstractCommand<GetAllTicketsCommandRequest, List<Ticket>, List<Ticket>> {

    private final DefaultConstraintValidator validator;
    private final PersistencePort persistencePort;

    public GetAllTicketsCommand(
            ObjectMapper mapper,
            DefaultConstraintValidator validator,
            PersistencePort persistencePort) {
        super("GetAllTicketsCommand", mapper);
        this.validator = validator;
        this.persistencePort = persistencePort;
    }

    @Override
    protected List<Ticket> handle(GetAllTicketsCommandRequest request) {
        return this.persistencePort.findAllTickets();
    }

    @Override
    protected List<ErrorObject> validate(GetAllTicketsCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Ticket> apiResponse(List<Ticket> handlerResponse) {
        return handlerResponse;
    }

}
