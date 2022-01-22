package com.callsign.ticketing.adapters.outbound;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTopicPublisher {

    private final StringRedisTemplate template;
    private final ChannelTopic deliveryUpdatesTopic;

    public void publishDeliveryUpdates(String message) {
        template.convertAndSend(deliveryUpdatesTopic.getTopic(), message);
    }

}
