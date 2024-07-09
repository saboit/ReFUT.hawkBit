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
import org.eclipse.hawkbit.repository.builder.AbstractUserRoleUpdateCreate;
import org.eclipse.hawkbit.repository.builder.UserRoleCreate;
import org.eclipse.hawkbit.repository.jpa.model.JpaUserRole;

/**
 * Create/build implementation.
 *
 */
public class JpaUserRoleCreate extends AbstractUserRoleUpdateCreate<UserRoleCreate> 
    implements UserRoleCreate {

    /**
     * Constructor
     */
    JpaUserRoleCreate(PrivilegeManagement privilegeManagement) {
        super(privilegeManagement);
    }

    @Override
    public JpaUserRole build() {
        final JpaUserRole result = new JpaUserRole(name, description);

        if (privileges != null) {
            privileges.forEach(result::addPrivilege);
        }

        return result;
    }
}
