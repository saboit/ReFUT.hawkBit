/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa;

import java.util.List;

import org.eclipse.hawkbit.im.authentication.SpPermission;
import org.eclipse.hawkbit.repository.PrivilegeManagement;

/**
 * JPA implementation of {@link PrivilegeManagement}.
 *
 */
public class JpaPrivilegeManagement implements PrivilegeManagement {

    @Override
    public List<String> findAllPermissions() {
        List<String> result = SpPermission.getAllAuthorities();
        result.removeIf(auth -> auth.equals("SYSTEM_ADMIN"));
        return result;
    }
}
