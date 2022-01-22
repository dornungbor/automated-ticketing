package com.callsign.ticketing.adapters.outbound;

import com.callsign.ticketing.domain.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TicketRepository extends MongoRepository<Ticket, String> {

    Optional<Ticket> findByTicketId(long ticketId);
    Optional<Ticket> findByDeliveryId(long deliveryId);
}
