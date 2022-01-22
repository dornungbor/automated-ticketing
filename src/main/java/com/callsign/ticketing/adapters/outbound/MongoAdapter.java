package com.callsign.ticketing.adapters.outbound;

import com.callsign.ticketing.configurations.AppConfiguration;
import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.domain.Sequence;
import com.callsign.ticketing.domain.Ticket;
import com.callsign.ticketing.domain.UserIdentity;
import com.callsign.ticketing.ports.outbound.PersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
@RequiredArgsConstructor
@Profile("!test")
public class MongoAdapter implements PersistencePort {

    private final DeliveryRepository deliveryRepository;
    private final TicketRepository ticketRepository;
    private final MongoOperations mongoOperations;

    // using this as a dummy IdP
    private final AppConfiguration configuration;

    @Override
    public Delivery createDeliveryEntry(Delivery delivery) {
        delivery.setDeliveryId(this.nextSequence(Delivery.SEQUENCE_NAME));
        delivery.setCreatedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        delivery.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return deliveryRepository.save(delivery);
    }

    @Override
    public Delivery updateDeliveryEntry(Delivery delivery) {
        delivery.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return deliveryRepository.save(delivery);
    }

    @Override
    public Optional<Delivery> findDeliveryByDeliveryId(int deliveryId) {
        return deliveryRepository.findByDeliveryId(deliveryId);
    }

    @Override
    public Ticket createTicketEntry(Ticket ticket) {
        ticket.setTicketId(this.nextSequence(Ticket.SEQUENCE_NAME));
        ticket.setCreatedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        ticket.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket updateTicketEntry(Ticket ticket) {
        ticket.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return ticketRepository.save(ticket);
    }

    @Override
    public Optional<Ticket> findTicketByTicketId(int ticketId) {
        return ticketRepository.findByTicketId(ticketId);
    }

    @Override
    public Optional<Ticket> findTicketByDeliveryId(int deliveryId) {
        return ticketRepository.findByDeliveryId(deliveryId);
    }

    @Override
    public List<Ticket> findAllTickets() {
        return ticketRepository.findAll();
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
        Sequence counter = mongoOperations.findAndModify(query(where("_id").is(sequenceName)),
                new Update().inc("sequence", 1), options().returnNew(true).upsert(true),
                Sequence.class);
        return !Objects.isNull(counter) ? counter.getSequence() : 1;
    }

}
