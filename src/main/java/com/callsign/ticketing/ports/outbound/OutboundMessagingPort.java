package com.callsign.ticketing.ports.outbound;

public interface OutboundMessagingPort {
    void publishDeliveryUpdate(String delivery);
}
