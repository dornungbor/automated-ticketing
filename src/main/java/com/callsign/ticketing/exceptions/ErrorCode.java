package com.callsign.ticketing.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_MESSAGE_ERROR("The message format read from the given topic is invalid"),
    INVALID_DELIVERY_DATE_ERROR("Delivery date must be a valid future date"),
    INVALID_EXPECTED_DATE_ERROR("Expected time to reach destination must be a valid future date"),
    VALIDATION_ERROR("The request has validation errors"),
    REQUEST_NOT_FOUND("The requested resource was NOT found"),
    GENERIC_ERROR("Generic error occurred. See stacktrace for details"),
    AUTHORIZATION_ERROR("You do NOT have adequate permission to access this resource"),
    INVALID_CREDENTIALS_ERROR("The credentials you provided are invalid"),
    INVALID_GRANT_ERROR("grant_type must be password"),
    TOKEN_GENERATION_ERROR("Unable to generate access token. Please ensure that the RSA keys are valid"),
    DUPLICATE_ENTRY_ERROR("Duplicate entry detected."),
    NO_PRINCIPAL("Principal identifier NOT provided", 500);

    private final String defaultMessage;
    private final int defaultHttpStatus;

    ErrorCode(String defaultMessage) {
        this(defaultMessage, 400);
    }

    ErrorCode(String defaultMessage, int defaultHttpStatus) {
        this.defaultMessage = defaultMessage;
        this.defaultHttpStatus = defaultHttpStatus;
    }
}
