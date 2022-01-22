package com.callsign.ticketing.adapters.outbound;

import com.callsign.ticketing.configurations.AppConfiguration;
import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.domain.Ticket;
import com.callsign.ticketing.domain.UserIdentity;
import com.callsign.ticketing.ports.outbound.PersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Profile("test")
public class MongoAdapterMock implements PersistencePort {

    private final AppConfiguration configuration;
    private static int sequence = 0;

    @Override
    public Delivery createDeliveryEntry(Delivery delivery) {
        delivery.setDeliveryId(this.nextSequence(Delivery.SEQUENCE_NAME));
        delivery.setCreatedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        delivery.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return delivery;
    }

    @Override
    public Delivery updateDeliveryEntry(Delivery delivery) {
        delivery.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return delivery;
    }

    @Override
    public Optional<Delivery> findDeliveryByDeliveryId(int deliveryId) {
        return Optional.of(Delivery.builder().deliveryId(deliveryId).build());
    }

    @Override
    public Ticket createTicketEntry(Ticket ticket) {
        ticket.setTicketId(this.nextSequence(Ticket.SEQUENCE_NAME));
        ticket.setCreatedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        ticket.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return ticket;
    }

    @Override
    public Ticket updateTicketEntry(Ticket ticket) {
        ticket.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return ticket;
    }

    @Override
    public Optional<Ticket> findTicketByTicketId(int ticketId) {
        return Optional.of(Ticket.builder().ticketId(ticketId).build());
    }

    @Override
    public Optional<Ticket> findTicketByDeliveryId(int deliveryId) {
        return Optional.of(Ticket.builder().ticketId(1).deliveryId(deliveryId).build());
    }

    @Override
    public List<Ticket> findAllTickets() {
        return Collections.singletonList(Ticket.builder().ticketId(1).build());
    }

    @Override
    public Optional<UserIdentity> findByUsername(String username) {
        // using this as a dummy IdP
        return this.configuration.getUsers().stream()
                .filter(x -> x.getUsername().equalsIgnoreCase(username))
                .map(x -> UserIdentity.builder()
                        .id(0)
                        .username(x.getUsername())
                        .hashedPassword(x.getHashedPassword())
                        .roles(Collections.singletonList(x.getRole()))
                        .build())
                .findFirst();
    }

    @Override
    public Optional<UserIdentity> findByUsernameAndPassword(String username, String hashedPassword) {
        // using this as a dummy IdP
        return this.configuration.getUsers().stream()
                .filter(x -> x.getUsername().equalsIgnoreCase(username) && x.getHashedPassword().equals(hashedPassword))
                .map(x -> UserIdentity.builder()
                        .id(0)
                        .username(x.getUsername())
                        .hashedPassword(x.getHashedPassword())
                        .roles(Collections.singletonList(x.getRole()))
                        .build())
                .findFirst();
    }

    @Override
    public int nextSequence(String sequenceName) {
        return ++sequence;
    }

}
