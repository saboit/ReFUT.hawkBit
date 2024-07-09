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
import org.eclipse.hawkbit.repository.model.User;

/**
 * Defines the remote event of creating a new {@link User}.
 *
 */
public class UserCreatedEvent extends RemoteEntityEvent<User>
        implements EntityCreatedEvent {
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public UserCreatedEvent() {
        // for serialization libs like jackson
    }

    /**
     * Constructor.
     *
     * @param baseEntity
     *            the User
     * @param applicationId
     *            the origin application id
     */
    public UserCreatedEvent(final User baseEntity, final String applicationId) {
        super(baseEntity, applicationId);
    }

}
