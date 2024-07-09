/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

import java.util.Locale;

import org.eclipse.hawkbit.repository.model.User;

/**
 * Management service for generating and sending 2nd authentication factor.
 *
 */
public interface Email2faManagement {

    /**
     * Generate 2nd factor
     * @param user
     */
    void generate(User user);

    /**
     * Send email with 2nd factor info
     * @param user
     */
    void sendEmail(Locale locale, User user);

    /**
     * Verify the given token with expected value and check expiration
     * @param givenToken The token user has provided
     * @param token Token stored in the database
     * @param expiration Expiration time of the token
     */
    void verify(String givenToken, String token, long expiration);
}