
package com.callsign.ticketing.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserLoginApiModel {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @NotEmpty
    @JsonProperty("grant_type")
    private String grantType;
}
