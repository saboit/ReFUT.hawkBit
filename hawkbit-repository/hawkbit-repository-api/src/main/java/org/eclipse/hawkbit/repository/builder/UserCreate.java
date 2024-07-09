/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.eclipse.hawkbit.repository.model.BaseEntity;
import org.eclipse.hawkbit.repository.model.User;
import org.eclipse.hawkbit.repository.model.UserRole;

/**
 * Builder to create a new {@link User} entry. Defines all fields that can be set
 * at creation time. Other fields are set by the repository automatically, e.g.
 * {@link BaseEntity#getCreatedAt()}.
 *
 */
public interface UserCreate {

    /**
     * @param username for {@link User#getName()}
     * @return updated builder instance
     */
    UserCreate username(@Size(min = 1, max = User.USERNAME_MAX_SIZE) @NotNull String username);

    /**
     * @param email for {@link User#getEmail()}
     * @return updated builder instance
     */
    UserCreate email(@Size(min = 0, max = User.EMAIL_MAX_SIZE) String email);            

    /**
     * @param firstname for {@link User#getFirstName()}
     * @return updated builder instance
     */
    UserCreate firstName(@Size(min = 0, max = User.FIRST_NAME_MAX_SIZE) String firstName);       

    /**
     * @param lastname for {@link User#getLastName()}
     * @return updated builder instance
     */
    UserCreate lastName(@Size(min = 0, max = User.LAST_NAME_MAX_SIZE) String lastName);        

    /**
     * @param password for {@link User#getPassword()}
     * @return updated builder instance
     */
    UserCreate password(@Size(min = 0, max = User.PASSWORD_MAX_SIZE) String password);

    /**
     * @param loginHash for {@link User#getLoginHash()}
     * @return updated builder instance
     */
    UserCreate loginHash(@Size(min = 0, max = User.LOGIN_HASH_MAX_SIZE) String loginHash);        

    /**
     * @param userRole for {@link User#getUserRole()}
     * @return updated builder instance
     */
    UserCreate userRole(@NotNull UserRole userRole);

    /**
     * @param userRole for {@link User#getUserRole()}
     * @return updated builder instance
     */
    UserCreate userRoleByName(@NotNull String userRole);

    /**
     * @return peek on current state of {@link User} in the builder
     */
    User build();
}
