package com.callsign.ticketing.models;

import com.callsign.ticketing.domain.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UpdateTicketStatusCommandRequest extends AccessControl {

    private int ticketId;

    @NotNull
    private TicketStatus ticketStatus;

    UpdateTicketStatusCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
