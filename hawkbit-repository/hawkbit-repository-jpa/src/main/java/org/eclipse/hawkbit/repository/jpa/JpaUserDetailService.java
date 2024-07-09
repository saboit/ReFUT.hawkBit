/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa;

import java.util.Collection;
import java.util.stream.Collectors;

import org.eclipse.hawkbit.im.authentication.UserPrincipal;
import org.eclipse.hawkbit.repository.jpa.model.JpaUser;
import org.eclipse.hawkbit.tenancy.TenantAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantAware tenantAware;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Pair<String, String> loginInfo = splitUsername(username);
        String tenant = loginInfo.getFirst();
        String userId = loginInfo.getSecond();

        final JpaUser user = tenantAware.runAsTenant(tenant, 
            () -> userRepository.findByUsername(userId))
            .orElseThrow(() -> new UsernameNotFoundException(String.format("User \"%s\" not found.", username)));
        
        final Collection<SimpleGrantedAuthority> authorities = user.getUserRole().getPrivileges().stream()
            .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        final UserPrincipal userPrincipal = new UserPrincipal(
            user.getUsername(),
            user.getPassword(),
            user.getFirstName(),
            user.getLastName(),
            user.getUsername(),
            user.getEmail(),
            user.getTenant(),
            authorities);
            
        return userPrincipal;
    }

    private Pair<String, String> splitUsername(String username) {
        int idx = username.indexOf("\\");
        if (idx < 0) {
            // assume no tenant and only username; use DEFAULT tenant
            return Pair.of("DEFAULT", username);
        }
        return Pair.of(username.substring(0, idx).toUpperCase(), username.substring(idx+1));
    }
}