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

import org.eclipse.hawkbit.repository.jpa.model.JpaUserRole;
import org.eclipse.hawkbit.repository.jpa.specifications.UserRoleSpecifications;
import org.eclipse.hawkbit.repository.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link UserRole} repository.
 *
 */
@Transactional(readOnly = true)
public interface UserRoleRepository
        extends BaseEntityRepository<JpaUserRole, Long>, JpaSpecificationExecutor<JpaUserRole> {

   /**
     * Returns all instances of the type.
     *
     * @return all entities
     */
    @Override
    List<JpaUserRole> findAll();

    /**
     * Finds all {@link UserRole}s in the repository sorted.
     *
     * Calls version with (empty) spec to allow injecting further specs
     *
     * @param sort instructions to sort result by
     * @return {@link List} of {@link UserRole}s
     */
    @Override
    @NonNull
    default Iterable<JpaUserRole> findAll(@NonNull Sort sort) {
        return this.findAll(Specification.where(null), sort);
    }

    /**
     * Finds a page of {@link UserRole}s in the repository.
     *
     * Calls version with (empty) spec to allow injecting further specs
     *
     * @param pageable paging context
     * @return {@link List} of {@link UserRole}s
     */
    @Override
    @NonNull
    default Page<JpaUserRole> findAll(@NonNull Pageable pageable) {
        return this.findAll(Specification.where(null), pageable);
    }

    /**
     * Finds a {@link UserRole}s in the repository by its name
     *
     * @param name of the user role
     * @return {@link List} of {@link UserRole}s
     */
    @NonNull
    default Optional<JpaUserRole> findByName(@NonNull String name) {
        return this.findOne(Specification.where(UserRoleSpecifications.hasName(name)));
    }
}