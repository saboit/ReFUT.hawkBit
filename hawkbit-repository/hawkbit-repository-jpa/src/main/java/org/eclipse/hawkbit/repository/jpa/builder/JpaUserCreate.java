/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.builder;

import org.eclipse.hawkbit.repository.UserRoleManagement;
import org.eclipse.hawkbit.repository.builder.AbstractUserUpdateCreate;
import org.eclipse.hawkbit.repository.builder.UserCreate;
import org.eclipse.hawkbit.repository.jpa.model.JpaUser;

/**
 * Create/build implementation.
 *
 */
public class JpaUserCreate extends AbstractUserUpdateCreate<UserCreate> 
    implements UserCreate {

    /**
     * Constructor
     */
    JpaUserCreate(UserRoleManagement userRoleManagement) {
        super(userRoleManagement);
    }

    @Override
    public JpaUser build() {
        final JpaUser result = new JpaUser();
        result.setUsername(getUsername());
        getEmail().ifPresent(result::setEmail);
        getFirstName().ifPresent(result::setFirstName);
        getLastName().ifPresent(result::setLastName);
        getPassword().ifPresent(result::setPassword);
        getLoginHash().ifPresent(result::setLoginHash);
        result.setUserRole(getUserRole());
        return result;
    }
}
