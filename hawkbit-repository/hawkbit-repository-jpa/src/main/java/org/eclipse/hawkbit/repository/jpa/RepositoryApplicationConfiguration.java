/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.validation.Validation;

import org.eclipse.hawkbit.artifact.repository.ArtifactRepository;
import org.eclipse.hawkbit.repository.ArtifactEncryption;
import org.eclipse.hawkbit.repository.ArtifactEncryptionSecretsStore;
import org.eclipse.hawkbit.repository.ArtifactEncryptionService;
import org.eclipse.hawkbit.repository.ArtifactManagement;
import org.eclipse.hawkbit.repository.BaseRepositoryTypeProvider;
import org.eclipse.hawkbit.repository.ConfirmationManagement;
import org.eclipse.hawkbit.repository.ControllerManagement;
import org.eclipse.hawkbit.repository.DeploymentManagement;
import org.eclipse.hawkbit.repository.DistributionSetManagement;
import org.eclipse.hawkbit.repository.DistributionSetTagManagement;
import org.eclipse.hawkbit.repository.DistributionSetTypeManagement;
import org.eclipse.hawkbit.repository.Email2faManagement;
import org.eclipse.hawkbit.repository.EmailService;
import org.eclipse.hawkbit.repository.EntityFactory;
import org.eclipse.hawkbit.repository.PrivilegeManagement;
import org.eclipse.hawkbit.repository.PropertiesQuotaManagement;
import org.eclipse.hawkbit.repository.QuotaManagement;
import org.eclipse.hawkbit.repository.RepositoryDefaultConfiguration;
import org.eclipse.hawkbit.repository.RepositoryProperties;
import org.eclipse.hawkbit.repository.RolloutApprovalStrategy;
import org.eclipse.hawkbit.repository.RolloutExecutor;
import org.eclipse.hawkbit.repository.RolloutGroupManagement;
import org.eclipse.hawkbit.repository.RolloutManagement;
import org.eclipse.hawkbit.repository.RolloutStatusCache;
import org.eclipse.hawkbit.repository.SoftwareModuleManagement;
import org.eclipse.hawkbit.repository.SoftwareModuleTypeManagement;
import org.eclipse.hawkbit.repository.SystemManagement;
import org.eclipse.hawkbit.repository.TargetFilterQueryManagement;
import org.eclipse.hawkbit.repository.TargetManagement;
import org.eclipse.hawkbit.repository.TargetTagManagement;
import org.eclipse.hawkbit.repository.TargetTypeManagement;
import org.eclipse.hawkbit.repository.TenantConfigurationManagement;
import org.eclipse.hawkbit.repository.TenantStatsManagement;
import org.eclipse.hawkbit.repository.UserManagement;
import org.eclipse.hawkbit.repository.UserRoleManagement;
import org.eclipse.hawkbit.repository.autoassign.AutoAssignExecutor;
import org.eclipse.hawkbit.repository.builder.DistributionSetBuilder;
import org.eclipse.hawkbit.repository.builder.DistributionSetTypeBuilder;
import org.eclipse.hawkbit.repository.builder.RolloutBuilder;
import org.eclipse.hawkbit.repository.builder.SoftwareModuleBuilder;
import org.eclipse.hawkbit.repository.builder.SoftwareModuleMetadataBuilder;
import org.eclipse.hawkbit.repository.builder.TargetBuilder;
import org.eclipse.hawkbit.repository.builder.TargetFilterQueryBuilder;
import org.eclipse.hawkbit.repository.builder.TargetTypeBuilder;
import org.eclipse.hawkbit.repository.builder.UserBuilder;
import org.eclipse.hawkbit.repository.builder.UserRoleBuilder;
import org.eclipse.hawkbit.repository.event.ApplicationEventFilter;
import org.eclipse.hawkbit.repository.event.remote.EventEntityManager;
import org.eclipse.hawkbit.repository.event.remote.EventEntityManagerHolder;
import org.eclipse.hawkbit.repository.event.remote.TargetPollEvent;
import org.eclipse.hawkbit.repository.jpa.aspects.ExceptionMappingAspectHandler;
import org.eclipse.hawkbit.repository.jpa.autoassign.AutoAssignChecker;
import org.eclipse.hawkbit.repository.jpa.autoassign.AutoAssignScheduler;
import org.eclipse.hawkbit.repository.jpa.autocleanup.AutoActionCleanup;
import org.eclipse.hawkbit.repository.jpa.autocleanup.AutoCleanupScheduler;
import org.eclipse.hawkbit.repository.jpa.autocleanup.CleanupTask;
import org.eclipse.hawkbit.repository.jpa.builder.JpaDistributionSetBuilder;
import org.eclipse.hawkbit.repository.jpa.builder.JpaDistributionSetTypeBuilder;
import org.eclipse.hawkbit.repository.jpa.builder.JpaRolloutBuilder;
import org.eclipse.hawkbit.repository.jpa.builder.JpaSoftwareModuleBuilder;
import org.eclipse.hawkbit.repository.jpa.builder.JpaSoftwareModuleMetadataBuilder;
import org.eclipse.hawkbit.repository.jpa.builder.JpaTargetBuilder;
import org.eclipse.hawkbit.repository.jpa.builder.JpaTargetFilterQueryBuilder;
import org.eclipse.hawkbit.repository.jpa.builder.JpaTargetTypeBuilder;
import org.eclipse.hawkbit.repository.jpa.builder.JpaUserBuilder;
import org.eclipse.hawkbit.repository.jpa.builder.JpaUserRoleBuilder;
import org.eclipse.hawkbit.repository.jpa.configuration.MultiTenantJpaTransactionManager;
import org.eclipse.hawkbit.repository.jpa.event.JpaEventEntityManager;
import org.eclipse.hawkbit.repository.jpa.executor.AfterTransactionCommitDefaultServiceExecutor;
import org.eclipse.hawkbit.repository.jpa.executor.AfterTransactionCommitExecutor;
import org.eclipse.hawkbit.repository.jpa.model.helper.AfterTransactionCommitExecutorHolder;
import org.eclipse.hawkbit.repository.jpa.model.helper.EntityInterceptorHolder;
import org.eclipse.hawkbit.repository.jpa.model.helper.SecurityTokenGeneratorHolder;
import org.eclipse.hawkbit.repository.jpa.model.helper.TenantAwareHolder;
import org.eclipse.hawkbit.repository.jpa.rollout.RolloutScheduler;
import org.eclipse.hawkbit.repository.jpa.rollout.condition.PauseRolloutGroupAction;
import org.eclipse.hawkbit.repository.jpa.rollout.condition.StartNextGroupRolloutGroupSuccessAction;
import org.eclipse.hawkbit.repository.jpa.rollout.condition.ThresholdRolloutGroupErrorCondition;
import org.eclipse.hawkbit.repository.jpa.rollout.condition.ThresholdRolloutGroupSuccessCondition;
import org.eclipse.hawkbit.repository.jpa.rsql.DefaultRsqlVisitorFactory;
import org.eclipse.hawkbit.repository.jpa.rsql.RsqlParserValidationOracle;
import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.DistributionSetType;
import org.eclipse.hawkbit.repository.model.Rollout;
import org.eclipse.hawkbit.repository.model.SoftwareModule;
import org.eclipse.hawkbit.repository.model.Target;
import org.eclipse.hawkbit.repository.model.TargetFilterQuery;
import org.eclipse.hawkbit.repository.model.TargetType;
import org.eclipse.hawkbit.repository.model.helper.EventPublisherHolder;
import org.eclipse.hawkbit.repository.model.helper.SystemManagementHolder;
import org.eclipse.hawkbit.repository.model.helper.SystemSecurityContextHolder;
import org.eclipse.hawkbit.repository.model.helper.TenantConfigurationManagementHolder;
import org.eclipse.hawkbit.repository.rsql.RsqlValidationOracle;
import org.eclipse.hawkbit.repository.rsql.RsqlVisitorFactory;
import org.eclipse.hawkbit.repository.rsql.RsqlVisitorFactoryHolder;
import org.eclipse.hawkbit.repository.rsql.VirtualPropertyReplacer;
import org.eclipse.hawkbit.security.HawkbitSecurityProperties;
import org.eclipse.hawkbit.security.SecurityTokenGenerator;
import org.eclipse.hawkbit.security.SystemSecurityContext;
import org.eclipse.hawkbit.security.TokenService;
import org.eclipse.hawkbit.security.JwtTokenService;
import org.eclipse.hawkbit.tenancy.TenantAware;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import com.google.common.collect.Maps;

