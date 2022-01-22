package com.callsign.ticketing.domain.commands;

import com.callsign.ticketing.domain.Delivery;
import com.callsign.ticketing.exceptions.CommandException;
import com.callsign.ticketing.exceptions.ErrorObject;
import com.callsign.ticketing.models.GetDeliveryDetailsCommandRequest;
import com.callsign.ticketing.ports.outbound.PersistencePort;
import com.callsign.ticketing.validation.DefaultConstraintValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.callsign.ticketing.exceptions.ErrorCode.REQUEST_NOT_FOUND;

@Slf4j
@Service
public class GetDeliveryDetailsCommand extends AbstractCommand<GetDeliveryDetailsCommandRequest, Delivery, Delivery> {

    private final DefaultConstraintValidator validator;
    private final PersistencePort persistencePort;

    public GetDeliveryDetailsCommand(
            ObjectMapper mapper,
            DefaultConstraintValidator validator,
            PersistencePort persistencePort) {
        super("GetDeliveryDetailsCommand", mapper);
        this.validator = validator;
        this.persistencePort = persistencePort;
    }

    @Override
    protected Delivery handle(GetDeliveryDetailsCommandRequest request) {
        return this.persistencePort.findDeliveryByDeliveryId(request.getDeliveryId())
                .orElseThrow(() -> CommandException.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .errorCode(REQUEST_NOT_FOUND)
                        .message(REQUEST_NOT_FOUND.getDefaultMessage())
                        .build());
    }

    @Override
    protected List<ErrorObject> validate(GetDeliveryDetailsCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected Delivery apiResponse(Delivery handlerResponse) {
        return handlerResponse;
    }

}
