
package com.callsign.ticketing.models;

import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.domain.commands.DeliverySupplierCommand;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class GenericDeliveryCommandResponse implements DeliverySupplierCommand {

    private final int deliveryId;
    private final Delivery delivery;
}
