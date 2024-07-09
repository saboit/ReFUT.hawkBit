/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.builder;

import org.eclipse.hawkbit.repository.PrivilegeManagement;

/**
 * Update implementation for User role
 */
public class GenericUserRoleUpdate extends AbstractUserRoleUpdateCreate<UserRoleUpdate>
        implements UserRoleUpdate {

    /**
     * @param id User role ID
     */
    public GenericUserRoleUpdate(PrivilegeManagement privilegeManagement, final Long id) {
        super(privilegeManagement);
        super.id = id;
    }
}
