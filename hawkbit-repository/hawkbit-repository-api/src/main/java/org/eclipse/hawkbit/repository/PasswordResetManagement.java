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
public interface PasswordResetManagement {

    /**
     * Generate verification code with short-term validity for the user
     * @param user to reset the password to
     */
    void resetRequest(User user);

    /**
     * Send email with 2nd factor info
     * @param locale language to be used for email
     * @param user to send the verification code to
     */
    void sendEmail(Locale locale, User user);

    /**
     * Set new password using verification code sent by email
     * @param user to change the password to
     * @param resetHash verification code sent by email
     * @param password the new password to be set
     */
    void setNewPassword(User user, String resetHash, String password);
}
