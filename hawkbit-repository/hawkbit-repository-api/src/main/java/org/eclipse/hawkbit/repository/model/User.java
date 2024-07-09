/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.model;

/**
 * A User entity
 */
public interface User extends TenantAwareBaseEntity {

    int USERNAME_MAX_SIZE = 64;
    int EMAIL_MAX_SIZE = 128;
    int PASSWORD_MAX_SIZE = 100;
    int FIRST_NAME_MAX_SIZE = 64;
    int LAST_NAME_MAX_SIZE = 64;
    int LOGIN_HASH_MAX_SIZE = 20;
    int PASSWORD_RESET_HASH_MAX_SIZE = 20;

    public String getUsername();

    public void setUsername(String username);

    public String getEmail();

    public void setEmail(String email);

    public String getFirstName();

    public void setFirstName(String first_name);

    public String getLastName();

    public void setLastName(String lastName);

    public String getPassword();

    public void setPassword(String password);

    public String getLoginHash();

    public void setLoginHash(String loginHash);

    public long getLoginHashValid();

    public void setLoginHashValid(long until);

    public UserRole getUserRole();

    public void setUserRole(UserRole userRole);

    public String getPasswordResetHash();

    public void setPasswordResetHash(String passwordResetHash);

    public long getPaswordResetValid();

    public void setPaswordResetValid(long paswordResetValid);
}
