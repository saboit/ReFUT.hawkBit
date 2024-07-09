/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.json.model.privilege;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A json annotated rest model for Privileges to RESTful API representation.
 *
 */
public class MgmtPrivilege {

    @JsonProperty()
    private List<String> privileges;

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    public List<String> getPrivileges() {
        return privileges;
    }
}
