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
import org.eclipse.hawkbit.repository.builder.UserCreate;
import org.eclipse.hawkbit.repository.builder.UserUpdate;
import org.eclipse.hawkbit.repository.exception.EntityAlreadyExistsException;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Management service for {@link User}s.
 *
 */
public interface UserManagement {

    /**
     * count {@link User}s.
     *
     * @return size of {@link User}s
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_READ)
    long count();

    /**
     * Creates a new {@link User}.
     *
     * @param create to be created
     *
     * @return the new created {@link User}
     *
     * @throws EntityAlreadyExistsException if given object already exists
     * @throws ConstraintViolationException if fields are not filled as specified. Check
     *             {@link User} for field constraints.
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_CREATE)
    User create(@NotNull @Valid UserCreate create);

    /**
     * @param creates List of UserCreate
     * @return List of users
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_CREATE)
    List<User> create(@NotEmpty @Valid Collection<UserCreate> creates);

    /**
     * returns all {@link User}s.
     *
     * @param pageable page parameter
     *
     * @return all {@link User}s
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_CREATE)
    Page<User> findAll(@NotNull Pageable pageable);

    /**
     * Finds {@link User} by given id.
     *
     * @param id of a user to search for
     * @return the found {@link User}
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_READ)
    Optional<User> get(long id);

    /**
     * Finds {@link User} by given id.
     *
     * @param username of a user to search for
     * @return the found {@link User}
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_READ + " or principal.username == #username")
    Optional<User> get(String username);

    /**
     * Finds {@link User} by given username and email (for sake of password reset)
     *
     * @param username of a user to search for
     * @param email of a user to search for
     * @return the found {@link User}
     */
    @PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
    Optional<User> get(String username, String email);

    /**
     * Finds {@link User} by given username and email (for sake of password reset)
     *
     * @param username of a user to search for
     * @param email of a user to search for
     * @return the found {@link User}
     */
    @PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
    Optional<User> getByPasswordResetHash(String resetHash);

    /**
     * updates the {@link User}.
     *
     * @param update the {@link User} with updated values
     * @return the updated {@link User}
     *
     * @throws EntityNotFoundException
     *      in case the {@link User} does not exists and cannot be updated
     * @throws ConstraintViolationException
     *             if fields are not filled as specified. Check
     *             {@link User} for field constraints.
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_UPDATE)
    User update(@NotNull @Valid UserUpdate update);

    /**
     * updates the {@link User}.
     *
     * @param update the {@link User} with updated values
     * @return the updated {@link User}
     *
     * @throws EntityNotFoundException
     *      in case the {@link User} does not exists and cannot be updated
     * @throws ConstraintViolationException
     *             if fields are not filled as specified. Check
     *             {@link User} for field constraints.
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_UPDATE 
        + " or principal.username == #user.username or hasAuthority('ROLE_ANONYMOUS')")
    User update(@NotNull User user);

    /**
     * Deletes a {@link User} with the given ID
     *
     * @param id userId
     *
     * @throws EntityNotFoundException if user  with given name does not exist
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_DELETE)
    void delete(@NotNull Long id);
}
