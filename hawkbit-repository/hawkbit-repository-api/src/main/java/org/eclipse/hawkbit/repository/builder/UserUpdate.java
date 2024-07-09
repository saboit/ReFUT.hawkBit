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

import org.eclipse.hawkbit.repository.model.User;
import org.eclipse.hawkbit.repository.model.UserRole;

/**
 * Builder to update an existing {@link User} entry. Defines all fields that can
 * be updated.
 *
 */
public interface UserUpdate {
    /**
     * @param username for {@link User#getName()}
     * @return updated builder instance
     */
    UserUpdate username(@Size(min = 1, max = User.USERNAME_MAX_SIZE) @NotNull String username);

    /**
     * @param email for {@link User#getEmail()}
     * @return updated builder instance
     */
    UserUpdate email(@Size(min = 0, max = User.EMAIL_MAX_SIZE) String email);            

    /**
     * @param firstname for {@link User#getFirstName()}
     * @return updated builder instance
     */
    UserUpdate firstName(@Size(min = 0, max = User.FIRST_NAME_MAX_SIZE) String firstName);       

    /**
     * @param lastname for {@link User#getLastName()}
     * @return updated builder instance
     */
    UserUpdate lastName(@Size(min = 0, max = User.LAST_NAME_MAX_SIZE) String lastName);        

    /**
     * @param password for {@link User#getPassword()}
     * @return updated builder instance
     */
    UserUpdate password(@Size(min = 0, max = User.PASSWORD_MAX_SIZE) String password);

    /**
     * @param loginHash for {@link User#getLoginHash()}
     * @return updated builder instance
     */
    UserUpdate loginHash(@Size(min = 0, max = User.LOGIN_HASH_MAX_SIZE) String loginHash);        

    /**
     * @param userRole for {@link User#getUserRole()}
     * @return updated builder instance
     */
    UserUpdate userRole(@NotNull UserRole userRole);

}