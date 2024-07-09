/**
 * Copyright (c) 2019 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.utils;

import static org.eclipse.hawkbit.tenancy.configuration.TenantConfigurationProperties.TenantConfigurationKey.MULTI_ASSIGNMENTS_ENABLED;
import static org.eclipse.hawkbit.tenancy.configuration.TenantConfigurationProperties.TenantConfigurationKey.USER_CONFIRMATION_ENABLED;

import org.eclipse.hawkbit.repository.TenantConfigurationManagement;
import org.eclipse.hawkbit.security.SystemSecurityContext;

/**
 * A collection of static helper methods for the tenant configuration
 */
public final class TenantConfigHelper {

    private final TenantConfigurationManagement tenantConfigurationManagement;
    private final SystemSecurityContext systemSecurityContext;

    private TenantConfigHelper(final SystemSecurityContext systemSecurityContext,
            final TenantConfigurationManagement tenantConfigurationManagement) {
        this.systemSecurityContext = systemSecurityContext;
        this.tenantConfigurationManagement = tenantConfigurationManagement;
    }

    /**
     * Setting the context of the tenant.
     * 
     * @param systemSecurityContext
     *            Security context used to get the tenant and for execution
     * @param tenantConfigurationManagement
     *            to get the value from
     * @return is active
     */
    public static TenantConfigHelper usingContext(final SystemSecurityContext systemSecurityContext,
            final TenantConfigurationManagement tenantConfigurationManagement) {
        return new TenantConfigHelper(systemSecurityContext, tenantConfigurationManagement);
    }

    /**
     * Is multi-assignments enabled for the current tenant
     * 
     * @return is active
     */
    public boolean isMultiAssignmentsEnabled() {
        return systemSecurityContext.runAsSystem(() -> tenantConfigurationManagement
                .getConfigurationValue(MULTI_ASSIGNMENTS_ENABLED, Boolean.class).getValue());
    }

    /**
     * Is confirmation flow enabled for the current tenant
     *
     * @return is enabled
     */
    public boolean isConfirmationFlowEnabled() {
        return systemSecurityContext.runAsSystem(() -> tenantConfigurationManagement
            .getConfigurationValue(USER_CONFIRMATION_ENABLED, Boolean.class).getValue());
    }
}
