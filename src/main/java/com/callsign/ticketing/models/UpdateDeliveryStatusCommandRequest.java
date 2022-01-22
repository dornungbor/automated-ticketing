package com.callsign.ticketing.models;

import com.callsign.ticketing.domain.DeliveryStatus;
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
public class UpdateDeliveryStatusCommandRequest extends AccessControl {

    private int deliveryId;

    @NotNull
    private DeliveryStatus deliveryStatus;

    UpdateDeliveryStatusCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
