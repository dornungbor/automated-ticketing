package com.callsign.ticketing.models;

import com.callsign.ticketing.domain.Role;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthenticatedUser {
    private String username;
    private String correlationId;
    private String clientIp;
    private List<Role> roles;
}
