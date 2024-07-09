/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.autoconfigure;

import java.io.IOException;
import java.util.Map;

import org.eclipse.hawkbit.tenancy.TenantAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.HttpResponse;
import org.zalando.logbook.Precorrelation;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserJsonHttpLogFormatter implements HttpLogFormatter {
        
    @Autowired
    private TenantAware tenantAware;

    private final JsonHttpLogFormatter delegate;

    public UserJsonHttpLogFormatter(ObjectMapper mapper) {
        this.delegate = new JsonHttpLogFormatter(mapper);
    }

    public String format(Precorrelation precorrelation, HttpRequest request) throws IOException {
        Map<String, Object> content = delegate.prepare(precorrelation, request);
        addUserInfo(content);
        String entry = delegate.format(content);
        return entry.replace("\n", " ");
    }

    public String format(Correlation correlation, HttpResponse response) throws IOException {
        Map<String, Object> content = delegate.prepare(correlation, response);
        addUserInfo(content);
        return delegate.format(content);
    }

    private void addUserInfo(Map<String, Object>content) {
        content.put("tenant", tenantAware.getCurrentTenant());
        content.put("user", tenantAware.getCurrentUsername());
    }
}