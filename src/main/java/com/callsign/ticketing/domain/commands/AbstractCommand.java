package com.callsign.ticketing.domain.commands;

import com.callsign.ticketing.exceptions.CommandException;
import com.callsign.ticketing.exceptions.ErrorObject;
import com.callsign.ticketing.exceptions.ValidationException;
import com.callsign.ticketing.helpers.Helper;
import com.callsign.ticketing.models.AccessControl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Executors;

import static com.callsign.ticketing.exceptions.ErrorCode.GENERIC_ERROR;
import static com.callsign.ticketing.exceptions.ErrorCode.VALIDATION_ERROR;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractCommand<T extends AccessControl, R, Z> {

    protected final String name;
    protected final ObjectMapper mapper;

    public Z execute(T request) {
        try {
            log.info("received {} request", this.name);

            log.info("validating {} request...", this.name);
            List<ErrorObject> validationErrors = this.validate(request);

            if (!CollectionUtils.isEmpty(validationErrors)) {
                log.info("{} request validation failed!", this.name);
                log.info("validation errors: {}", Helper.writeAsStringOrDefault(mapper, validationErrors));
                ValidationException exception = new ValidationException(validationErrors);
                throw CommandException.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .errorCode(VALIDATION_ERROR)
                        .cause(exception)
                        .message(String.format("Validation failed for %s request", this.name))
                        .build();
            }

            R response = this.handle(request);
            log.info("{} request successfully processed", this.name);

            if (this instanceof UpdatesPublisherCommand && response instanceof DeliverySupplierCommand) {
                log.info("publishing {} update to REDIS", this.name);
                Executors.newSingleThreadExecutor().submit(() -> {
                    ((UpdatesPublisherCommand)this).publishUpdates((DeliverySupplierCommand)response);
                });
            }

            log.info("returning {} response", this.name);
            return this.apiResponse(response);
        } catch (Exception exception) {
            log.error("An error occurred while processing {} request", this.name, exception);
            if (exception instanceof CommandException) {
                throw ((CommandException) exception);
            } else {
                throw CommandException.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .errorCode(GENERIC_ERROR)
                        .cause(exception)
                        .message(String.format("An error occurred while processing %s request", this.name))
                        .build();
            }
        }
    }

    protected abstract R handle(T request);

    protected abstract List<ErrorObject> validate(T request);

    protected abstract Z apiResponse(R handlerResponse);
}
