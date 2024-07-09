/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.mgmt.rest.resource;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.hawkbit.mgmt.json.model.userrole.MgmtUserRole;
import org.eclipse.hawkbit.mgmt.json.model.userrole.MgmtUserRoleRequestBody;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtUserRoleRestApi;
import org.eclipse.hawkbit.repository.EntityFactory;
import org.eclipse.hawkbit.repository.builder.UserRoleCreate;
import org.eclipse.hawkbit.repository.model.UserRole;
import org.eclipse.hawkbit.rest.data.ResponseList;

/**
 * A mapper which maps repository model to RESTful model representation and
 * back.
 *
 */
final class MgmtUserRoleMapper {
    private MgmtUserRoleMapper() {
        // Utility class
    }

    static List<MgmtUserRole> toResponse(final List<UserRole> userRoles) {
        final List<MgmtUserRole> userRolesRest = new ArrayList<>();
        if (userRoles == null) {
            return userRolesRest;
        }

        for (final UserRole userRole : userRoles) {
            final MgmtUserRole response = toResponse(userRole);

            userRolesRest.add(response);
        }
        return new ResponseList<>(userRolesRest);
    }

    static MgmtUserRole toResponse(final UserRole userRole) {
        final MgmtUserRole response = new MgmtUserRole();
        if (userRole == null) {
            return response;
        }

        mapUserRole(response, userRole);

        response.add(linkTo(methodOn(MgmtUserRoleRestApi.class).getUserRole(userRole.getId())).withSelfRel());

        return response;
    }

    private static void mapUserRole(final MgmtUserRole response, final UserRole userRole) {
        MgmtRestModelMapper.mapNamedToNamed(response, userRole);
        response.setRoleId(userRole.getId());
        response.setPrivileges(userRole.getPrivileges());
    }

    static List<MgmtUserRole> toListResponse(final List<UserRole> types) {
        if (types == null) {
            return Collections.emptyList();
        }
        return new ResponseList<>(types.stream().map(MgmtUserRoleMapper::toResponse).collect(Collectors.toList()));
    }

    static List<UserRoleCreate> userRoleFromRequest(final EntityFactory entityFactory,
        final Collection<MgmtUserRoleRequestBody> userRoleRest) {
        if (userRoleRest == null) {
            return Collections.emptyList();
        }
        return userRoleRest.stream().map(aUserRoleRest -> fromRequest(entityFactory, aUserRoleRest)).collect(Collectors.toList());
    }

    private static UserRoleCreate fromRequest(final EntityFactory entityFactory,
        final MgmtUserRoleRequestBody userRoleRest) {
        return entityFactory.userRole().create()
            .name(userRoleRest.getName())
            .description(userRoleRest.getDescription())
            .privileges(userRoleRest.getPrivileges());
    }
}