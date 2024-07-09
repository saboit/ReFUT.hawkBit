/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.rest.api;

import java.util.List;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.userrole.MgmtUserRole;
import org.eclipse.hawkbit.mgmt.json.model.userrole.MgmtUserRoleRequestBody;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * REST Resource for handling user roles.
 *
 */
@RequestMapping(MgmtRestConstants.USER_ROLE_V1_REQUEST_MAPPING)
public interface MgmtUserRoleRestApi {

    /**
     * Handles the GET request for receiving all user roles
     *
     * @param pagingOffsetParam
     *            the offset of list of user roles for pagination, might not be
     *            present in the rest request then default value will be applied
     * @param pagingLimitParam
     *            the limit of the paged request, might not be present in the
     *            rest request then default value will be applied
     * @param sortParam
     *            the sorting parameter in the request URL, syntax
     *            {@code field:direction, field:direction}
     * @return a list of all user roles for a defined or default page request
     *         with status OK. The response is always paged. In any failure the
     *         JsonResponseExceptionHandler is handling the response.
     */
    @GetMapping(produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<PagedList<MgmtUserRole>> getUserRoles(
        @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_PAGING_OFFSET, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_PAGING_DEFAULT_OFFSET) int pagingOffsetParam,
        @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_PAGING_LIMIT, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_PAGING_DEFAULT_LIMIT) int pagingLimitParam,
        @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_SORTING, required = false) String sortParam);

    /**
     * Handles the GET request of retrieving a single {@link UserRole}.
     *
     * @param userRoleId the ID of the user role to retrieve
     *
     * @return a single user role with status OK.
     */
    @GetMapping(value = "/{userRoleId}", produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<MgmtUserRole> getUserRole(@PathVariable("userRoleId") Long userRoleId);

    /**
     * Handles the DELETE request of deleting a {@link UserRole}.
     *
     * @param userRoleId the Name of the user role
     * 
     * @return if the given user role exists and could be deleted HTTP
     *         OK. In any failure the JsonResponseExceptionHandler is handling
     *         the response.
     */
    @DeleteMapping(value = "/{userRoleId}", produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<Void> deleteUserRole(@PathVariable("userRoleId") Long userRoleId);

    /**
     * Handles the POST request of creating a new {@link UserRole}. The request body must
     * always be a list of types.
     *
     * @param userRoles the user roles to be created.
     * 
     * @return In case all user roles could be successfully created the
     *         ResponseEntity with status code 201 - Created but without
     *         ResponseBody. In any failure the JsonResponseExceptionHandler is
     *         handling the response.
     */
    @PostMapping(consumes = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
        MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })

        ResponseEntity<List<MgmtUserRole>> createUserRoles(List<MgmtUserRoleRequestBody> userRoles);


    /**
     * Handles the PUT request for updating a {@link} UserRole}
     *
     * @param userRoleId the id  of the user role
     * @param restUserRole the new values for the user role
     * @return if the given configuration value exists and could be get HTTP OK.
     *         In any failure the JsonResponseExceptionHandler is handling the
     *         response.
     */
    @PutMapping(value = "/{userRoleId}", consumes = { MediaTypes.HAL_JSON_VALUE,
            MediaType.APPLICATION_JSON_VALUE }, produces = { MediaTypes.HAL_JSON_VALUE,
                    MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<MgmtUserRole> updateUserRole(
            @PathVariable("userRoleId") Long userRoleId, MgmtUserRoleRequestBody restUserRole);

}
