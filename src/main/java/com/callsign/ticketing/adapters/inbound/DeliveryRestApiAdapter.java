package com.callsign.ticketing.adapters.inbound;

import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.domain.DeliveryStatus;
import com.callsign.ticketing.domain.commands.CreateDeliveryCommand;
import com.callsign.ticketing.domain.commands.GetDeliveryDetailsCommand;
import com.callsign.ticketing.domain.commands.UpdateDeliveryStatusCommand;
import com.callsign.ticketing.helpers.Helper;
import com.callsign.ticketing.models.*;
import com.callsign.ticketing.ports.inbound.DeliveryRestApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryRestApiAdapter implements DeliveryRestApiPort {

    private final CreateDeliveryCommand createDeliveryCommand;
    private final UpdateDeliveryStatusCommand updateDeliveryStatusCommand;
    private final GetDeliveryDetailsCommand getDeliveryDetailsCommand;

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public CreateDeliveryCommandResponse createDelivery(
            @RequestBody CreateDeliveryApiModel createDeliveryApiModel,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = Helper.fromHeaders(headers);
        CreateDeliveryCommandRequest createDeliveryCommandRequest = CreateDeliveryCommandRequest.builder()
                .createDeliveryApiModel(createDeliveryApiModel)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.createDeliveryCommand.execute(createDeliveryCommandRequest);
    }

    @PutMapping("/{deliveryId}/status/{status}")
    @Override
    public void updateDeliveryStatus(
            @PathVariable int deliveryId,
            @PathVariable DeliveryStatus status,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = Helper.fromHeaders(headers);
        UpdateDeliveryStatusCommandRequest updateDeliveryStatusCommandRequest = UpdateDeliveryStatusCommandRequest.builder()
                .deliveryId(deliveryId)
                .deliveryStatus(status)
                .authenticatedUser(authenticatedUser)
                .build();
        this.updateDeliveryStatusCommand.execute(updateDeliveryStatusCommandRequest);
    }

    @GetMapping("/{deliveryId}")
    @ResponseBody
    @Override
    public Delivery getDeliveryDetails(
            @PathVariable int deliveryId,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = Helper.fromHeaders(headers);
        GetDeliveryDetailsCommandRequest getDeliveryDetailsCommandRequest = GetDeliveryDetailsCommandRequest.builder()
                .deliveryId(deliveryId)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getDeliveryDetailsCommand.execute(getDeliveryDetailsCommandRequest);
    }
}
