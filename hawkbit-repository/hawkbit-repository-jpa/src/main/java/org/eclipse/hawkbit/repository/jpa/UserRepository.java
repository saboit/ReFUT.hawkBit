/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.eclipse.hawkbit.repository.jpa.model.JpaUser;
import org.eclipse.hawkbit.repository.jpa.specifications.UserSpecifications;
import org.eclipse.hawkbit.repository.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link User} repository.
 *
 */
@Transactional(readOnly = true)
public interface UserRepository
        extends BaseEntityRepository<JpaUser, Long>, JpaSpecificationExecutor<JpaUser> {

   /**
     * Returns all instances of the type.
     *
     * @return all users
     */
    @Override
    List<JpaUser> findAll();

    /**
     * Finds all {@link User}s in the repository sorted.
     *
     * Calls version with (empty) spec to allow injecting further specs
     *
     * @param sort instructions to sort result by
     * @return {@link List} of {@link User}s
     */
    @Override
    @NonNull
    default Iterable<JpaUser> findAll(@NonNull Sort sort) {
        return this.findAll(Specification.where(null), sort);
    }

    /**
     * Finds a page of {@link User}s in the repository.
     *
     * Calls version with (empty) spec to allow injecting further specs
     *
     * @param pageable paging context
     * @return {@link List} of {@link User}s
     */
    @Override
    @NonNull
    default Page<JpaUser> findAll(@NonNull Pageable pageable) {
        return this.findAll(Specification.where(null), pageable);
    }

    /**
     * Finds a {@link User}s in the repository by a username.
     *
     * @param username of the user to be found
     * @return found {@link User}
     */
    @NonNull
    default Optional<JpaUser> findByUsername(String username) {
        return this.findOne(Specification.where(UserSpecifications.hasUsername(username)));
    }

    /**
     * Finds a {@link User}s in the repository by a username and email.
     *
     * @param username of the user to be found
     * @param email of the user to be found
     * @return found {@link User}
     */
    @NonNull
    default Optional<JpaUser> findByUsernameAndEmail(String username, String email) {
        return this.findOne(Specification.where(UserSpecifications.hasUsernameAndEmail(username, email)));
    }

    /**
     * Finds a {@link User}s in the repository by a username and email.
     *
     * @param username of the user to be found
     * @param email of the user to be found
     * @return found {@link User}
     */
    @NonNull
    default Optional<JpaUser> findByPasswordResetHash(final String resetHash) {
        return this.findOne(Specification.where(UserSpecifications.hasPasswordResetHash(resetHash)));
    }
}
