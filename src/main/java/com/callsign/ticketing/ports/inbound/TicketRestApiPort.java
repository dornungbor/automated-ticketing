package com.callsign.ticketing.ports.inbound;

import com.callsign.ticketing.domain.Ticket;
import com.callsign.ticketing.domain.TicketStatus;
import com.callsign.ticketing.models.CreateTicketApiModel;
import com.callsign.ticketing.models.CreateTicketCommandResponse;

import java.util.List;
import java.util.Map;

public interface TicketRestApiPort {

    CreateTicketCommandResponse createTicket(CreateTicketApiModel createTicketApiModel, Map<String, String> headers);
    void updateTicketStatus(int ticketId, TicketStatus ticketStatus, Map<String, String> headers);
    Ticket getTicketDetails(int ticketId, Map<String, String> headers);
    List<Ticket> getAllTickets(Map<String, String> headers);

}
