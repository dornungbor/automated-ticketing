package com.callsign.ticketing.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GetTicketDetailsCommandRequest extends AccessControl {

    private int ticketId;

    GetTicketDetailsCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
