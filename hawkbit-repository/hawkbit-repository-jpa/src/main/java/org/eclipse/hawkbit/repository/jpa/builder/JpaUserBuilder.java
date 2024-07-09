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
import org.eclipse.hawkbit.repository.builder.GenericUserUpdate;
import org.eclipse.hawkbit.repository.builder.UserBuilder;
import org.eclipse.hawkbit.repository.builder.UserCreate;
import org.eclipse.hawkbit.repository.builder.UserUpdate;

/**
 * Builder implementation for {@link User}.
 *
 */
public class JpaUserBuilder implements UserBuilder {

    private UserRoleManagement userRoleManagement;

    /**
     * Constructor
     */
    public JpaUserBuilder(UserRoleManagement userRoleManagement) {
        this.userRoleManagement = userRoleManagement;
    }

    @Override
    public UserUpdate update(long id) {
        return new GenericUserUpdate(userRoleManagement, id);
    }

    @Override
    public UserCreate create() {
        return new JpaUserCreate(userRoleManagement);
    }
}
