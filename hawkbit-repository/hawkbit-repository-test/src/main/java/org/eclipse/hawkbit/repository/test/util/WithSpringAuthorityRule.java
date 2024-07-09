/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.test.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.hawkbit.im.authentication.SpPermission;
import org.eclipse.hawkbit.im.authentication.TenantAwareAuthenticationDetails;
import org.eclipse.hawkbit.im.authentication.UserPrincipal;
import org.eclipse.hawkbit.repository.model.helper.SystemManagementHolder;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class WithSpringAuthorityRule implements BeforeEachCallback, AfterEachCallback {

    public static final String DEFAULT_TENANT = "default";
    private  SecurityContext oldContext;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        oldContext = SecurityContextHolder.getContext();
        WithUser annotation = getWithUserAnnotation(context);
        if (annotation != null) {
            if (annotation.autoCreateTenant()) {
                createTenant(annotation.tenantId());
            }
            setSecurityContext(annotation);
        }
    }

    private WithUser getWithUserAnnotation(ExtensionContext context) {
        if (context.getRequiredTestMethod().isAnnotationPresent(WithUser.class)) {
            return context.getRequiredTestMethod().getAnnotation(WithUser.class);
        }
        if(context.getRequiredTestClass().isAnnotationPresent(WithUser.class)){
            return context.getRequiredTestClass().getAnnotation(WithUser.class);
        }
        return null;
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        SecurityContextHolder.setContext(oldContext);
    }

    private static  void setSecurityContext(final WithUser annotation) {
        SecurityContextHolder.setContext(new SecurityContext() {
            private static final long serialVersionUID = 1L;

            @Override
            public void setAuthentication(final Authentication authentication) {
                // nothing to do
            }

            @Override
            public Authentication getAuthentication() {
                final String[] authorities;
                if (annotation.allSpPermissions()) {
                    authorities = getAllAuthorities(annotation.authorities(), annotation.removeFromAllPermission());
                } else {
                    authorities = annotation.authorities();
                }
                final TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(
                        new UserPrincipal(annotation.principal(), annotation.principal(), annotation.principal(),
                                annotation.principal(), null, annotation.tenantId()),
                        annotation.credentials(), authorities);
                testingAuthenticationToken.setDetails(
                        new TenantAwareAuthenticationDetails(annotation.tenantId(), annotation.controller()));
                return testingAuthenticationToken;
            }

            private String[] getAllAuthorities(final String[] additionalAuthorities, final String[] notInclude) {
                final List<String> permissions = SpPermission.getAllAuthorities();
                if (notInclude != null) {
                    permissions.removeAll(Arrays.asList(notInclude));
                }
                if (additionalAuthorities != null) {
                    permissions.addAll(Arrays.asList(additionalAuthorities));
                }
                return permissions.toArray(new String[0]);
            }
        });
    }

    public static <T> T  runAsPrivileged(final Callable<T> callable) throws Exception {
        return runAs(privilegedUser(), callable);
    }

    public static <T> T  runAs(final WithUser withUser, final Callable<T> callable) throws Exception {
        final SecurityContext oldContext = SecurityContextHolder.getContext();
        setSecurityContext(withUser);
        if (withUser.autoCreateTenant()) {
            createTenant(withUser.tenantId());
        }
        try {
            return callable.call();
        } finally {
            SecurityContextHolder.setContext(oldContext);
        }
    }

    private static void createTenant(final String tenantId) {
        final SecurityContext oldContext = SecurityContextHolder.getContext();
        setSecurityContext(privilegedUser());
        try {
            SystemManagementHolder.getInstance().getSystemManagement().getTenantMetadata(tenantId);
        } finally {
            SecurityContextHolder.setContext(oldContext);
        }
    }

    public static WithUser withController(final String principal, final String... authorities) {
        return withUserAndTenant(principal, DEFAULT_TENANT, true, true, true, authorities);
    }

    public static WithUser withUser(final String principal, final String... authorities) {
        return withUserAndTenant(principal, DEFAULT_TENANT, true, true, false, authorities);
    }

    public static WithUser withUser(final String principal, final boolean allSpPermision, final String... authorities) {
        return withUserAndTenant(principal, DEFAULT_TENANT, true, allSpPermision, false, authorities);
    }

    public static WithUser withUser(final boolean autoCreateTenant) {
        return withUserAndTenant("bumlux", DEFAULT_TENANT, autoCreateTenant, true, false);
    }

    public static WithUser withUserAndTenant(final String principal, final String tenant, final String... authorities) {
        return withUserAndTenant(principal, tenant, true, true, false, authorities);
    }

    public static WithUser withUserAndTenant(final String principal, final String tenant,
            final boolean autoCreateTenant, final boolean allSpPermission, final boolean controller,
            final String... authorities) {
        return createWithUser(principal, tenant, autoCreateTenant, allSpPermission, controller, authorities);
    }

    private static WithUser privilegedUser() {
        return createWithUser("bumlux", DEFAULT_TENANT, true, true, false, "ROLE_CONTROLLER", "ROLE_SYSTEM_CODE");
    }

    private static WithUser createWithUser(final String principal, final String tenant, final boolean autoCreateTenant,
            final boolean allSpPermission, final boolean controller, final String... authorities) {
        return new WithUser() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return WithUser.class;
            }

            @Override
            public String principal() {
                return principal;
            }

            @Override
            public String credentials() {
                return null;
            }

            @Override
            public String[] authorities() {
                return authorities;
            }

            @Override
            public boolean allSpPermissions() {
                return allSpPermission;
            }

            @Override
            public String[] removeFromAllPermission() {
                return new String[0];
            }

            @Override
            public String tenantId() {
                return tenant;
            }

            @Override
            public boolean autoCreateTenant() {
                return autoCreateTenant;
            }

            @Override
            public boolean controller() {
                return controller;
            }
        };
    }
}
