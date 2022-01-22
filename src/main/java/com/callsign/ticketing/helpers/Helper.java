package com.callsign.ticketing.helpers;

import com.callsign.ticketing.domain.Role;
import com.callsign.ticketing.models.AuthenticatedUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Helper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String writeAsStringOrDefault(ObjectMapper mapper, T source) {
        try {
            return mapper.writeValueAsString(source);
        } catch (JsonProcessingException exception) {
            log.error("An error occurred while writing source parameter as string", exception);
            return "";
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON deserialization error");
        }
    }

    public static AuthenticatedUser fromHeaders(Map<String, String> headers) {
        String clientIp = "";
        String xForwardedFor = headers.get("x-forwarded-for");
        if (StringUtils.hasText(xForwardedFor)) {
            clientIp = xForwardedFor.split(",")[0].trim();
        }
        List<Role> roles = new ArrayList<>();
        String rolesHeader = headers.get("user-roles");
        if (StringUtils.hasText(rolesHeader)) {
            String[] rolesArr = rolesHeader.split(",");
            roles = Arrays.stream(rolesArr).map(x -> {
                try {
                    return Role.valueOf(x.trim());
                } catch (Exception exception) {
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return AuthenticatedUser.builder()
                .username(headers.get("username"))
                .correlationId(headers.get("correlation-id"))
                .clientIp(clientIp)
                .roles(roles)
                .build();
    }

    public static void addToMappedDiagnosticContext(String name, String value) {
        if (StringUtils.hasText(value)) {
            MDC.put(name, value);
        }
    }

    public static void addToMappedDiagnosticContextOrRandomUUID(String name, String value) {
        if (StringUtils.hasText(value)) {
            MDC.put(name, value);
        } else {
            MDC.put(name, UUID.randomUUID().toString());
        }
    }
}
