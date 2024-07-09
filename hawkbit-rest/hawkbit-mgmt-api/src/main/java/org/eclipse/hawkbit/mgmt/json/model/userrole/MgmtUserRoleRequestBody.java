/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.json.model.userrole;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for {@link UserRole} PUT/POST commands.
 *
 */
public class MgmtUserRoleRequestBody {

    @JsonProperty
    private String id;

    @JsonProperty(required = true)
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private List<String> privileges;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPrivileges() {
        return this.privileges;
    }

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }
}
