/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.rest.resource;

import java.util.Locale;

import javax.validation.ValidationException;

import org.eclipse.hawkbit.mgmt.json.model.user.MgmtNewPasswordRequest;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtPasswordReset;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtPasswordResetRequest;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtPasswordRestApi;
import org.eclipse.hawkbit.repository.PasswordResetManagement;
import org.eclipse.hawkbit.repository.UserManagement;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Resource for handling password reset operations.
 */
@RestController
public class MgmtPasswordResource implements MgmtPasswordRestApi {

    @Autowired
    UserManagement userManagement;

    @Autowired
    PasswordResetManagement passwordResetManagement;

    @Override
    public ResponseEntity<MgmtPasswordReset> resetRequest(Locale locale, MgmtPasswordResetRequest request) {
        // get user
        final User user = userManagement.get(request.getUsername(), request.getEmail())
            .orElseThrow(() -> new EntityNotFoundException(User.class, request.getUsername() + " " + request.getEmail()));

        // generate hash
        passwordResetManagement.resetRequest(user);
        passwordResetManagement.sendEmail(locale, user);
        userManagement.update(user);

        // respond
        final MgmtPasswordReset response = new MgmtPasswordReset();
        response.setValidUntil(user.getPaswordResetValid());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> setPassword(MgmtNewPasswordRequest request) {
        // get user
        final User user = userManagement.getByPasswordResetHash(request.getResetHash())
            .orElseThrow(() -> new ValidationException("Invalid password reset hash"));

        // set password
        passwordResetManagement.setNewPassword(user, request.getResetHash(), request.getPassword());
        userManagement.update(user);

        return ResponseEntity.noContent().build();
    }
}