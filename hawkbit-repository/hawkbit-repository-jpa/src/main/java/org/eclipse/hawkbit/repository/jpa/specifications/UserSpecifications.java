/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.specifications;

import org.eclipse.hawkbit.repository.jpa.model.JpaUser;
import org.eclipse.hawkbit.repository.jpa.model.JpaUser_;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications class for {@link User}s. The class provides Spring Data JPQL
 * Specifications.
 *
 */
public final class UserSpecifications {
    private UserSpecifications() {
        // utility class
    }

    /**
     * {@link Specification} for retrieving {@link User}s by username
     *
     * @param username to search for
     *
     * @return the {@link User} {@link Specification}
     */
    public static Specification<JpaUser> hasUsername(final String username) {
        return (targetUser, query, cb) -> cb.equal(targetUser.get(JpaUser_.username), username);
    }

    /**
     * {@link Specification} for retrieving {@link User}s by username
     *
     * @param username to search for
     *
     * @return the {@link User} {@link Specification}
     */
    public static Specification<JpaUser> hasUsernameAndEmail(final String username, final String email) {
        return (targetUser, query, cb) -> cb.and(
            cb.equal(targetUser.get(JpaUser_.username), username),
            cb.equal(targetUser.get(JpaUser_.email), email)
        );
    }

    /**
     * {@link Specification} for retrieving {@link User}s by password reset hash
     *
     * @param password reset hash to search for
     *
     * @return the {@link User} {@link Specification}
     */
    public static Specification<JpaUser> hasPasswordResetHash(final String resetHash) {
        return (targetUser, query, cb) -> cb.equal(targetUser.get(JpaUser_.passwordResetHash), resetHash);
    }
}
