/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.model.helper;

import org.eclipse.hawkbit.tenancy.TenantAware;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class TenantAwareMessageConverter extends MessageConverter {

    public String convert(ILoggingEvent event) {
        return enhance(super.convert(event));
    }
    
    private String enhance(String incoming) {
        String tenant = "x";
        String user = "x";

        TenantAware tenantAware = TenantAwareHolder.getInstance().getTenantAware();
        if (tenantAware != null) {
            tenant = tenantAware.getCurrentTenant() == null ? "-" : tenantAware.getCurrentTenant();
            user = tenantAware.getCurrentUsername() == null ? "-" : tenantAware.getCurrentUsername();
        }

        return String.format("%s %s %s", tenant, user, incoming.replace("\n", " "));
    }
}
