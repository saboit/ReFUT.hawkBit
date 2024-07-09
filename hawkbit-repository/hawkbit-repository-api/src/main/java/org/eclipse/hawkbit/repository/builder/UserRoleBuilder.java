/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.builder;

import org.eclipse.hawkbit.repository.model.UserRole;

/**
 * Builder for {@link UserRole}.
 *
 */
public interface UserRoleBuilder {

    /**
     * @return create builder
     */
    UserRoleCreate create();

    /**
     * @param id of the updatable entity
     * @return builder instance
     */
    UserRoleUpdate update(long id);
}
