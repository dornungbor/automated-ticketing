package com.callsign.ticketing.domain.commands;

import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.ports.outbound.OutboundMessagingPort;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Objects;

public interface UpdatesPublisherCommand<T extends DeliverySupplierCommand> {

    OutboundMessagingPort getOutboundMessagingPort();

    String convertToUpdatesMessage(Delivery delivery) throws JsonProcessingException;

    default void publishUpdates(T t) {
        Delivery delivery = t.getDelivery();
        OutboundMessagingPort outboundMessagingPort = this.getOutboundMessagingPort();
        if (Objects.nonNull(delivery) && Objects.nonNull(outboundMessagingPort)) {
            try {
                String message = this.convertToUpdatesMessage(delivery);
                outboundMessagingPort.publishDeliveryUpdate(message);
            } catch (JsonProcessingException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}
