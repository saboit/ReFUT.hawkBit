/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

import java.util.List;

import org.eclipse.hawkbit.im.authentication.SpPermission.SpringEvalExpressions;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Management service for {@link Privilege}s.
 *
 */
public interface PrivilegeManagement {

    /**
     * returns all {@link Privilege}s.
     * 
     * @return all {@link Privilege}s
     */
    @PreAuthorize(SpringEvalExpressions.HAS_AUTH_USER_MANAGEMENT_READ)
    List<String> findAllPermissions();
}
