/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.rest.resource;

import java.util.List;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.userrole.MgmtUserRole;
import org.eclipse.hawkbit.mgmt.json.model.userrole.MgmtUserRoleRequestBody;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtRestConstants;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtUserRoleRestApi;
import org.eclipse.hawkbit.repository.EntityFactory;
import org.eclipse.hawkbit.repository.OffsetBasedPageRequest;
import org.eclipse.hawkbit.repository.UserRoleManagement;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.model.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Resource for handling {@link UserRole} operations.
 */
@RestController
public class MgmtUserRoleResource implements MgmtUserRoleRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(MgmtUserRoleResource.class);

    private final UserRoleManagement userRoleManagement;
    private final EntityFactory entityFactory;

    MgmtUserRoleResource(final UserRoleManagement userRoleManagement, EntityFactory entityFactory) {
        this.userRoleManagement = userRoleManagement;
        this.entityFactory = entityFactory;
    }

    @Override
    public ResponseEntity<PagedList<MgmtUserRole>> getUserRoles(
        @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_PAGING_OFFSET, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_PAGING_DEFAULT_OFFSET) final int pagingOffsetParam,
        @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_PAGING_LIMIT, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_PAGING_DEFAULT_LIMIT) final int pagingLimitParam,
        @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_SORTING, required = false) final String sortParam) {

        final int sanitizedOffsetParam = PagingUtility.sanitizeOffsetParam(pagingOffsetParam);
        final int sanitizedLimitParam = PagingUtility.sanitizePageLimitParam(pagingLimitParam);
        final Sort sorting = PagingUtility.sanitizeUserRoleSortParam(sortParam);

        final Pageable pageable = new OffsetBasedPageRequest(sanitizedOffsetParam, sanitizedLimitParam, sorting);
        Page<UserRole> findUserRolesAll = this.userRoleManagement.findAll(pageable);

        final List<MgmtUserRole> rest = MgmtUserRoleMapper.toResponse(findUserRolesAll.getContent());
        return ResponseEntity.ok(new PagedList<>(rest, findUserRolesAll.getTotalElements()));
    }

    @Override
    public ResponseEntity<MgmtUserRole> getUserRole(@PathVariable("userRoleId") final Long userRoleId) {
        final UserRole found = findUserRoleWithExceptionIfNotFound(userRoleId);
        final MgmtUserRole response = MgmtUserRoleMapper.toResponse(found);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteUserRole(@PathVariable("userRoleId") final Long userRoleId) {
        LOG.debug("Delete {} user role, return status {}", userRoleId, HttpStatus.OK);
        userRoleManagement.delete(userRoleId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<MgmtUserRole>> createUserRoles(
        @RequestBody final List<MgmtUserRoleRequestBody> userRoles) {
            
        final List<UserRole> createdUserRoles = userRoleManagement
            .create(MgmtUserRoleMapper.userRoleFromRequest(entityFactory, userRoles));
        LOG.debug("Create {} user roles, return status {}", userRoles.size(), HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.CREATED).body(MgmtUserRoleMapper.toListResponse(createdUserRoles));
    }

    @Override
    public ResponseEntity<MgmtUserRole> updateUserRole(
            @PathVariable("userRoleId") Long userRoleId, @RequestBody final MgmtUserRoleRequestBody restUserRole) {

        final UserRole updated = userRoleManagement.update(entityFactory.userRole().update(userRoleId)
            .name(restUserRole.getName())
            .description(restUserRole.getDescription())
            .privileges(restUserRole.getPrivileges()));
        final MgmtUserRole response = MgmtUserRoleMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }
        
    private UserRole findUserRoleWithExceptionIfNotFound(final Long userRoleId) {
        return userRoleManagement.get(userRoleId)
                .orElseThrow(() -> new EntityNotFoundException(UserRole.class, userRoleId));
    }
}