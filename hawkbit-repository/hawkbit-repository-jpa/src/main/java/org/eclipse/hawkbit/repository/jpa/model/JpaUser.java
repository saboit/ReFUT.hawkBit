/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.eclipse.hawkbit.repository.event.remote.UserDeletedEvent;
import org.eclipse.hawkbit.repository.event.remote.entity.UserCreatedEvent;
import org.eclipse.hawkbit.repository.event.remote.entity.UserUpdatedEvent;
import org.eclipse.hawkbit.repository.model.User;
import org.eclipse.hawkbit.repository.model.UserRole;
import org.eclipse.hawkbit.repository.model.helper.EventPublisherHolder;
import org.eclipse.persistence.descriptors.DescriptorEvent;

/**
 * A user entity JPA implementation
 */
@Entity
@Table(name = "sp_user",
    indexes = {
        @Index(name = "sp_idx_user_prim", columnList = "id"),
        @Index(name = "sp_idx_user_username", columnList = "username")
    },
    uniqueConstraints = { @UniqueConstraint(columnNames = { "username", "tenant" }, name = "uk_user_username")})
public class JpaUser extends AbstractJpaTenantAwareBaseEntity implements User, EventAwareEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "username", nullable = false, length = User.USERNAME_MAX_SIZE)
    @Size(max = User.USERNAME_MAX_SIZE)
    @NotNull
    private String username;

    @Column(name = "email", nullable = true, length = User.EMAIL_MAX_SIZE)
    @Size(max = User.EMAIL_MAX_SIZE)
    private String email;

    @Column(name = "first_name", nullable = true, length = User.FIRST_NAME_MAX_SIZE)
    @Size(max = User.FIRST_NAME_MAX_SIZE)
    private String firstName;

    @Column(name = "last_name", nullable = true, length = User.LAST_NAME_MAX_SIZE)
    @Size(max = User.LAST_NAME_MAX_SIZE)
    private String lastName;

    @Column(name = "password", nullable = true, length = User.PASSWORD_MAX_SIZE)
    @Size(max = User.PASSWORD_MAX_SIZE)
    private String password;

    @Column(name = "login_hash", nullable = true, length = User.LOGIN_HASH_MAX_SIZE)
    @Size(max = User.LOGIN_HASH_MAX_SIZE)
    private String loginHash;

    @Column(name = "login_hash_valid", nullable = true)
    private long loginHashValid;

    @Column(name = "password_reset_hash", nullable = true, length = User.PASSWORD_RESET_HASH_MAX_SIZE)
    @Size(max = User.PASSWORD_RESET_HASH_MAX_SIZE)
    private String passwordResetHash;

    @Column(name = "password_reset_valid", nullable = true)
    private long paswordResetValid;

    @ManyToOne(fetch = FetchType.EAGER, optional = true, targetEntity = JpaUserRole.class, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_role_id", nullable = true, updatable = true, insertable = true,
        foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "fk_user_user_role"))
    @NotNull
    private UserRole userRole;

    public JpaUser() {
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginHash() {
        return this.loginHash;
    }

    public void setLoginHash(String loginHash) {
        this.loginHash = loginHash;
    }

    public long getLoginHashValid() {
        return this.loginHashValid;
    }

    public void setLoginHashValid(long loginHashValid) {
        this.loginHashValid = loginHashValid;
    }

    public String getPasswordResetHash() {
        return this.passwordResetHash;
    }

    public void setPasswordResetHash(String passwordResetHash) {
        this.passwordResetHash = passwordResetHash;
    }

    public long getPaswordResetValid() {
        return this.paswordResetValid;
    }

    public void setPaswordResetValid(long paswordResetValid) {
        this.paswordResetValid = paswordResetValid;
    }

    public UserRole getUserRole() {
        return this.userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public void fireCreateEvent(DescriptorEvent descriptorEvent) {
        EventPublisherHolder.getInstance().getEventPublisher().publishEvent(
                new UserCreatedEvent(this, EventPublisherHolder.getInstance().getApplicationId()));
    }

    @Override
    public void fireUpdateEvent(DescriptorEvent descriptorEvent) {
        EventPublisherHolder.getInstance().getEventPublisher().publishEvent(
                new UserUpdatedEvent(this, EventPublisherHolder.getInstance().getApplicationId()));
    }

    @Override
    public void fireDeleteEvent(DescriptorEvent descriptorEvent) {
        EventPublisherHolder.getInstance().getEventPublisher().publishEvent(new UserDeletedEvent(
                getTenant(), getId(), getClass(), EventPublisherHolder.getInstance().getApplicationId()));
    }
}
