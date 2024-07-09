/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.builder;

import org.eclipse.hawkbit.repository.PrivilegeManagement;
import org.eclipse.hawkbit.repository.builder.GenericUserRoleUpdate;
import org.eclipse.hawkbit.repository.builder.UserRoleBuilder;
import org.eclipse.hawkbit.repository.builder.UserRoleCreate;
import org.eclipse.hawkbit.repository.builder.UserRoleUpdate;
import org.eclipse.hawkbit.repository.model.UserRole;

/**
 * Builder implementation for {@link UserRole}.
 *
 */
public class JpaUserRoleBuilder implements UserRoleBuilder {

    private final PrivilegeManagement privilegeManagement;

    public JpaUserRoleBuilder(PrivilegeManagement privilegeManagement) {
        this.privilegeManagement = privilegeManagement;
    }

    @Override
    public UserRoleUpdate update(long id) {
        return new GenericUserRoleUpdate(privilegeManagement, id);
    }

    @Override
    public UserRoleCreate create() {
        return new JpaUserRoleCreate(privilegeManagement);
    }
}
