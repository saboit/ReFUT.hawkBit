/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.security;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.hawkbit.im.authentication.TenantAwareAuthenticationDetails;
import org.eclipse.hawkbit.im.authentication.UserPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class TenantRequestFilter extends OncePerRequestFilter {
    private static final String TENANT_HEADER = "X-Tenant";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

        // If already authenticated, simply continue
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        // Get tenant from headers
        String tenant = getTenantTokenValue(request);
        if (tenant == null) {
            chain.doFilter(request, response);
            return;
        }

        // Create authentication with anonymous details
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        UserPrincipal principal = new UserPrincipal("anonymous", "***", null, null, null, null, tenant, authorities);
        AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken("anonymous", principal, authorities);
        anonymousAuthenticationToken.setDetails(new TenantAwareAuthenticationDetails(tenant, false));
        SecurityContextHolder.getContext().setAuthentication(anonymousAuthenticationToken);

        // continue processing
		chain.doFilter(request, response);
	}

    private String getTenantTokenValue(HttpServletRequest request) {
		final String tenant = request.getHeader(TENANT_HEADER);
        return tenant == null ? null : tenant.trim();
    }
}
