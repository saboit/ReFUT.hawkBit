/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.rest.resource;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUserLogin;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUserLoginBody;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUser;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUser2fa;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUserRequestBody;
import org.eclipse.hawkbit.mgmt.json.model.userrole.MgmtUserRole;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtRestConstants;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtUserRestApi;
import org.eclipse.hawkbit.repository.Email2faManagement;
import org.eclipse.hawkbit.repository.EntityFactory;
import org.eclipse.hawkbit.repository.OffsetBasedPageRequest;
import org.eclipse.hawkbit.repository.TenantConfigurationManagement;
import org.eclipse.hawkbit.repository.UserManagement;
import org.eclipse.hawkbit.repository.UserRoleManagement;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.model.User;
import org.eclipse.hawkbit.repository.model.UserRole;
import org.eclipse.hawkbit.security.SystemSecurityContext;
import org.eclipse.hawkbit.security.TokenService;
import org.eclipse.hawkbit.tenancy.configuration.TenantConfigurationProperties.TenantConfigurationKey;
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
 * REST Resource for handling {@link User} operations.
 */
@RestController
public class MgmtUserResource implements MgmtUserRestApi {

    private static final Logger LOG = LoggerFactory.getLogger(MgmtUserResource.class);

    private final UserManagement userManagement;
    private final UserRoleManagement userRoleManagement;
    private final EntityFactory entityFactory;
    private final TokenService tokenService;
    private final Email2faManagement email2faManagement;
    private final SystemSecurityContext systemSecurityContext;
    private final TenantConfigurationManagement tenantConfigurationManagement;

    MgmtUserResource(
        final UserManagement userManagement,
        final UserRoleManagement userRoleManagement,
        final EntityFactory entityFactory,
        final TokenService tokenService,
        final Email2faManagement email2faManagement,
        final SystemSecurityContext systemSecurityContext,
        final TenantConfigurationManagement tenantConfigurationManagement
    ) {
        this.userManagement = userManagement;
        this.userRoleManagement = userRoleManagement;
        this.entityFactory = entityFactory;
        this.tokenService = tokenService;
        this.email2faManagement = email2faManagement;
        this.systemSecurityContext = systemSecurityContext;
        this.tenantConfigurationManagement = tenantConfigurationManagement;
    }

    @Override
    public ResponseEntity<PagedList<MgmtUser>> getUsers(
        @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_PAGING_OFFSET, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_PAGING_DEFAULT_OFFSET) final int pagingOffsetParam,
        @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_PAGING_LIMIT, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_PAGING_DEFAULT_LIMIT) final int pagingLimitParam,
        @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_SORTING, required = false) final String sortParam) {

        final int sanitizedOffsetParam = PagingUtility.sanitizeOffsetParam(pagingOffsetParam);
        final int sanitizedLimitParam = PagingUtility.sanitizePageLimitParam(pagingLimitParam);
        final Sort sorting = PagingUtility.sanitizeUserSortParam(sortParam);

        final Pageable pageable = new OffsetBasedPageRequest(sanitizedOffsetParam, sanitizedLimitParam, sorting);
        Page<User> findUsersAll = this.userManagement.findAll(pageable);

        final List<MgmtUser> rest = MgmtUserMapper.toResponse(findUsersAll.getContent());
        return ResponseEntity.ok(new PagedList<>(rest, findUsersAll.getTotalElements()));
    }

    @Override
    public ResponseEntity<MgmtUser> getUser(@PathVariable("userId") final Long userId) {
        final User found = findUserWithExceptionIfNotFound(userId);
        final MgmtUser response = MgmtUserMapper.toResponse(found);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") final Long userId) {
        LOG.debug("Delete {} user, return status {}", userId, HttpStatus.OK);
        userManagement.delete(userId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<MgmtUser>> createUsers(
        @RequestBody final List<MgmtUserRequestBody> users) {

        final List<User> createdUsers = userManagement
            .create(MgmtUserMapper.userFromRequest(entityFactory, users));
        LOG.debug("Create {} users, return status {}", users.size(), HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.CREATED).body(MgmtUserMapper.toListResponse(createdUsers));
    }

    @Override
    public ResponseEntity<MgmtUser> updateUser(
            @PathVariable("userId") Long userId, @RequestBody final MgmtUserRequestBody restUser) {

        UserRole userRole = userRoleManagement.get(restUser.getUserRole())
            .orElseThrow(() -> new EntityNotFoundException(UserRole.class, restUser.getUserRole()));

        final User updated = userManagement.update(entityFactory.user().update(userId)
            .username(restUser.getUsername())
            .email(restUser.getEmail())
            .firstName(restUser.getFirstName())
            .lastName(restUser.getLastName())
            .userRole(userRole));

        final MgmtUser response = MgmtUserMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    private User findUserWithExceptionIfNotFound(final Long userId) {
        return userManagement.get(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, userId));
    }

    @Override
    public ResponseEntity<MgmtUserLogin> login(Principal principal, @RequestBody(required = false) final MgmtUserLoginBody restLogin) {
        final User user = userManagement.get(principal.getName())
            .orElseThrow(() -> new EntityNotFoundException(User.class, principal.getName()));

        final boolean isMfa = getMultifactor();
        if (isMfa) {
            final String givenToken = restLogin != null ? restLogin.getMfaToken() : null;
            email2faManagement.verify(givenToken, user.getLoginHash(), user.getLoginHashValid());
        }

        final MgmtUser userResponse = MgmtUserMapper.toResponse(user);
        final MgmtUserRole userRole = MgmtUserRoleMapper.toResponse(user.getUserRole());
        final String jwt = tokenService.generate(user.getTenant(), user.getUsername());
        final MgmtUserLogin userInfo = MgmtUserMapper.toUserInfoResponse(userResponse, userRole, jwt);
        return ResponseEntity.ok(userInfo);
    }

    private boolean getMultifactor()
    {
        final String mfa = systemSecurityContext.runAsSystem(() -> tenantConfigurationManagement
                    .getConfigurationValue(TenantConfigurationKey.AUTHENTICATION_LOGIN_MFA, String.class).getValue());

        return mfa.equals("email");
    }

    @Override
    public ResponseEntity<MgmtUser2fa> email2fa(Locale locale, Principal principal) {
        // get user
        final User user = userManagement.get(principal.getName())
            .orElseThrow(() -> new EntityNotFoundException(User.class, principal.getName()));

        // generate hash
        email2faManagement.generate(user);
        email2faManagement.sendEmail(locale, user);
        userManagement.update(user);

        // respond
        final MgmtUser2fa response = new MgmtUser2fa();
        response.setValidUntil(user.getLoginHashValid());
        return ResponseEntity.ok(response);
    }
}