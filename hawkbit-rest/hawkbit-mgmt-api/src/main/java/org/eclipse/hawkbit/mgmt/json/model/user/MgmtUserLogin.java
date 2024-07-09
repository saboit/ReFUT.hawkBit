/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.json.model.user;

import org.eclipse.hawkbit.mgmt.json.model.userrole.MgmtUserRole;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A json annotated rest model for UserLogin to RESTful API representation.
 *
 */
public class MgmtUserLogin {

    @JsonProperty
    private String jwt;            

    @JsonProperty
    private MgmtUser user;       
    
    @JsonProperty
    private MgmtUserRole userRole;       

    public String getJwt() {
        return this.jwt;
    }
    
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public MgmtUser getUser() {
        return this.user;
    }

    public void setUser(MgmtUser user) {
        this.user = user;
    }

    public MgmtUserRole getUserRole() {
        return this.userRole;
    }

    public void setUserRole(MgmtUserRole userRole) {
        this.userRole = userRole;
    }    
}
    