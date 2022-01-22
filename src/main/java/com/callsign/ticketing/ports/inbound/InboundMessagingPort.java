package com.callsign.ticketing.ports.inbound;

public interface InboundMessagingPort {
    void onDeliveryUpdate(String delivery);
}
