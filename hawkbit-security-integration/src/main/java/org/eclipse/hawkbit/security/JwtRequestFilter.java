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
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_SCHEME = "Bearer";

    @Autowired
    private TokenService tokenService;

    @Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

        // If already authenticated, simply continue
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        // Get Authorization: Bearer token value from the request header
        String authToken = getAuthTokenValue(request);
        if (authToken == null) {
            chain.doFilter(request, response);
            return;
        }

        // Verify the token
        Map<String, String> tokenInfo;
        try {
            tokenInfo = tokenService.verify(authToken);
        } catch (Exception e) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, e.toString());
            return;
		}

        // Create authentication with user details
        String username = tokenInfo.get("tenant") + "\\" + tokenInfo.get("username");
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
            = new UsernamePasswordAuthenticationToken(userDetails, authToken, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        // continue processing
		chain.doFilter(request, response);
	}

    private String getAuthTokenValue(HttpServletRequest request) {
		final String requestTokenHeader = request.getHeader(AUTH_HEADER);
        if (requestTokenHeader == null || !requestTokenHeader.startsWith(AUTH_SCHEME + " ")) {
            return null;
        }

        return requestTokenHeader.substring(AUTH_SCHEME.length()).trim();
    }
}
