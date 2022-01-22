package com.callsign.ticketing.models;

import com.callsign.ticketing.domain.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TokenGenerationParameters {
    private String username;
    private List<Role> roles;
}
