
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
@Document("deliveries")
public class Delivery {

    @Transient
    public static final String SEQUENCE_NAME = "delivery_sequence";

    @Id
    @JsonProperty("delivery_id")
    private int deliveryId;

    @Version
    private int version;

    @JsonProperty("created_on")
    private String createdOn;

    @JsonProperty("last_modified")
    private String lastModified;

    @JsonProperty("customer_type")
    private CustomerType customerType;

    @JsonProperty("delivery_status")
    private DeliveryStatus deliveryStatus;

    @JsonProperty("expected_delivery_time")
    private String expectedDeliveryTime;

    @JsonProperty("current_distance_from_destination_in_meters")
    private int currentDistanceFromDestinationInMeters;

    @JsonProperty("time_to_reach_destination")
    private String timeToReachDestination;

    @JsonProperty("rider_rating")
    private float riderRating;

    @JsonProperty("restaurant_mean_time_to_prepare_food_in_minutes")
    private int restaurantMeanTimeToPrepareFoodInMinutes;
}
