package com.callsign.ticketing.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTicketCommandResponse {

    @JsonProperty("ticket_id")
    private long ticketId;
}
