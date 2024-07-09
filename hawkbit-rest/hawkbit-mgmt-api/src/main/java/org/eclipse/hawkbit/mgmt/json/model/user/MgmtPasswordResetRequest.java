/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.json.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A json annotated rest model for Password reset request body.
 *
 */
public class MgmtPasswordResetRequest {

    @JsonProperty(required = true)
    private String username;

    @JsonProperty(required = true)
    private String email;

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

}
