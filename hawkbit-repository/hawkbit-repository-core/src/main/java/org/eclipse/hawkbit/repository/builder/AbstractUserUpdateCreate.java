/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.builder;

import java.util.Optional;

import org.eclipse.hawkbit.repository.UserRoleManagement;
import org.eclipse.hawkbit.repository.ValidString;
import org.eclipse.hawkbit.repository.model.UserRole;
import org.springframework.util.StringUtils;
import org.eclipse.hawkbit.repository.exception.UserRoleNotExistsException;

/**
 * Create user builder DTO.
 *
 * @param <T> update or create user builder interface
 */
public abstract class AbstractUserUpdateCreate<T> extends AbstractBaseEntityBuilder {

    @ValidString protected String username;
    protected String email;
    protected String firstName;
    protected String lastName;
    protected String password;
    protected String loginHash;
    protected UserRole userRole;

    private final UserRoleManagement userRoleManagement;

    public AbstractUserUpdateCreate(UserRoleManagement userRoleManagement) {
        super();
        this.userRoleManagement = userRoleManagement;
    }

    public T username(final String username) {
        this.username = StringUtils.trimWhitespace(username);
        return (T) this;
    }

    public T email(final String email) {
        this.email = StringUtils.trimWhitespace(email);
        return (T) this;
    }

    public T firstName(final String firstName) {
        this.firstName = StringUtils.trimWhitespace(firstName);
        return (T) this;
    }

    public T lastName(final String lastName) {
        this.lastName = StringUtils.trimWhitespace(lastName);
        return (T) this;
    }

    public T password(final String password) {
        this.password = StringUtils.trimWhitespace(password);
        return (T) this;
    }

    public T loginHash(final String loginHash) {
        this.loginHash = StringUtils.trimWhitespace(loginHash);
        return (T) this;
    }

    public T userRole(final UserRole userRole) {
        this.userRole = userRole;
        return (T) this;
    }

    public T userRoleByName(final String userRole) {
        Optional<UserRole> role = userRoleManagement.get(userRole);
        this.userRole = role.orElseThrow(() -> new UserRoleNotExistsException(UserRole.class, userRole));
        return (T) this;
    }

    public String getUsername() {
        return username;
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> getLastName() {
        return Optional.ofNullable(lastName);
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    public Optional<String> getLoginHash() {
        return Optional.ofNullable(loginHash);
    }

    public UserRole getUserRole() {
        return userRole;
    }
}
