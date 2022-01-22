
package com.callsign.ticketing.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
// TODO this could be written to DB or outsourced to an identity provider altogether
public class UserIdentity {

    private long id;
    private String username;
    private String hashedPassword;
    private String email;
    private String firstname;
    private String lastname;
    private List<Role> roles;
}
