/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.rest.api;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUserLogin;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUserLoginBody;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUser;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUser2fa;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUserRequestBody;
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
 * REST Resource for handling users.
 *
 */
@RequestMapping(MgmtRestConstants.USER_V1_REQUEST_MAPPING)
public interface MgmtUserRestApi {

    /**
     * Handles the GET request for receiving all user rs
     *
     * @param pagingOffsetParam
     *            the offset of list of users for pagination, might not be
     *            present in the rest request then default value will be applied
     * @param pagingLimitParam
     *            the limit of the paged request, might not be present in the
     *            rest request then default value will be applied
     * @param sortParam
     *            the sorting parameter in the request URL, syntax
     *            {@code field:direction, field:direction}
     * @return a list of all users for a defined or default page request
     *         with status OK. The response is always paged. In any failure the
     *         JsonResponseExceptionHandler is handling the response.
     */
    @GetMapping(produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<PagedList<MgmtUser>> getUsers(
        @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_PAGING_OFFSET, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_PAGING_DEFAULT_OFFSET) int pagingOffsetParam,
        @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_PAGING_LIMIT, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_PAGING_DEFAULT_LIMIT) int pagingLimitParam,
        @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_SORTING, required = false) String sortParam);

    /**
     * Handles the GET request of retrieving a single {@link UserRole}.
     *
     * @param userId the ID of the user to retrieve
     *
     * @return a single user with status OK.
     */
    @GetMapping(value = "/{userId}", produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<MgmtUser> getUser(@PathVariable("userId") Long userId);

    /**
     * Handles the DELETE request of deleting a {@link User}.
     *
     * @param UserId the Name of the user
     *
     * @return if the given user exists and could be deleted HTTP
     *         OK. In any failure the JsonResponseExceptionHandler is handling
     *         the response.
     */
    @DeleteMapping(value = "/{userId}", produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId);

    /**
     * Handles the POST request of creating a new {@link User}. The request body must
     * always be a list of types.
     *
     * @param users to be created.
     *
     * @return In case all users could be successfully created the
     *         ResponseEntity with status code 201 - Created but without
     *         ResponseBody. In any failure the JsonResponseExceptionHandler is
     *         handling the response.
     */
    @PostMapping(consumes = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
        MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<List<MgmtUser>> createUsers(List<MgmtUserRequestBody> users);


    /**
     * Handles the PUT request for updating a {@link} User}
     *
     * @param userId the id  of the user
     * @param restUser the new values for the user
     * @return if the given user exists and could be get, returns HTTP OK.
     *         In any failure the JsonResponseExceptionHandler is handling the
     *         response.
     */
    @PutMapping(value = "/{userId}", consumes = { MediaTypes.HAL_JSON_VALUE,
            MediaType.APPLICATION_JSON_VALUE }, produces = { MediaTypes.HAL_JSON_VALUE,
                    MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<MgmtUser> updateUser(
            @PathVariable("userId") Long userId, MgmtUserRequestBody restUser);

    /**
     * Handles the POST request of loging a {@link User}. The request body is empty.
     *
     * @param user to be logged in.
     *
     * @return In case all users could be successfully created the
     *         ResponseEntity with status code 201 - Created but without
     *         ResponseBody. In any failure the JsonResponseExceptionHandler is
     *         handling the response.
     */
    @PostMapping(value = "/login",
        produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<MgmtUserLogin> login(Principal userPrincipal, MgmtUserLoginBody restLogin);

    /**
     * Handles the POST request of sending a 2nd authentication factor via email.
     * Request body is empty.
     *
     * @param user to be logged in.
     *
     * @return In case all users could be successfully created the
     *         ResponseEntity with status code 201 - Created but without
     *         ResponseBody. In any failure the JsonResponseExceptionHandler is
     *         handling the response.
     */
    @PostMapping(value = "/email2fa",
        produces = { MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    ResponseEntity<MgmtUser2fa> email2fa(Locale locale, Principal userPrincipal);
}
