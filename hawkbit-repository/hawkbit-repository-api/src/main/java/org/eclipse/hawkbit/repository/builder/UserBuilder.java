/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.builder;

import org.eclipse.hawkbit.repository.model.User;

/**
 * Builder for {@link User}.
 *
 */
public interface UserBuilder {

    /**
     * @return user create builder
     */
    UserCreate create();

    /**
     * @param id of the updatable user entity
     * @returnuser user update builder instance
     */
    UserUpdate update(long id);
}
