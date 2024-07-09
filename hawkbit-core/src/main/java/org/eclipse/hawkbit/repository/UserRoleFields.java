/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Describing the fields of the {@link User} model which can be used in the REST API
 * e.g. for sorting etc.
 *
 */
public enum UserRoleFields implements FieldNameProvider {

    /**
     * The id field.
     */
    ID("id"),

    /**
     * The username field.
     */
    NAME("name"),

    /**
     * The first name field.
     */

    DESCRIPTION("description");

    private final String fieldName;
    private List<String> subEntityAttribues;
    private boolean mapField;
    private Entry<String, String> subEntityMapTuple;

    private UserRoleFields(final String fieldName) {
        this(fieldName, false, Collections.emptyList(), null);
    }

    private UserRoleFields(final String fieldName, final boolean isMapField) {
        this(fieldName, isMapField, Collections.emptyList(), null);
    }

    private UserRoleFields(final String fieldName, final String... subEntityAttribues) {
        this(fieldName, false, Arrays.asList(subEntityAttribues), null);
    }

    private UserRoleFields(final String fieldName, final Entry<String, String> subEntityMapTuple) {
        this(fieldName, true, Collections.emptyList(), subEntityMapTuple);
    }

    private UserRoleFields(final String fieldName, final boolean mapField, final List<String> subEntityAttribues,
            final Entry<String, String> subEntityMapTuple) {
        this.fieldName = fieldName;
        this.mapField = mapField;
        this.subEntityAttribues = subEntityAttribues;
        this.subEntityMapTuple = subEntityMapTuple;
    }

    @Override
    public List<String> getSubEntityAttributes() {
        return subEntityAttribues;
    }

    @Override
    public Optional<Entry<String, String>> getSubEntityMapTuple() {
        return Optional.ofNullable(subEntityMapTuple);
    }

    @Override
    public boolean isMap() {
        return mapField;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }
}
