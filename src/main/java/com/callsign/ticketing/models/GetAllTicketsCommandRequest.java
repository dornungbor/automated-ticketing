package com.callsign.ticketing.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class GetAllTicketsCommandRequest extends AccessControl {

    GetAllTicketsCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
