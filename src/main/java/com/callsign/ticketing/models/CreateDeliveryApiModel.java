package com.callsign.ticketing.models;

import com.callsign.ticketing.domain.CustomerType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateDeliveryApiModel {

    @NotNull
    @JsonProperty("customer_type")
    private CustomerType customerType;

    @NotEmpty
    @JsonProperty("expected_delivery_time")
    private String expectedDeliveryTime;

    @Min(value = 0)
    @JsonProperty("current_distance_from_destination_in_meters")
    private int currentDistanceFromDestinationInMeters;

    @NotEmpty
    @JsonProperty("time_to_reach_destination")
    private String timeToReachDestination;

    @Min(value = 0)
    @JsonProperty("rider_rating")
    private float riderRating;

    @Min(value = 0)
    @JsonProperty("restaurant_mean_time_to_prepare_food_in_minutes")
    private int restaurantMeanTimeToPrepareFoodInMinutes;
}
