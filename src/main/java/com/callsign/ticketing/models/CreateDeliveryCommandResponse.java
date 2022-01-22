package com.callsign.ticketing.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateDeliveryCommandResponse {

    @JsonProperty("delivery_id")
    private long deliveryId;
}
