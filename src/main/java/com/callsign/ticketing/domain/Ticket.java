
package com.callsign.ticketing.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document("tickets")
public class Ticket {

    @Transient
    public static final String SEQUENCE_NAME = "ticket_sequence";

    @Id
    @JsonProperty("ticket_id")
    private int ticketId;

    @JsonProperty("delivery_id")
    private int deliveryId;

    @Version
    private int version;

    @JsonProperty("created_on")
    private String createdOn;

    @JsonProperty("last_modified")
    private String lastModified;

    private TicketStatus status;

    private TicketPriority priority;
}
