/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.specifications;

import org.eclipse.hawkbit.repository.jpa.model.JpaUserRole;
import org.eclipse.hawkbit.repository.jpa.model.JpaUserRole_;
import org.eclipse.hawkbit.repository.model.UserRole;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications class for {@link UserRole}s. The class provides Spring Data JPQL
 * Specifications.
 *
 */
public final class UserRoleSpecifications {
    private UserRoleSpecifications() {
        // utility class
    }

    /**
     * {@link Specification} for retrieving {@link UserRole}s by name
     *
     * @param name to search for
     *
     * @return the {@link UserRole} {@link Specification}
     */
    public static Specification<JpaUserRole> hasName(final String name) {
        return (targetUserRole, query, cb) -> cb.equal(targetUserRole.get(JpaUserRole_.name), name);
    }

}
