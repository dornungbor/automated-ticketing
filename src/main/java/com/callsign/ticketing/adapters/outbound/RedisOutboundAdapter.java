package com.callsign.ticketing.adapters.outbound;

import com.callsign.ticketing.ports.outbound.OutboundMessagingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisOutboundAdapter implements OutboundMessagingPort {

    private final RedisTopicPublisher redisTopicPublisher;

    @Override
    public void publishDeliveryUpdate(String delivery) {
        this.redisTopicPublisher.publishDeliveryUpdates(delivery);
    }
}
