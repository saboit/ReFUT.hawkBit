/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.exception;

import org.eclipse.hawkbit.exception.AbstractServerRtException;
import org.eclipse.hawkbit.exception.SpServerError;
import org.eclipse.hawkbit.repository.model.BaseEntity;

/**
 * the {@link EntityAlreadyExistsException} is thrown when an entity is tried to
 * be saved which already exists or which violates unique key constraints.
 */
public class UserRoleNotExistsException extends AbstractServerRtException {

    private static final long serialVersionUID = 1L;
    private static final SpServerError THIS_ERROR = SpServerError.SP_USER_ROLE_NOT_EXISTS;

    /**
     * Default constructor.
     */
    public UserRoleNotExistsException() {
        super(THIS_ERROR);
    }

    /**
     * Parameterized constructor.
     * 
     * @param cause
     *            of the exception
     */
    public UserRoleNotExistsException(final Throwable cause) {
        super(THIS_ERROR, cause);
    }

    /**
     * Parameterized constructor.
     * 
     * @param message
     *            of the exception
     * @param cause
     *            of the exception
     */
    public UserRoleNotExistsException(final String message, final Throwable cause) {
        super(message, THIS_ERROR, cause);
    }

    /**
     * Parameterized constructor.
     * 
     * @param message
     *            of the exception
     */
    public UserRoleNotExistsException(final String message) {
        super(message, THIS_ERROR);
    }

    /**
     * Parameterized constructor for {@link BaseEntity} not found.
     * 
     * @param type
     *            of the entity that was not found
     * 
     * @param entityId
     *            of the {@link BaseEntity}
     */
    public UserRoleNotExistsException(final Class<? extends BaseEntity> type, final Object entityId) {
        this(type.getSimpleName() + " with given identifier {" + entityId + "} does not exist.");
    }

}
