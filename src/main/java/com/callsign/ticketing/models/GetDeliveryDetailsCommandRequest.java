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
public class GetDeliveryDetailsCommandRequest extends AccessControl {

    private int deliveryId;

    GetDeliveryDetailsCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
