package com.callsign.ticketing.models;

import com.callsign.ticketing.domain.TicketPriority;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateTicketApiModel {

    @JsonProperty("delivery_id")
    private int deliveryId;

    @NotNull
    private TicketPriority priority;

}
