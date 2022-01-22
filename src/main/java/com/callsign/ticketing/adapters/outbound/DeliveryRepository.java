package com.callsign.ticketing.adapters.outbound;

import com.callsign.ticketing.domain.Delivery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DeliveryRepository extends MongoRepository<Delivery, String> {

    Optional<Delivery> findByDeliveryId(long deliveryId);
}
