/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.hawkbit.repository.UserRoleManagement;
import org.eclipse.hawkbit.repository.builder.GenericUserRoleUpdate;
import org.eclipse.hawkbit.repository.builder.UserRoleCreate;
import org.eclipse.hawkbit.repository.builder.UserRoleUpdate;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.jpa.builder.JpaUserRoleCreate;
import org.eclipse.hawkbit.repository.jpa.configuration.Constants;
import org.eclipse.hawkbit.repository.jpa.model.JpaUserRole;
import org.eclipse.hawkbit.repository.model.UserRole;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * JPA implementation of {@link UserRoleManagement}.
 *
 */
@Transactional(readOnly = true)
@Validated
public class JpaUserRoleManagement implements UserRoleManagement {

    private final UserRoleRepository userRoleRepository;

    /**
     * Constructor
     *
     * @param userRoleRepository User role repository
     */
    public JpaUserRoleManagement(final UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public long count() {
        return userRoleRepository.count();
    }

    @Override
    @Transactional
    @Retryable(include = {
            ConcurrencyFailureException.class }, maxAttempts = Constants.TX_RT_MAX, backoff = @Backoff(delay = Constants.TX_RT_DELAY))
    public UserRole create(final UserRoleCreate create) {
        final JpaUserRoleCreate userRoleCreate = (JpaUserRoleCreate) create;
        return userRoleRepository.save(userRoleCreate.build());
    }

    @Override
    @Transactional
    @Retryable(include = {
            ConcurrencyFailureException.class }, maxAttempts = Constants.TX_RT_MAX, backoff = @Backoff(delay = Constants.TX_RT_DELAY))
    public List<UserRole> create(final Collection<UserRoleCreate> creates) {
        return creates.stream().map(this::create).collect(Collectors.toList());
    }

    @Override
    public Page<UserRole> findAll(final Pageable pageable) {
        return JpaManagementHelper.findAllWithCountBySpec(userRoleRepository, pageable, null);
    }

    @Override
    public Optional<UserRole> get(final long id) {
        return userRoleRepository.findById(id).map(userRole -> userRole);
    }

    @Override
    public Optional<UserRole> get(final String name) {
        return userRoleRepository.findByName(name).map(userRole -> userRole);
    }

    @Override
    @Transactional
    @Retryable(include = {
            ConcurrencyFailureException.class }, maxAttempts = Constants.TX_RT_MAX, backoff = @Backoff(delay = Constants.TX_RT_DELAY))
    public void delete(final Long userRoleId) {
        throwExceptionIfUserRoleDoesNotExist(userRoleId);

        userRoleRepository.deleteById(userRoleId);
    }

    @Override
    @Transactional
    @Retryable(include = {
            ConcurrencyFailureException.class }, maxAttempts = Constants.TX_RT_MAX, backoff = @Backoff(delay = Constants.TX_RT_DELAY))
    public UserRole update(final UserRoleUpdate update) {
        final GenericUserRoleUpdate userRoleUpdate = (GenericUserRoleUpdate) update;

        final JpaUserRole userRole = findUserRoleAndThrowExceptionIfNotFound(userRoleUpdate.getId());

        userRoleUpdate.getName().ifPresent((userRole::setName));
        userRoleUpdate.getDescription().ifPresent(userRole::setDescription);
        userRoleUpdate.getPrivileges().ifPresent(userRole::setPrivileges);

        return userRoleRepository.save(userRole);
    }

    private void throwExceptionIfUserRoleDoesNotExist(final Long typeId) {
        if (!userRoleRepository.existsById(typeId)) {
            throw new EntityNotFoundException(UserRole.class, typeId);
        }
    }

    private JpaUserRole findUserRoleAndThrowExceptionIfNotFound(final Long userRoleId) {
        return (JpaUserRole) get(userRoleId).orElseThrow(() -> new EntityNotFoundException(UserRole.class, userRoleId));
    }
}
