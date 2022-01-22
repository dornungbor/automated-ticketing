package com.callsign.ticketing.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CreateTicketCommandRequest extends AccessControl {

    @NotNull
    @Valid
    private CreateTicketApiModel createTicketApiModel;

    CreateTicketCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
