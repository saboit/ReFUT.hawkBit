/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

/**
 * Management service for sending emails.
 *
 */
public interface EmailService {

    /**
     * Send an email
     * @param to - email recipient
     * @param subject - email subject
     * @param text - email message body
     */
    void sendEmail(String to, String subject, String text);
}