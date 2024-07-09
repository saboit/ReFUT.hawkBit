/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.builder;

import org.eclipse.hawkbit.repository.UserRoleManagement;

/**
 * Update builder implementation for {@link User}
 */
public class GenericUserUpdate extends AbstractUserUpdateCreate<UserUpdate>
        implements UserUpdate {

    /**
     * @param userRoleManagement {@link UserRoleManagement} service to retrive assigned used role  
     * @param id User ID
     */
    public GenericUserUpdate(UserRoleManagement userRoleManagement, final Long id) {
        super(userRoleManagement);
        super.id = id;
    }

}