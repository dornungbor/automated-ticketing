package com.callsign.ticketing.adapters.inbound;

import com.callsign.ticketing.adapters.common.RedisConfiguration;
import com.callsign.ticketing.configurations.AppConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisInboundConfiguration {

    private final RedisConfiguration redisConfiguration;

    @Bean("deliveryUpdateListenerAdapter")
    MessageListenerAdapter deliveryUpdateListenerAdapter(RedisInboundAdapter redisInboundAdapter) {
        return new MessageListenerAdapter(new DeliveryUpdateMessageSubscriber(redisInboundAdapter));
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer(RedisInboundAdapter redisInboundAdapter, AppConfiguration appConfiguration) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConfiguration.connectionFactory(appConfiguration));
        container.addMessageListener(deliveryUpdateListenerAdapter(redisInboundAdapter), redisConfiguration.deliveryUpdatesTopic());
        return container;
    }

}
