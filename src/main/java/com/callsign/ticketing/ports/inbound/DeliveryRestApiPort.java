package com.callsign.ticketing.ports.inbound;

import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.domain.DeliveryStatus;
import com.callsign.ticketing.models.CreateDeliveryApiModel;
import com.callsign.ticketing.models.CreateDeliveryCommandResponse;

import java.util.Map;

public interface DeliveryRestApiPort {

    CreateDeliveryCommandResponse createDelivery(CreateDeliveryApiModel createDeliveryApiModel, Map<String, String> headers);
    void updateDeliveryStatus(int deliveryId, DeliveryStatus deliveryStatus, Map<String, String> headers);
    Delivery getDeliveryDetails(int deliveryId, Map<String, String> headers);
}
