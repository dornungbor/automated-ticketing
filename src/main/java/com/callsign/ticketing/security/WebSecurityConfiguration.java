package com.callsign.ticketing.security;

import com.callsign.ticketing.domain.Role;
import com.callsign.ticketing.helpers.JwtHelper;
import com.callsign.ticketing.util.AccessTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationEntryPointImpl unauthorizedHandler;
    private final UserDetailsServiceImpl userDetailsService;
    private final AccessTokenUtil accessTokenUtil;
    private final JwtHelper jwtHelper;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(
            AccessTokenUtil accessTokenUtil,
            JwtHelper jwtHelper,
            UserDetailsServiceImpl userDetailsService
    ) {
        return new AuthTokenFilter(accessTokenUtil, jwtHelper, userDetailsService);
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/identity/auth/login").permitAll()
                .antMatchers("/deliveries/**").hasAnyAuthority(Role.ROLE_DELIVERY_MANAGER.name(), Role.ROLE_ADMIN.name())
                .antMatchers("/tickets/**").hasAnyAuthority(Role.ROLE_TICKET_MANAGER.name(), Role.ROLE_ADMIN.name())
                .anyRequest().authenticated();

        http.addFilterBefore(
                authenticationJwtTokenFilter(accessTokenUtil, jwtHelper, userDetailsService),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}
