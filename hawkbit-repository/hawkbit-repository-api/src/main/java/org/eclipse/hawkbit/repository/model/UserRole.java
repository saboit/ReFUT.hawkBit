/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.model;

import java.util.List;

/**
 * A UserRole entity to be assigned to a user with certain set or privileges
 * assigned.
 */
public interface UserRole extends NamedEntity {

    /**
     * @return list of privileges
     */
    List<String> getPrivileges();
    
    /**
     * @param privileges list of privileges to be set (assigned)
     */
    void setPrivileges(List<String> privileges);
}
