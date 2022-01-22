package com.callsign.ticketing.domain.commands;

import com.callsign.ticketing.domain.Delivery;

public interface DeliverySupplierCommand {
    Delivery getDelivery();
}
