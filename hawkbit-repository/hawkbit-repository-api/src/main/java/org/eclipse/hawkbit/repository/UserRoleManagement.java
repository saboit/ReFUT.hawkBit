/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.eclipse.hawkbit.im.authentication.SpPermission.SpringEvalExpressions;
import org.eclipse.hawkbit.repository.builder.UserRoleCreate;
import org.eclipse.hawkbit.repository.builder.UserRoleUpdate;
import org.eclipse.hawkbit.repository.exception.EntityAlreadyExistsException;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Management service for {@link UserRole}s.
 *
 */
public interface UserRoleManagement {

    /**
     * count {@link UserRole}s.
     * 
     * @return size of {@link UserRole}s
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_READ)
    long count();

    /**
     * Creates a new {@link UserRole}.
     * 
     * @param create to be created
     *
     * @return the new created {@link UserRole}
     *
     * @throws EntityAlreadyExistsException if given object already exists
     * @throws ConstraintViolationException if fields are not filled as specified. Check
     *             {@link UserRole} for field constraints.
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_CREATE)
    UserRole create(@NotNull @Valid UserRoleCreate create);

    /**
     * @param creates List of UserRoleCreate
     * @return List of userRoles
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_CREATE)
    List<UserRole> create(@NotEmpty @Valid Collection<UserRoleCreate> creates);

    /**
     * returns all {@link UserRole}s.
     * 
     * @param pageable page parameter
     *
     * @return all {@link UserRole}s
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_CREATE)
    Page<UserRole> findAll(@NotNull Pageable pageable);

    /**
     * Finds {@link UserRole} by given id.
     *
     * @param id to search for
     * @return the found {@link UserRole}
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_READ)
    Optional<UserRole> get(long id);

    /**
     * Finds {@link UserRole} by given name.
     *
     * @param name to search for
     * @return the found {@link UserRole}
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_READ)
    Optional<UserRole> get(String name);

    /**
     * updates the {@link UserRole}.
     *
     * @param update
     *            the {@link UserRole} with updated values
     * @return the updated {@link UserRole}
     * 
     * @throws EntityNotFoundException
     *             in case the {@link UserRole} does not exists and cannot be
     *             updated
     * @throws ConstraintViolationException
     *             if fields are not filled as specified. Check
     *             {@link UserRole} for field constraints.
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_UPDATE)
    UserRole update(@NotNull @Valid UserRoleUpdate update);

    /**
     * Deletes a {@link UserRole} with the given ID
     * 
     * @param id userRoleId
     * 
     * @throws EntityNotFoundException
     *             if tag with given name does not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_DELETE)
    void delete(@NotNull Long id);

}
