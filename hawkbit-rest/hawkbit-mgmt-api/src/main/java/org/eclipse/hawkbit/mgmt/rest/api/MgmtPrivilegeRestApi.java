/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.rest.api;

import org.eclipse.hawkbit.mgmt.json.model.privilege.MgmtPrivilege;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * REST Resource handling for Privileges get operation.
 *
 */
@RequestMapping(MgmtRestConstants.PRIVILEGE_V1_REQUEST_MAPPING)
public interface MgmtPrivilegeRestApi {

    /**
     * Handles the GET request of retrieving all privileges.
     *
     * @return a list of all privileges available. No paging needed.
     */
    @GetMapping(produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<MgmtPrivilege> getAllPrivileges();

}
