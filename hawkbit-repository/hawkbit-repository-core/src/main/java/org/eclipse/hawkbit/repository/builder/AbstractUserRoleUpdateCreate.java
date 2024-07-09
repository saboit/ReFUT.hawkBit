/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.eclipse.hawkbit.repository.PrivilegeManagement;
import org.eclipse.hawkbit.repository.ValidString;
import org.springframework.util.StringUtils;

/**
 * Create builder DTO.
 *
 * @param <T> update or create builder interface
 */
public abstract class AbstractUserRoleUpdateCreate<T> extends AbstractNamedEntityBuilder<T> {

    protected List<@ValidString String> privileges;

    private final PrivilegeManagement privilegeManagement;

    protected AbstractUserRoleUpdateCreate(PrivilegeManagement privilegeManagement) {
        super();
        this.privilegeManagement = privilegeManagement;
    }

    public Optional<List<String>> getPrivileges() {
        return Optional.ofNullable(this.privileges);
    }

    public void setPrivileges(List<String> newPrivileges) {
        privileges(newPrivileges);
    }

    public T privileges(final List<String> privileges) {

        List<String> newPrivileges = new ArrayList<>();
        if (privileges != null) {
            newPrivileges = privileges.stream().map(StringUtils::trimWhitespace).collect(Collectors.toList());
            validatePrivileges(newPrivileges);
        }

        if (this.privileges == null) {
            this.privileges = newPrivileges;
        } else {
            this.privileges.addAll(newPrivileges);
        }

        return (T) this;
    }

    public T privilege(final String privilege) {
        validatePrivilege(privilege);

        if (this.privileges == null) {
            this.privileges = new ArrayList<>();
        }
        this.privileges.add(StringUtils.trimWhitespace(privilege));

        return (T) this;
    }

    private void validatePrivileges(List<String> privileges) {
        privileges.forEach(this::validatePrivilege);
    }

    private void validatePrivilege(String privilege) {
        if (!privilegeManagement.findAllPermissions().contains(privilege)) {
            throw new ValidationException(String.format("Privilege %s does not exist!", privilege));
        }
    }
}
