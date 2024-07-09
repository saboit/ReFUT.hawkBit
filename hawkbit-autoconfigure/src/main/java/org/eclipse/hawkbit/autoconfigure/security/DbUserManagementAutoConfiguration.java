/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.autoconfigure.security;

import org.eclipse.hawkbit.im.authentication.MultitenancyIndicator;
import org.eclipse.hawkbit.im.authentication.TenantAwareAuthenticationDetails;
import org.eclipse.hawkbit.im.authentication.UserPrincipal;
import org.eclipse.hawkbit.security.JwtRequestFilter;
import org.eclipse.hawkbit.security.TenantRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Auto-configuration for the database persisted user-management.
 *
 */
@Configuration
public class DbUserManagementAutoConfiguration extends GlobalAuthenticationConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    DbUserManagementAutoConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(final AuthenticationManagerBuilder auth) throws Exception {
        final DaoAuthenticationProvider userDaoAuthenticationProvider = new TenantDaoAuthenticationProvider();
        userDaoAuthenticationProvider.setUserDetailsService(userDetailsService);
        auth.authenticationProvider(userDaoAuthenticationProvider);
    }

    /**
     * @return the multi-tenancy indicator to enable multi-tenancy
     */
    @Bean
    MultitenancyIndicator multiTenancyIndicator() {
        return () -> true;
    }

    private static class TenantDaoAuthenticationProvider extends DaoAuthenticationProvider {

        @Override
        protected Authentication createSuccessAuthentication(final Object principal,
                final Authentication authentication, final UserDetails user) {
            final UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal,
                    authentication.getCredentials(), user.getAuthorities());
            UserPrincipal userPrincipal = (UserPrincipal)principal;
            result.setDetails(new TenantAwareAuthenticationDetails(userPrincipal.getTenant(), false));
            return result;
        }
    }

    /**
     * @return an authentication filter for JWT Tokens.
     */
    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter();
    }

    /**
     * @return an authentication filter for tenant with anonymous user.
     */
    @Bean
    public TenantRequestFilter tenantRequestFilter() {
        return new TenantRequestFilter();
    }
}
