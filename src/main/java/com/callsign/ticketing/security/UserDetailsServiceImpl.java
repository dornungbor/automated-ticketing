package com.callsign.ticketing.security;

import com.callsign.ticketing.domain.UserIdentity;
import com.callsign.ticketing.ports.outbound.PersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PersistencePort persistencePort;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserIdentity user = persistencePort.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));
        return UserDetailsImpl.build(user);
    }
}
