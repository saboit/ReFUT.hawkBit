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

import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUser;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUserLogin;
import org.eclipse.hawkbit.mgmt.json.model.user.MgmtUserRequestBody;
import org.eclipse.hawkbit.mgmt.json.model.userrole.MgmtUserRole;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtUserRestApi;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtUserRoleRestApi;
import org.eclipse.hawkbit.repository.EntityFactory;
import org.eclipse.hawkbit.repository.builder.UserCreate;
import org.eclipse.hawkbit.repository.model.User;
import org.eclipse.hawkbit.rest.data.ResponseList;

/**
 * A mapper which maps repository model to RESTful model representation and
 * back.
 *
 */
final class MgmtUserMapper {

    private MgmtUserMapper() {
        // Utility class
    }

    static List<MgmtUser> toResponse(final List<User> users) {
        final List<MgmtUser> userRest = new ArrayList<>();
        if (users == null) {
            return userRest;
        }

        for (final User user : users) {
            final MgmtUser response = toResponse(user);

            userRest.add(response);
        }
        return new ResponseList<>(userRest);
    }

    static MgmtUser toResponse(final User user) {
        final MgmtUser response = new MgmtUser();
        if (user == null) {
            return response;
        }

        mapUser(response, user);

        response.add(linkTo(methodOn(MgmtUserRestApi.class).getUser(user.getId())).withSelfRel());
        response.add(linkTo(methodOn(MgmtUserRoleRestApi.class).getUserRole(user.getUserRole().getId())).withRel("userrole"));

        return response;
    }

    private static void mapUser(final MgmtUser response, final User user) {
        MgmtRestModelMapper.mapBaseToBase(response, user);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setUserRole(user.getUserRole().getName());
    }

    static List<MgmtUser> toListResponse(final List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return new ResponseList<>(users.stream().map(MgmtUserMapper::toResponse).collect(Collectors.toList()));
    }

    static List<UserCreate> userFromRequest(final EntityFactory entityFactory,
        final Collection<MgmtUserRequestBody> userRest) {
        if (userRest == null) {
            return Collections.emptyList();
        }
        return userRest.stream().map(aUserRest -> fromRequest(entityFactory, aUserRest)).collect(Collectors.toList());
    }

    private static UserCreate fromRequest(final EntityFactory entityFactory, final MgmtUserRequestBody userRest) {
        return entityFactory.user().create()
            .username(userRest.getUsername())
            .email(userRest.getEmail())
            .firstName(userRest.getFirstName())
            .lastName(userRest.getLastName())
            .userRoleByName(userRest.getUserRole());
    }

    static MgmtUserLogin toUserInfoResponse(final MgmtUser user, final MgmtUserRole userRole, String token) {
        MgmtUserLogin userLogin = new MgmtUserLogin();
        userLogin.setJwt(token);
        userLogin.setUser(user);
        userLogin.setUserRole(userRole);
        return userLogin;
    }
}