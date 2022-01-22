package com.callsign.ticketing.ports.outbound;

import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.domain.Ticket;
import com.callsign.ticketing.domain.UserIdentity;

import java.util.List;
import java.util.Optional;

public interface PersistencePort {
    Delivery createDeliveryEntry(Delivery delivery);
    Delivery updateDeliveryEntry(Delivery delivery);
    Optional<Delivery> findDeliveryByDeliveryId(int deliveryId);

    Ticket createTicketEntry(Ticket ticket);
    Ticket updateTicketEntry(Ticket ticket);
    Optional<Ticket> findTicketByTicketId(int ticketId);
    Optional<Ticket> findTicketByDeliveryId(int deliveryId);
    List<Ticket> findAllTickets();

    Optional<UserIdentity> findByUsername(String username);
    Optional<UserIdentity> findByUsernameAndPassword(String username, String hashedPassword);

    int nextSequence(String sequenceName);
}
