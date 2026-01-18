package com.samjay.rehire.security;

import com.samjay.rehire.model.Organization;
import com.samjay.rehire.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private final OrganizationRepository organizationRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String email = authentication.getName();

        String password = authentication.getCredentials().toString();

        Organization organization = organizationRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Organization with email " + email + " not found"));

        if (passwordEncoder.matches(password, organization.getPassword())) {

            List<GrantedAuthority> authorities = new ArrayList<>();

            authorities.add(new SimpleGrantedAuthority(organization.getRole().getRoleName()));

            return new UsernamePasswordAuthenticationToken(email, password, authorities);

        } else {

            throw new BadCredentialsException("Invalid Password");

        }
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));

    }
}