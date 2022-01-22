package com.callsign.ticketing.adapters.inbound;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@RequiredArgsConstructor
public class DeliveryUpdateMessageSubscriber implements MessageListener {

    private final RedisInboundAdapter redisInboundAdapter;

    @Override
    public void onMessage(Message message, byte[] bytes) {
        redisInboundAdapter.onDeliveryUpdate(String.valueOf(message));
    }
}
