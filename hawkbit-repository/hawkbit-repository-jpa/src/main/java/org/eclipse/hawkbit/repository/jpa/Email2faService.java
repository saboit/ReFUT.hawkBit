/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.eclipse.hawkbit.repository.Email2faManagement;
import org.eclipse.hawkbit.repository.EmailService;
import org.eclipse.hawkbit.repository.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class Email2faService implements Email2faManagement {

    @Autowired
    private EmailService emailService;

    @Autowired
    MessageSource messageSource;

    private final long  hashValiditySec = 10 * 60; // 10 minutes

    @Override
    public void generate(User user) {
        user.setLoginHash(randomCode());
        final ZonedDateTime expiration = ZonedDateTime.now().plusSeconds(hashValiditySec);
        user.setLoginHashValid(Date.from(expiration.toInstant()).getTime());
    }

    private String randomCode() {
        int min = 100000;
        int max = 999999;
        Random random = new Random();
        int code = random.nextInt(max - min) + min;
        return String.valueOf(code);
    }

    @Override
    public void sendEmail(Locale locale, User user) {
        emailService.sendEmail(
            user.getEmail(),
            messageSource.getMessage("loginEmail.subject", null, locale),
            messageSource.getMessage("loginEmail.text", new Object[] { user.getLoginHash() }, locale)
        );
    }

    @Override
    public void verify(String givenToken, String token, long expiration) {
        if (givenToken == null) {
            throw new BadCredentialsException("MFA is enabled but no MFA token provided.");
        }

        long now = new Date().getTime();
        if (expiration < now) {
            throw new BadCredentialsException("MFA token is expired.");
        }

        if (!givenToken.equals(token)) {
            throw new BadCredentialsException("MFA token is incorrect.");
        }

    }
}
