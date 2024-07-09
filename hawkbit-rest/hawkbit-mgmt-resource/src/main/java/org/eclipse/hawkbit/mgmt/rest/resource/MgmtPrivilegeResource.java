/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.rest.resource;

import org.eclipse.hawkbit.mgmt.json.model.privilege.MgmtPrivilege;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtPrivilegeRestApi;
import org.eclipse.hawkbit.repository.PrivilegeManagement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Resource handling for get all permissions.
 */
@RestController
public class MgmtPrivilegeResource implements MgmtPrivilegeRestApi {
    private PrivilegeManagement privilegeManagement;

    MgmtPrivilegeResource(final PrivilegeManagement privilegeManagement) {
        this.privilegeManagement = privilegeManagement;
    }

    @Override
    public ResponseEntity<MgmtPrivilege> getAllPrivileges() {
        final MgmtPrivilege response = new MgmtPrivilege();
        response.setPrivileges(privilegeManagement.findAllPermissions());
        return ResponseEntity.ok(response);
    }
}
