package com.callsign.ticketing.adapters.inbound;

import com.callsign.ticketing.domain.Ticket;
import com.callsign.ticketing.domain.TicketStatus;
import com.callsign.ticketing.domain.commands.CreateTicketCommand;
import com.callsign.ticketing.domain.commands.GetAllTicketsCommand;
import com.callsign.ticketing.domain.commands.GetTicketDetailsCommand;
import com.callsign.ticketing.domain.commands.UpdateTicketStatusCommand;
import com.callsign.ticketing.helpers.Helper;
import com.callsign.ticketing.models.*;
import com.callsign.ticketing.ports.inbound.TicketRestApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketRestApiAdapter implements TicketRestApiPort {

    private final CreateTicketCommand createTicketCommand;
    private final UpdateTicketStatusCommand updateTicketStatusCommand;
    private final GetTicketDetailsCommand getTicketDetailsCommand;
    private final GetAllTicketsCommand getAllTicketsCommand;

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public CreateTicketCommandResponse createTicket(
            @RequestBody CreateTicketApiModel createTicketApiModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = Helper.fromHeaders(headers);
        CreateTicketCommandRequest createTicketCommandRequest = CreateTicketCommandRequest.builder()
                .createTicketApiModel(createTicketApiModel)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.createTicketCommand.execute(createTicketCommandRequest);
    }

    @PutMapping("/{ticketId}/status/{status}")
    @Override
    public void updateTicketStatus(
            @PathVariable int ticketId,
            @PathVariable TicketStatus status,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = Helper.fromHeaders(headers);
        UpdateTicketStatusCommandRequest updateTicketStatusCommandRequest = UpdateTicketStatusCommandRequest.builder()
                .ticketId(ticketId)
                .ticketStatus(status)
                .authenticatedUser(authenticatedUser)
                .build();
        this.updateTicketStatusCommand.execute(updateTicketStatusCommandRequest);
    }

    @GetMapping("/{ticketId}")
    @ResponseBody
    @Override
    public Ticket getTicketDetails(
            @PathVariable int ticketId,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = Helper.fromHeaders(headers);
        GetTicketDetailsCommandRequest getTicketDetailsCommandRequest = GetTicketDetailsCommandRequest.builder()
                .ticketId(ticketId)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getTicketDetailsCommand.execute(getTicketDetailsCommandRequest);
    }

    @GetMapping
    @ResponseBody
    @Override
    public List<Ticket> getAllTickets(Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = Helper.fromHeaders(headers);
        GetAllTicketsCommandRequest getAllTicketsCommandRequest = GetAllTicketsCommandRequest.builder()
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getAllTicketsCommand.execute(getAllTicketsCommandRequest);
    }
}