/**
 * General configuration for hawkBit's Repository.
 *
 */
@EnableJpaRepositories(value = "org.eclipse.hawkbit.repository.jpa", repositoryFactoryBeanClass = CustomBaseRepositoryFactoryBean.class)
@EnableTransactionManagement
@EnableJpaAuditing
@EnableAspectJAutoProxy
@Configuration
@EnableScheduling
@EnableRetry
@EntityScan("org.eclipse.hawkbit.repository.jpa.model")
@PropertySource("classpath:/hawkbit-jpa-defaults.properties")
@Import({ RepositoryDefaultConfiguration.class, DataSourceAutoConfiguration.class,
        SystemManagementCacheKeyGenerator.class })
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class RepositoryApplicationConfiguration extends JpaBaseConfiguration {

    protected RepositoryApplicationConfiguration(final DataSource dataSource, final JpaProperties properties,
            final ObjectProvider<JtaTransactionManager> jtaTransactionManagerProvider) {
        super(dataSource, properties, jtaTransactionManagerProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    PauseRolloutGroupAction pauseRolloutGroupAction(final RolloutManagement rolloutManagement,
            final RolloutGroupRepository rolloutGroupRepository, final SystemSecurityContext systemSecurityContext) {
        return new PauseRolloutGroupAction(rolloutManagement, rolloutGroupRepository, systemSecurityContext);
    }

    @Bean
    @ConditionalOnMissingBean
    StartNextGroupRolloutGroupSuccessAction startNextRolloutGroupAction(
            final RolloutGroupRepository rolloutGroupRepository, final DeploymentManagement deploymentManagement,
            final SystemSecurityContext systemSecurityContext) {
        return new StartNextGroupRolloutGroupSuccessAction(rolloutGroupRepository, deploymentManagement,
                systemSecurityContext);
    }

    @Bean
    @ConditionalOnMissingBean
    ThresholdRolloutGroupErrorCondition thresholdRolloutGroupErrorCondition(final ActionRepository actionRepository) {
        return new ThresholdRolloutGroupErrorCondition(actionRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    ThresholdRolloutGroupSuccessCondition thresholdRolloutGroupSuccessCondition(
            final ActionRepository actionRepository) {
        return new ThresholdRolloutGroupSuccessCondition(actionRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    SystemManagementCacheKeyGenerator systemManagementCacheKeyGenerator() {
        return new SystemManagementCacheKeyGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    AfterTransactionCommitDefaultServiceExecutor afterTransactionCommitDefaultServiceExecutor() {
        return new AfterTransactionCommitDefaultServiceExecutor();
    }

    @Bean
    @ConditionalOnMissingBean
    RsqlValidationOracle rsqlValidationOracle() {
        return new RsqlParserValidationOracle();
    }

    @Bean
    @ConditionalOnMissingBean
    QuotaManagement staticQuotaManagement(final HawkbitSecurityProperties securityProperties) {
        return new PropertiesQuotaManagement(securityProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    RolloutStatusCache rolloutStatusCache(final TenantAware tenantAware) {
        return new RolloutStatusCache(tenantAware);
    }

    @Bean
    @ConditionalOnMissingBean
    ApplicationEventFilter applicationEventFilter(final RepositoryProperties repositoryProperties) {
        return e -> e instanceof TargetPollEvent && !repositoryProperties.isPublishTargetPollEvent();
    }

    /**
     * @param distributionSetTypeManagement
     *            to loading the {@link DistributionSetType}
     * @param softwareManagement
     *            for loading {@link DistributionSet#getModules()}
     * @return DistributionSetBuilder bean
     */
    @Bean
    DistributionSetBuilder distributionSetBuilder(final DistributionSetTypeManagement distributionSetTypeManagement,
            final SoftwareModuleManagement softwareManagement) {
        return new JpaDistributionSetBuilder(distributionSetTypeManagement, softwareManagement);
    }

    @Bean
    TargetBuilder targetBuilder(final TargetTypeManagement targetTypeManagement) {
        return new JpaTargetBuilder(targetTypeManagement);
    }

    /**
     * @param dsTypeManagement
     *            for loading
     *            {@link TargetType#getCompatibleDistributionSetTypes()}
     * @return TargetTypeBuilder bean
     */
    @Bean
    TargetTypeBuilder targetTypeBuilder(final DistributionSetTypeManagement dsTypeManagement) {
        return new JpaTargetTypeBuilder(dsTypeManagement);
    }

    @Bean
    SoftwareModuleMetadataBuilder softwareModuleMetadataBuilder(
            final SoftwareModuleManagement softwareModuleManagement) {
        return new JpaSoftwareModuleMetadataBuilder(softwareModuleManagement);
    }

    /**
     * @param softwareManagement
     *            for loading
     *            {@link DistributionSetType#getMandatoryModuleTypes()} and
     *            {@link DistributionSetType#getOptionalModuleTypes()}
     * @return DistributionSetTypeBuilder bean
     */
    @Bean
    DistributionSetTypeBuilder distributionSetTypeBuilder(
            final SoftwareModuleTypeManagement softwareModuleTypeManagement) {
        return new JpaDistributionSetTypeBuilder(softwareModuleTypeManagement);
    }

    /**
     * @param softwareModuleTypeManagement
     *            for loading {@link SoftwareModule#getType()}
     * @return SoftwareModuleBuilder bean
     */
    @Bean
    SoftwareModuleBuilder softwareModuleBuilder(final SoftwareModuleTypeManagement softwareModuleTypeManagement) {
        return new JpaSoftwareModuleBuilder(softwareModuleTypeManagement);
    }

    /**
     * @param distributionSetManagement
     *            for loading {@link Rollout#getDistributionSet()}
     * @return RolloutBuilder bean
     */
    @Bean
    RolloutBuilder rolloutBuilder(final DistributionSetManagement distributionSetManagement) {
        return new JpaRolloutBuilder(distributionSetManagement);
    }

    /**
     * @param distributionSetManagement
     *            for loading
     *            {@link TargetFilterQuery#getAutoAssignDistributionSet()}
     * @return TargetFilterQueryBuilder bean
     */
    @Bean
    TargetFilterQueryBuilder targetFilterQueryBuilder(final DistributionSetManagement distributionSetManagement) {
        return new JpaTargetFilterQueryBuilder(distributionSetManagement);
    }

    /**
     * @return the {@link SystemSecurityContext} singleton bean which make it
     *         accessible in beans which cannot access the service directly,
     *         e.g. JPA entities.
     */
    @Bean
    SystemSecurityContextHolder systemSecurityContextHolder() {
        return SystemSecurityContextHolder.getInstance();
    }

    /**
     * @return the {@link TenantConfigurationManagement} singleton bean which
     *         make it accessible in beans which cannot access the service
     *         directly, e.g. JPA entities.
     */
    @Bean
    TenantConfigurationManagementHolder tenantConfigurationManagementHolder() {
        return TenantConfigurationManagementHolder.getInstance();
    }

    /**
     * @return the {@link SystemManagementHolder} singleton bean which holds the
     *         current {@link SystemManagement} service and make it accessible
     *         in beans which cannot access the service directly, e.g. JPA
     *         entities.
     */
    @Bean
    SystemManagementHolder systemManagementHolder() {
        return SystemManagementHolder.getInstance();
    }

    /**
     * @return the {@link TenantAwareHolder} singleton bean which holds the
     *         current {@link TenantAware} service and make it accessible in
     *         beans which cannot access the service directly, e.g. JPA
     *         entities.
     */
    @Bean
    TenantAwareHolder tenantAwareHolder() {
        return TenantAwareHolder.getInstance();
    }

    /**
     * @return the {@link SecurityTokenGeneratorHolder} singleton bean which
     *         holds the current {@link SecurityTokenGenerator} service and make
     *         it accessible in beans which cannot access the service via
     *         injection
     */
    @Bean
    SecurityTokenGeneratorHolder securityTokenGeneratorHolder() {
        return SecurityTokenGeneratorHolder.getInstance();
    }

    /**
     * @return the singleton instance of the {@link EntityInterceptorHolder}
     */
    @Bean
    EntityInterceptorHolder entityInterceptorHolder() {
        return EntityInterceptorHolder.getInstance();
    }

    /**
     *
     * @return the singleton instance of the
     *         {@link AfterTransactionCommitExecutorHolder}
     */
    @Bean
    AfterTransactionCommitExecutorHolder afterTransactionCommitExecutorHolder() {
        return AfterTransactionCommitExecutorHolder.getInstance();
    }

    /**
     * Defines the validation processor bean.
     *
     * @return the {@link MethodValidationPostProcessor}
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        final MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(Validation.byDefaultProvider().configure()
                .addProperty(HibernateValidatorConfiguration.ALLOW_PARALLEL_METHODS_DEFINE_PARAMETER_CONSTRAINTS,
                        "true")
                .buildValidatorFactory().getValidator());
        return processor;
    }

    /**
     * @return {@link ExceptionMappingAspectHandler} aspect bean
     */
    @Bean
    ExceptionMappingAspectHandler createRepositoryExceptionHandlerAdvice() {
        return new ExceptionMappingAspectHandler();
    }

    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new EclipseLinkJpaVendorAdapter() {
            private final HawkBitEclipseLinkJpaDialect jpaDialect = new HawkBitEclipseLinkJpaDialect();

            @Override
            public EclipseLinkJpaDialect getJpaDialect() {
                return jpaDialect;
            }
        };
    }

    @Override
    protected Map<String, Object> getVendorProperties() {

        final Map<String, Object> properties = Maps.newHashMapWithExpectedSize(7);
        // Turn off dynamic weaving to disable LTW lookup in static weaving mode
        properties.put(PersistenceUnitProperties.WEAVING, "false");
        // needed for reports
        properties.put(PersistenceUnitProperties.ALLOW_NATIVE_SQL_QUERIES, "true");
        // flyway
        properties.put(PersistenceUnitProperties.DDL_GENERATION, "none");
        // Embeed into hawkBit logging
        properties.put(PersistenceUnitProperties.LOGGING_LOGGER, "JavaLogger");
        // Ensure that we flush only at the end of the transaction
        properties.put(PersistenceUnitProperties.PERSISTENCE_CONTEXT_FLUSH_MODE, "COMMIT");
        // Enable batch writing
        properties.put(PersistenceUnitProperties.BATCH_WRITING, "JDBC");
        // Batch size
        properties.put(PersistenceUnitProperties.BATCH_WRITING_SIZE, "500");

        return properties;
    }

    /**
     * {@link MultiTenantJpaTransactionManager} bean.
     *
     * @see org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration#transactionManager(ObjectProvider)
     * @return a new {@link PlatformTransactionManager}
     */
    @Override
    @Bean
    public PlatformTransactionManager transactionManager(
            final ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        return new MultiTenantJpaTransactionManager();
    }

    /**
     * {@link JpaSystemManagement} bean.
     *
     * @return a new {@link SystemManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    SystemManagement systemManagement(final JpaProperties properties) {
        return new JpaSystemManagement(properties);
    }

    /**
     * {@link JpaDistributionSetManagement} bean.
     *
     * @return a new {@link DistributionSetManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    DistributionSetManagement distributionSetManagement(final EntityManager entityManager,
            final DistributionSetRepository distributionSetRepository,
            final DistributionSetTagManagement distributionSetTagManagement, final SystemManagement systemManagement,
            final DistributionSetTypeManagement distributionSetTypeManagement, final QuotaManagement quotaManagement,
            final DistributionSetMetadataRepository distributionSetMetadataRepository,
            final TargetFilterQueryRepository targetFilterQueryRepository, final ActionRepository actionRepository,
            final EventPublisherHolder eventPublisherHolder, final TenantAware tenantAware,
            final VirtualPropertyReplacer virtualPropertyReplacer,
            final SoftwareModuleRepository softwareModuleRepository,
            final DistributionSetTagRepository distributionSetTagRepository,
            final AfterTransactionCommitExecutor afterCommit, final JpaProperties properties) {
        return new JpaDistributionSetManagement(entityManager, distributionSetRepository, distributionSetTagManagement,
                systemManagement, distributionSetTypeManagement, quotaManagement, distributionSetMetadataRepository,
                targetFilterQueryRepository, actionRepository, eventPublisherHolder, tenantAware,
                virtualPropertyReplacer, softwareModuleRepository, distributionSetTagRepository, afterCommit,
                properties.getDatabase());

    }

    /**
     * {@link JpaDistributionSetManagement} bean.
     *
     * @return a new {@link DistributionSetManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    DistributionSetTypeManagement distributionSetTypeManagement(
            final DistributionSetTypeRepository distributionSetTypeRepository,
            final SoftwareModuleTypeRepository softwareModuleTypeRepository,
            final DistributionSetRepository distributionSetRepository, final TargetTypeRepository targetTypeRepository,
            final VirtualPropertyReplacer virtualPropertyReplacer, final JpaProperties properties,
            final QuotaManagement quotaManagement) {
        return new JpaDistributionSetTypeManagement(distributionSetTypeRepository, softwareModuleTypeRepository,
                distributionSetRepository, targetTypeRepository, virtualPropertyReplacer, properties.getDatabase(),
                quotaManagement);
    }

    /**
     * {@link JpaTargetTypeManagement} bean.
     *
     * @return a new {@link TargetTypeManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    TargetTypeManagement targetTypeManagement(final TargetTypeRepository targetTypeRepository,
            final TargetRepository targetRepository, final DistributionSetTypeRepository distributionSetTypeRepository,
            final VirtualPropertyReplacer virtualPropertyReplacer, final JpaProperties properties,
            final QuotaManagement quotaManagement) {
        return new JpaTargetTypeManagement(targetTypeRepository, targetRepository, distributionSetTypeRepository,
                virtualPropertyReplacer, properties.getDatabase(), quotaManagement);
    }

    /**
     * {@link JpaTenantStatsManagement} bean.
     *
     * @return a new {@link TenantStatsManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    TenantStatsManagement tenantStatsManagement() {
        return new JpaTenantStatsManagement();
    }

    /**
     * {@link JpaTenantConfigurationManagement} bean.
     *
     * @return a new {@link TenantConfigurationManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    TenantConfigurationManagement tenantConfigurationManagement() {
        return new JpaTenantConfigurationManagement();
    }

    /**
     * {@link JpaTargetManagement} bean.
     *
     * @return a new {@link JpaTargetManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    TargetManagement targetManagement(final EntityManager entityManager, final QuotaManagement quotaManagement,
            final TargetRepository targetRepository, final TargetMetadataRepository targetMetadataRepository,
            final RolloutGroupRepository rolloutGroupRepository,
            final TargetFilterQueryRepository targetFilterQueryRepository,
            final TargetTypeRepository targetTypeRepository, final TargetTagRepository targetTagRepository,
            final EventPublisherHolder eventPublisherHolder, final TenantAware tenantAware,
            final AfterTransactionCommitExecutor afterCommit, final VirtualPropertyReplacer virtualPropertyReplacer,
            final JpaProperties properties, final DistributionSetManagement distributionSetManagement) {
        return new JpaTargetManagement(entityManager, distributionSetManagement, quotaManagement, targetRepository,
                targetTypeRepository, targetMetadataRepository, rolloutGroupRepository, targetFilterQueryRepository,
                targetTagRepository, eventPublisherHolder, tenantAware, afterCommit, virtualPropertyReplacer,
                properties.getDatabase());
    }

    /**
     * {@link JpaTargetFilterQueryManagement} bean.
     *
     * @param targetFilterQueryRepository
     *            holding {@link TargetFilterQuery} entities
     * @param targetManagement
     *            managing {@link Target} entities
     * @param virtualPropertyReplacer
     *            for RSQL handling
     * @param distributionSetManagement
     *            for auto assign DS access
     * @param quotaManagement
     *            to access quotas
     * @param properties
     *            JPA properties
     * @param tenantAware
     *            the {@link TenantAware} bean holding the tenant information
     *
     * @return a new {@link TargetFilterQueryManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    TargetFilterQueryManagement targetFilterQueryManagement(
            final TargetFilterQueryRepository targetFilterQueryRepository, final TargetManagement targetManagement,
            final VirtualPropertyReplacer virtualPropertyReplacer,
            final DistributionSetManagement distributionSetManagement, final QuotaManagement quotaManagement,
            final JpaProperties properties, final TenantConfigurationManagement tenantConfigurationManagement,
            final SystemSecurityContext systemSecurityContext, final TenantAware tenantAware) {
        return new JpaTargetFilterQueryManagement(targetFilterQueryRepository, targetManagement,
                virtualPropertyReplacer, distributionSetManagement, quotaManagement, properties.getDatabase(),
                tenantConfigurationManagement, systemSecurityContext, tenantAware);
    }

    /**
     * {@link JpaTargetTagManagement} bean.
     *
     * @return a new {@link TargetTagManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    TargetTagManagement targetTagManagement(final TargetTagRepository targetTagRepository,
            final TargetRepository targetRepository, final VirtualPropertyReplacer virtualPropertyReplacer,
            final JpaProperties properties) {
        return new JpaTargetTagManagement(targetTagRepository, targetRepository, virtualPropertyReplacer,
                properties.getDatabase());
    }

    /**
     * {@link JpaDistributionSetTagManagement} bean.
     *
     * @return a new {@link JpaDistributionSetTagManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    DistributionSetTagManagement distributionSetTagManagement(
            final DistributionSetTagRepository distributionSetTagRepository,
            final DistributionSetRepository distributionSetRepository,
            final VirtualPropertyReplacer virtualPropertyReplacer, final JpaProperties properties) {
        return new JpaDistributionSetTagManagement(distributionSetTagRepository, distributionSetRepository,
                virtualPropertyReplacer, properties.getDatabase());
    }

    /**
     * {@link JpaSoftwareModuleManagement} bean.
     *
     * @return a new {@link SoftwareModuleManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    SoftwareModuleManagement softwareModuleManagement(final EntityManager entityManager,
            final DistributionSetRepository distributionSetRepository,
            final SoftwareModuleRepository softwareModuleRepository,
            final SoftwareModuleMetadataRepository softwareModuleMetadataRepository,
            final SoftwareModuleTypeRepository softwareModuleTypeRepository, final AuditorAware<String> auditorProvider,
            final ArtifactManagement artifactManagement, final QuotaManagement quotaManagement,
            final VirtualPropertyReplacer virtualPropertyReplacer, final JpaProperties properties) {
        return new JpaSoftwareModuleManagement(entityManager, distributionSetRepository, softwareModuleRepository,
                softwareModuleMetadataRepository, softwareModuleTypeRepository, auditorProvider, artifactManagement,
                quotaManagement, virtualPropertyReplacer, properties.getDatabase());
    }

    /**
     * {@link JpaSoftwareModuleTypeManagement} bean.
     *
     * @return a new {@link SoftwareModuleTypeManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    SoftwareModuleTypeManagement softwareModuleTypeManagement(
            final DistributionSetTypeRepository distributionSetTypeRepository,
            final SoftwareModuleTypeRepository softwareModuleTypeRepository,
            final VirtualPropertyReplacer virtualPropertyReplacer,
            final SoftwareModuleRepository softwareModuleRepository, final JpaProperties properties) {
        return new JpaSoftwareModuleTypeManagement(distributionSetTypeRepository, softwareModuleTypeRepository,
                virtualPropertyReplacer, softwareModuleRepository, properties.getDatabase());
    }

    @Bean
    @ConditionalOnMissingBean
    RolloutExecutor rolloutExecutor(final RolloutTargetGroupRepository rolloutTargetGroupRepository,
            final EntityManager entityManager, final RolloutRepository rolloutRepository,
            final ActionRepository actionRepository, final RolloutGroupRepository rolloutGroupRepository,
            final AfterTransactionCommitExecutor afterCommit, final TenantAware tenantAware,
            final RolloutGroupManagement rolloutGroupManagement, final QuotaManagement quotaManagement,
            final DeploymentManagement deploymentManagement, final TargetManagement targetManagement,
            final EventPublisherHolder eventPublisherHolder, final PlatformTransactionManager txManager,
            final RolloutApprovalStrategy rolloutApprovalStrategy, final ApplicationContext context) {
        return new JpaRolloutExecutor(rolloutTargetGroupRepository, entityManager, rolloutRepository, actionRepository,
                rolloutGroupRepository, afterCommit, tenantAware, rolloutGroupManagement, quotaManagement,
                deploymentManagement, targetManagement, eventPublisherHolder, txManager, rolloutApprovalStrategy,
                context);
    }

    @Bean
    @ConditionalOnMissingBean
    RolloutManagement rolloutManagement(final TargetManagement targetManagement,
            final DistributionSetManagement distributionSetManagement, final EventPublisherHolder eventPublisherHolder,
            final VirtualPropertyReplacer virtualPropertyReplacer, final PlatformTransactionManager txManager,
            final TenantAware tenantAware, final LockRegistry lockRegistry, final JpaProperties properties,
            final RolloutApprovalStrategy rolloutApprovalStrategy,
            final TenantConfigurationManagement tenantConfigurationManagement,
            final SystemSecurityContext systemSecurityContext, final RolloutExecutor rolloutExecutor) {
        return new JpaRolloutManagement(targetManagement, distributionSetManagement, eventPublisherHolder,
                virtualPropertyReplacer, txManager, tenantAware, lockRegistry, properties.getDatabase(),
                rolloutApprovalStrategy, tenantConfigurationManagement, systemSecurityContext, rolloutExecutor);
    }

    /**
     * {@link DefaultRolloutApprovalStrategy} bean.
     *
     * @return a new {@link RolloutApprovalStrategy}
     */
    @Bean
    @ConditionalOnMissingBean
    RolloutApprovalStrategy rolloutApprovalStrategy(final UserDetailsService userDetailsService,
            final TenantConfigurationManagement tenantConfigurationManagement,
            final SystemSecurityContext systemSecurityContext) {
        return new DefaultRolloutApprovalStrategy(userDetailsService, tenantConfigurationManagement,
                systemSecurityContext);
    }

    /**
     * {@link JpaRolloutGroupManagement} bean.
     *
     * @return a new {@link RolloutGroupManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    RolloutGroupManagement rolloutGroupManagement(final RolloutGroupRepository rolloutGroupRepository,
            final RolloutRepository rolloutRepository, final ActionRepository actionRepository,
            final TargetRepository targetRepository, final EntityManager entityManager,
            final VirtualPropertyReplacer virtualPropertyReplacer, final RolloutStatusCache rolloutStatusCache,
            final JpaProperties properties) {
        return new JpaRolloutGroupManagement(rolloutGroupRepository, rolloutRepository, actionRepository,
                targetRepository, entityManager, virtualPropertyReplacer, rolloutStatusCache, properties.getDatabase());
    }

    /**
     * {@link JpaDeploymentManagement} bean.
     *
     * @return a new {@link DeploymentManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    DeploymentManagement deploymentManagement(final EntityManager entityManager,
            final ActionRepository actionRepository, final DistributionSetRepository distributionSetRepository,
            final DistributionSetManagement distributionSetManagement, final TargetRepository targetRepository,
            final ActionStatusRepository actionStatusRepository, final AuditorAware<String> auditorProvider,
            final EventPublisherHolder eventPublisherHolder, final AfterTransactionCommitExecutor afterCommit,
            final VirtualPropertyReplacer virtualPropertyReplacer, final PlatformTransactionManager txManager,
            final TenantConfigurationManagement tenantConfigurationManagement, final QuotaManagement quotaManagement,
            final SystemSecurityContext systemSecurityContext, final TenantAware tenantAware,
            final JpaProperties properties, final RepositoryProperties repositoryProperties) {
        return new JpaDeploymentManagement(entityManager, actionRepository, distributionSetManagement,
                distributionSetRepository, targetRepository, actionStatusRepository, auditorProvider,
                eventPublisherHolder, afterCommit, virtualPropertyReplacer, txManager, tenantConfigurationManagement,
                quotaManagement, systemSecurityContext, tenantAware, properties.getDatabase(), repositoryProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    ConfirmationManagement confirmationManagement(final TargetRepository targetRepository,
            final ActionRepository actionRepository, final ActionStatusRepository actionStatusRepository,
            final RepositoryProperties repositoryProperties, final QuotaManagement quotaManagement,
            final EntityFactory entityFactory) {
        return new JpaConfirmationManagement(targetRepository, actionRepository, actionStatusRepository,
                repositoryProperties, quotaManagement, entityFactory);
    }

    /**
     * {@link JpaControllerManagement} bean.
     *
     * @return a new {@link ControllerManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    ControllerManagement controllerManagement(final ScheduledExecutorService executorService,
            final ActionRepository actionRepository, final ActionStatusRepository actionStatusRepository,
            final QuotaManagement quotaManagement, final RepositoryProperties repositoryProperties) {
        return new JpaControllerManagement(executorService, actionRepository, actionStatusRepository, quotaManagement,
                repositoryProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    ArtifactManagement artifactManagement(final LocalArtifactRepository localArtifactRepository,
            final SoftwareModuleRepository softwareModuleRepository, final ArtifactRepository artifactRepository,
            final QuotaManagement quotaManagement, final TenantAware tenantAware) {
        return new JpaArtifactManagement(localArtifactRepository, softwareModuleRepository, artifactRepository,
                quotaManagement, tenantAware);
    }

    /**
     * {@link JpaEntityFactory} bean.
     *
     * @return a new {@link EntityFactory}
     */
    @Bean
    @ConditionalOnMissingBean
    EntityFactory entityFactory() {
        return new JpaEntityFactory();
    }

    /**
     * {@link EventEntityManagerHolder} bean.
     *
     * @return a new {@link EventEntityManagerHolder}
     */
    @Bean
    @ConditionalOnMissingBean
    EventEntityManagerHolder eventEntityManagerHolder() {
        return EventEntityManagerHolder.getInstance();
    }

    /**
     * {@link EventEntityManager} bean.
     *
     * @param aware
     *            the tenant aware
     * @param entityManager
     *            the entitymanager
     * @return a new {@link EventEntityManager}
     */
    @Bean
    @ConditionalOnMissingBean
    EventEntityManager eventEntityManager(final TenantAware aware, final EntityManager entityManager) {
        return new JpaEventEntityManager(aware, entityManager);
    }

    /**
     * {@link AutoAssignChecker} bean.
     *
     * @param targetFilterQueryManagement
     *            to get all target filter queries
     * @param targetManagement
     *            to get targets
     * @param deploymentManagement
     *            to assign distribution sets to targets
     * @param transactionManager
     *            to run transactions
     * @return a new {@link AutoAssignChecker}
     */
    @Bean
    @ConditionalOnMissingBean
    AutoAssignExecutor autoAssignExecutor(final TargetFilterQueryManagement targetFilterQueryManagement,
            final TargetManagement targetManagement, final DeploymentManagement deploymentManagement,
            final PlatformTransactionManager transactionManager, final TenantAware tenantAware) {
        return new AutoAssignChecker(targetFilterQueryManagement, targetManagement, deploymentManagement,
                transactionManager, tenantAware);
    }

    /**
     * {@link AutoAssignScheduler} bean.
     *
     * Note: does not activate in test profile, otherwise it is hard to test the
     * auto assign functionality.
     *
     * @param tenantAware
     *            to run as specific tenant
     * @param systemManagement
     *            to find all tenants
     * @param systemSecurityContext
     *            to run as system
     * @param autoAssignChecker
     *            to run a check as tenant
     * @param lockRegistry
     *            to lock the tenant for auto assignment
     * @return a new {@link AutoAssignChecker}
     */
    @Bean
    @ConditionalOnMissingBean
    // don't active the auto assign scheduler in test, otherwise it is hard to
    // test
    @Profile("!test")
    @ConditionalOnProperty(prefix = "hawkbit.autoassign.scheduler", name = "enabled", matchIfMissing = true)
    AutoAssignScheduler autoAssignScheduler(final SystemManagement systemManagement,
            final SystemSecurityContext systemSecurityContext, final AutoAssignExecutor autoAssignExecutor,
            final LockRegistry lockRegistry) {
        return new AutoAssignScheduler(systemManagement, systemSecurityContext, autoAssignExecutor, lockRegistry);
    }

    /**
     * {@link AutoActionCleanup} bean.
     *
     * @param deploymentManagement
     *            Deployment management service
     * @param configManagement
     *            Tenant configuration service
     *
     * @return a new {@link AutoActionCleanup} bean
     */
    @Bean
    CleanupTask actionCleanup(final DeploymentManagement deploymentManagement,
            final TenantConfigurationManagement configManagement) {
        return new AutoActionCleanup(deploymentManagement, configManagement);
    }

    /**
     * {@link AutoCleanupScheduler} bean.
     *
     * @param systemManagement
     *            to find all tenants
     * @param systemSecurityContext
     *            to run as system
     * @param lockRegistry
     *            to lock the tenant for auto assignment
     * @param cleanupTasks
     *            a list of cleanup tasks
     *
     * @return a new {@link AutoCleanupScheduler} bean
     */
    @Bean
    @ConditionalOnMissingBean
    @Profile("!test")
    @ConditionalOnProperty(prefix = "hawkbit.autocleanup.scheduler", name = "enabled", matchIfMissing = true)
    AutoCleanupScheduler autoCleanupScheduler(final SystemManagement systemManagement,
            final SystemSecurityContext systemSecurityContext, final LockRegistry lockRegistry,
            final List<CleanupTask> cleanupTasks) {
        return new AutoCleanupScheduler(systemManagement, systemSecurityContext, lockRegistry, cleanupTasks);
    }

    /**
     * {@link RolloutScheduler} bean.
     *
     * Note: does not activate in test profile, otherwise it is hard to test the
     * rollout handling functionality.
     *
     * @param systemManagement
     *            to find all tenants
     * @param rolloutManagement
     *            to run the rollout handler
     * @param systemSecurityContext
     *            to run as system
     * @return a new {@link RolloutScheduler} bean.
     */
    @Bean
    @ConditionalOnMissingBean
    @Profile("!test")
    @ConditionalOnProperty(prefix = "hawkbit.rollout.scheduler", name = "enabled", matchIfMissing = true)
    RolloutScheduler rolloutScheduler(final TenantAware tenantAware, final SystemManagement systemManagement,
            final RolloutManagement rolloutManagement, final SystemSecurityContext systemSecurityContext) {
        return new RolloutScheduler(systemManagement, rolloutManagement, systemSecurityContext);
    }

    /**
     * Creates the {@link RsqlVisitorFactory} bean.
     *
     * @return A new {@link RsqlVisitorFactory} bean.
     */
    @Bean
    @ConditionalOnMissingBean
    RsqlVisitorFactory rsqlVisitorFactory() {
        return new DefaultRsqlVisitorFactory();
    }

    /**
     * Obtains the {@link RsqlVisitorFactoryHolder} bean.
     *
     * @return The {@link RsqlVisitorFactoryHolder} singleton.
     */
    @Bean
    RsqlVisitorFactoryHolder rsqlVisitorFactoryHolder() {
        return RsqlVisitorFactoryHolder.getInstance();
    }

    /**
     * {@link JpaDistributionSetInvalidationManagement} bean.
     *
     * @return a new {@link JpaDistributionSetInvalidationManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    JpaDistributionSetInvalidationManagement distributionSetInvalidationManagement(
            final DistributionSetManagement distributionSetManagement, final RolloutManagement rolloutManagement,
            final DeploymentManagement deploymentManagement,
            final TargetFilterQueryManagement targetFilterQueryManagement, final PlatformTransactionManager txManager,
            final RepositoryProperties repositoryProperties, final TenantAware tenantAware,
            final LockRegistry lockRegistry) {
        return new JpaDistributionSetInvalidationManagement(distributionSetManagement, rolloutManagement,
                deploymentManagement, targetFilterQueryManagement, txManager, repositoryProperties, tenantAware,
                lockRegistry);
    }

    /**
     * Our default {@link BaseRepositoryTypeProvider} bean always provides the
     * NoCountBaseRepository
     *
     * @return a {@link BaseRepositoryTypeProvider} bean
     */
    @Bean
    @ConditionalOnMissingBean
    BaseRepositoryTypeProvider baseRepositoryTypeProvider() {
        return new NoCountBaseRepositoryTypeProvider();

    }

    /**
     * Default artifact encryption service bean that internally uses
     * {@link ArtifactEncryption} and {@link ArtifactEncryptionSecretsStore}
     * beans for {@link SoftwareModule} artifacts encryption/decryption
     *
     * @return a {@link ArtifactEncryptionService} bean
     */
    @Bean
    @ConditionalOnMissingBean
    ArtifactEncryptionService artifactEncryptionService() {
        return ArtifactEncryptionService.getInstance();
    }

    /**
     * {@link JpaPrivilegeManagement} bean.
     *
     * @return a new {@link PrivilegeManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    PrivilegeManagement privilegeManagement() {
        return new JpaPrivilegeManagement();
    }

    /**
     * {@link JpaUserRoleManagement} bean.
     *
     * @return a new {@link UserRoleManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    UserRoleManagement userRoleManagement(UserRoleRepository userRoleRepository) {
        return new JpaUserRoleManagement(userRoleRepository);
    }

    /**
     * @return {@link UserRoleBuilder} bean
     */
    @Bean
    UserRoleBuilder userRoleBuilder(PrivilegeManagement privilegeManagement) {
        return new JpaUserRoleBuilder(privilegeManagement);
    }

    /**
     * {@link JpaUserManagement} bean.
     *
     * @return a new {@link UserManagement}
     */
    @Bean
    @ConditionalOnMissingBean
    UserManagement userManagement(UserRepository userRepository) {
        return new JpaUserManagement(userRepository);
    }

    /**
     * @return {@link UserBuilder} bean
     */
    @Bean
    UserBuilder userBuilder(UserRoleManagement userRoleManagement) {
        return new JpaUserBuilder(userRoleManagement);
    }

    /**
     * @return {@link UserDetailsService} bean
     */
    @Bean
    UserDetailsService userDetailsService() {
        return new JpaUserDetailService();
    }

    /**
     * @return {@link PasswordEncoder} bean
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * @return {@link TokenService} bean
     */
    @Bean
    TokenService tokenService() {
        return new JwtTokenService();
    }

    /**
     * @return {@link Email2faManagement} bean
     */
    @Bean
    Email2faManagement email2faManagement() {
        return new Email2faService();
    }

    /**
     * @return {@link EmailService} bean
     */
    @Bean
    EmailService emailService() {
        return new EmailServiceImpl();
    }

    /**
     * @return {@link PasswordResetManagement} bean
     */
    @Bean
    PasswordResetService passwordResetManagement() {
        return new PasswordResetService();
    }
}
