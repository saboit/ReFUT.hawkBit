/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.eclipse.hawkbit.repository.event.remote.UserRoleDeletedEvent;
import org.eclipse.hawkbit.repository.event.remote.entity.UserRoleCreatedEvent;
import org.eclipse.hawkbit.repository.event.remote.entity.UserRoleUpdatedEvent;
import org.eclipse.hawkbit.repository.model.UserRole;
import org.eclipse.hawkbit.repository.model.helper.EventPublisherHolder;
import org.eclipse.persistence.annotations.CascadeOnDelete;
import org.eclipse.persistence.descriptors.DescriptorEvent;

/**
 * A user role defines privileges (authorities) granted
 */
@Entity
@Table(name = "sp_user_role", 
    indexes = { @Index(name = "sp_idx_user_role_prim", columnList = "id") }, 
    uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "tenant" }, name = "uk_user_role_name")})
public class JpaUserRole extends AbstractJpaNamedEntity implements UserRole, EventAwareEntity{

    private static final long serialVersionUID = 1L;
    private static final int PRIVILEGE_LENGTH = 64;

    @CascadeOnDelete
    @ElementCollection(fetch = FetchType.EAGER, targetClass = String.class)
    @CollectionTable(name = "sp_user_role_privilege",
        joinColumns = @JoinColumn(name = "user_role_id", 
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "fk_user_role_privilege_user_role"), 
            updatable = true, nullable = false), 
        indexes = { @Index(name = "sp_idx_action_status_msgs_01", columnList = "action_status_id") })
    @Column(name = "privilege", length = PRIVILEGE_LENGTH, nullable = false, updatable = true)
    private List<String> privileges;

    /**
     * Constructor
     */
    public JpaUserRole() {
        privileges = new ArrayList<>();
    }

    /**
     * Constructor
     *
     * @param name
     *          Type name
     * @param description
     *          Description
     */
    public JpaUserRole(String name, String description) {
        super(name, description);
        privileges = new ArrayList<>();
    }

    /**
     * @param privilege Privilege (Authorization)
     * @return User Role JPA
     */
    public JpaUserRole addPrivilege(final String privilege) {
        if (privileges == null) {
            privileges = new ArrayList<>();
        }

        privileges.add(privilege);
        return this;
    }

    /**
     * @param privilege Privilege (Authorization)
     * @return User Role
     */
    public JpaUserRole removePrivilege(final String privilege) {
        if (privileges == null) {
            return this;
        }
        privileges.remove(privilege);
        return this;
    }

    @Override
    public List<String> getPrivileges() {
        return privileges;
    }

    @Override
    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    @Override
    public void fireCreateEvent(DescriptorEvent descriptorEvent) {
        EventPublisherHolder.getInstance().getEventPublisher().publishEvent(
                new UserRoleCreatedEvent(this, EventPublisherHolder.getInstance().getApplicationId()));
    }

    @Override
    public void fireUpdateEvent(DescriptorEvent descriptorEvent) {
        EventPublisherHolder.getInstance().getEventPublisher().publishEvent(
                new UserRoleUpdatedEvent(this, EventPublisherHolder.getInstance().getApplicationId()));
    }

    @Override
    public void fireDeleteEvent(DescriptorEvent descriptorEvent) {
        EventPublisherHolder.getInstance().getEventPublisher().publishEvent(new UserRoleDeletedEvent(
                getTenant(), getId(), getClass(), EventPublisherHolder.getInstance().getApplicationId()));
    }
}
