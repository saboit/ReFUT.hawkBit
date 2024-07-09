/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.rest.api;

import java.util.Locale;

import org.eclipse.hawkbit.mgmt.json.model.user.MgmtNewPasswordRequest;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtPasswordReset;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtPasswordResetRequest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * REST Resource for handling users.
 *
 */
@RequestMapping(MgmtRestConstants.PASSWORD_V1_REQUEST_MAPPING)
public interface MgmtPasswordRestApi {

    /**
     * Handles the POST request to reset a password
     *
     * @param locale language to be used for an email.
     * @param request json request for password reset identifing the user.
     *
     * @return response with timestamp of reset password token validity.
     */
    @PostMapping(value = "/reset", consumes = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
        MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<MgmtPasswordReset> resetRequest(Locale locale, @RequestBody MgmtPasswordResetRequest request);

    /**
     * Handles the POST request to set a new password
     *
     * @param password to be created.
     *
     * @return Empty response
     */
    @PostMapping(value = "/newpassword", consumes = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
        MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<Void> setPassword(@RequestBody MgmtNewPasswordRequest request);
}
