package com.callsign.ticketing.util;

import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.domain.TicketPriority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.callsign.ticketing.domain.DeliveryStatus.ORDER_DELIVERED;
import static com.callsign.ticketing.domain.DeliveryStatus.ORDER_PICKED_UP;

@Service
public class TicketPriorityUtil {

    public TicketPriority determinePriority(Delivery delivery) {
        if (Instant.parse(delivery.getExpectedDeliveryTime()).isBefore(Instant.now())
                && delivery.getDeliveryStatus() != ORDER_DELIVERED) {
            return TicketPriority.HIGH;
        }

        Instant expectedPickupTime = Instant.parse(delivery.getCreatedOn()).plus(delivery.getRestaurantMeanTimeToPrepareFoodInMinutes(), ChronoUnit.MINUTES);
        if (expectedPickupTime.isBefore(Instant.now())
                && delivery.getDeliveryStatus() != ORDER_PICKED_UP && delivery.getDeliveryStatus() != ORDER_DELIVERED) {
            return TicketPriority.MEDIUM;
        }

        switch (delivery.getCustomerType()) {
            case VIP:
                return TicketPriority.HIGH;
            case LOYAL:
                return TicketPriority.MEDIUM;
            case NEW:
            default:
                return TicketPriority.LOW;
        }
    }

}
