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

import javax.validation.ValidationException;

import org.eclipse.hawkbit.repository.EmailService;
import org.eclipse.hawkbit.repository.PasswordResetManagement;
import org.eclipse.hawkbit.repository.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService implements PasswordResetManagement {

    @Autowired
    private EmailService emailService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    PasswordEncoder passwordEncoder;

    private final long  resetValiditySec = 10 * 60; // 10 minutes

    @Override
    public void resetRequest(User user) {
        user.setPasswordResetHash(randomCode(12));
        final ZonedDateTime expiration = ZonedDateTime.now().plusSeconds(resetValiditySec);
        user.setPaswordResetValid(Date.from(expiration.toInstant()).getTime());
    }

    private String randomCode(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String result = random.ints(leftLimit, rightLimit + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();

        return result;
    }

    @Override
    public void sendEmail(Locale locale, User user) {
        emailService.sendEmail(
            user.getEmail(),
            messageSource.getMessage("passwordResetEmail.subject", null, locale),
            messageSource.getMessage("passwordResetEmail.text", new Object[] { user.getPasswordResetHash() }, locale)
        );
    }

    @Override
    public void setNewPassword(User user, String resetHash, String password) {
        if (resetHash == null) {
            throw new ValidationException("Verification code not provided!");
        }

        if (password == null) {
            throw new ValidationException("The new password not provided!");
        }

        long now = new Date().getTime();
        if (user.getPaswordResetValid() < now) {
            throw new ValidationException("Password reset code is expired.");
        }

        if (!resetHash.equals(user.getPasswordResetHash())) {
            throw new ValidationException("Password reset hash is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordResetHash(null);
        user.setPaswordResetValid(0);
    }
}
