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

import org.eclipse.hawkbit.repository.UserManagement;
import org.eclipse.hawkbit.repository.builder.GenericUserUpdate;
import org.eclipse.hawkbit.repository.builder.UserCreate;
import org.eclipse.hawkbit.repository.builder.UserUpdate;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.jpa.builder.JpaUserCreate;
import org.eclipse.hawkbit.repository.jpa.configuration.Constants;
import org.eclipse.hawkbit.repository.jpa.model.JpaUser;
import org.eclipse.hawkbit.repository.model.User;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * JPA implementation of {@link UserManagement}.
 *
 */
@Transactional(readOnly = true)
@Validated
public class JpaUserManagement implements UserManagement {

    private final UserRepository userRepository;

    /**
     * Constructor
     *
     * @param userRepository User repository
     */
    public JpaUserManagement(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    @Transactional
    @Retryable(include = {
            ConcurrencyFailureException.class }, maxAttempts = Constants.TX_RT_MAX, backoff = @Backoff(delay = Constants.TX_RT_DELAY))
    public User create(final UserCreate create) {
        final JpaUserCreate userCreate = (JpaUserCreate) create;

        return userRepository.save(userCreate.build());
    }

    @Override
    @Transactional
    @Retryable(include = {
            ConcurrencyFailureException.class }, maxAttempts = Constants.TX_RT_MAX, backoff = @Backoff(delay = Constants.TX_RT_DELAY))
    public List<User> create(final Collection<UserCreate> creates) {
        return creates.stream().map(this::create).collect(Collectors.toList());
    }

    @Override
    public Page<User> findAll(final Pageable pageable) {
        return JpaManagementHelper.findAllWithCountBySpec(userRepository, pageable, null);
    }

    @Override
    public Optional<User> get(final long id) {
        return userRepository.findById(id).map(user -> user);
    }

    @Override
    public Optional<User> get(final String username) {
        return userRepository.findByUsername(username).map(user -> user);
    }

    @Override
    public Optional<User> get(final String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email).map(user -> user);
    }

    @Override
    public Optional<User> getByPasswordResetHash(final String resetHash) {
        return userRepository.findByPasswordResetHash(resetHash).map(user -> user);
    }

    @Override
    @Transactional
    @Retryable(include = {
            ConcurrencyFailureException.class }, maxAttempts = Constants.TX_RT_MAX, backoff = @Backoff(delay = Constants.TX_RT_DELAY))
    public void delete(final Long userId) {
        throwExceptionIfUserDoesNotExist(userId);

        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    @Retryable(include = {
            ConcurrencyFailureException.class }, maxAttempts = Constants.TX_RT_MAX, backoff = @Backoff(delay = Constants.TX_RT_DELAY))
    public User update(final UserUpdate update) {
        final GenericUserUpdate userUpdate = (GenericUserUpdate)update;

        final JpaUser user = findUserAndThrowExceptionIfNotFound(userUpdate.getId());

        user.setUsername(userUpdate.getUsername());
        userUpdate.getEmail().ifPresent(user::setEmail);
        userUpdate.getFirstName().ifPresent(user::setFirstName);
        userUpdate.getLastName().ifPresent(user::setLastName);
        userUpdate.getPassword().ifPresent(user::setPassword);
        userUpdate.getLoginHash().ifPresent(user::setLoginHash);
        user.setUserRole(userUpdate.getUserRole());

        return userRepository.save(user);
    }

    @Override
    @Transactional
    @Retryable(include = {
            ConcurrencyFailureException.class }, maxAttempts = Constants.TX_RT_MAX, backoff = @Backoff(delay = Constants.TX_RT_DELAY))
    public User update(final User user) {
        return userRepository.save((JpaUser)user);
    }

    private void throwExceptionIfUserDoesNotExist(final Long typeId) {
        if (!userRepository.existsById(typeId)) {
            throw new EntityNotFoundException(User.class, typeId);
        }
    }

    private JpaUser findUserAndThrowExceptionIfNotFound(final Long userId) {
        return (JpaUser) get(userId).orElseThrow(() -> new EntityNotFoundException(User.class, userId));
    }
}