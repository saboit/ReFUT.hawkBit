/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.event.remote.entity;

import org.eclipse.hawkbit.repository.event.entity.EntityCreatedEvent;
import org.eclipse.hawkbit.repository.model.UserRole;

/**
 * Defines the remote event of creating a new {@link UserRole}.
 *
 */
public class UserRoleCreatedEvent extends RemoteEntityEvent<UserRole>
        implements EntityCreatedEvent {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public UserRoleCreatedEvent() {
        // for serialization libs like jackson
    }

    /**
     * Constructor.
     *
     * @param baseEntity
     *            the User role
     * @param applicationId
     *            the origin application id
     */
    public UserRoleCreatedEvent(final UserRole baseEntity, final String applicationId) {
        super(baseEntity, applicationId);
    }

}
