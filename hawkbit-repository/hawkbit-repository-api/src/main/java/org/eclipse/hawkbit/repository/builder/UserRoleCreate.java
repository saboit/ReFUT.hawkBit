/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.builder;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.eclipse.hawkbit.repository.model.BaseEntity;
import org.eclipse.hawkbit.repository.model.NamedEntity;
import org.eclipse.hawkbit.repository.model.UserRole;

/**
 * Builder to create a new {@link UserRole} entry. Defines all fields that can be set
 * at creation time. Other fields are set by the repository automatically, e.g.
 * {@link BaseEntity#getCreatedAt()}.
 *
 */
public interface UserRoleCreate {
    /**
     * @param name
     *            for {@link UserRole#getName()}
     * @return updated builder instance
     */
    UserRoleCreate name(@Size(min = 1, max = NamedEntity.NAME_MAX_SIZE) @NotNull String name);

    /**
     * @param description
     *            for {@link UserRole#getDescription()}
     * @return updated builder instance
     */
    UserRoleCreate description(@Size(max = NamedEntity.DESCRIPTION_MAX_SIZE) String description);

    /**
     * @param privileges
     *            for {@link UserRole#getPrivileges()}
     * @return updated {@link UserRoleCreate} object
     */
    UserRoleCreate privileges(List<String> privileges);

    /**
     * @param privilege
     *            for {@link UserRole#getPrivileges()}
     * @return updated {@link UserRoleCreate} object
     */
    UserRoleCreate privilege(String privilege);

    /**
     * @return peek on current state of {@link UserRole} in the builder
     */
    UserRole build();
}
