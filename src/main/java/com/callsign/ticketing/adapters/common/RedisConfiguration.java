package com.callsign.ticketing.adapters.common;

import com.callsign.ticketing.configurations.AppConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static com.callsign.ticketing.common.Constants.DELIVERY_UPDATES_TOPIC;

@Configuration
public class RedisConfiguration {

    @Bean
    public JedisConnectionFactory connectionFactory(AppConfiguration appConfiguration) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(appConfiguration.getRedis().getHost());
        configuration.setPort(appConfiguration.getRedis().getPort());
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    public ChannelTopic deliveryUpdatesTopic() {
        return new ChannelTopic(DELIVERY_UPDATES_TOPIC);
    }
}
