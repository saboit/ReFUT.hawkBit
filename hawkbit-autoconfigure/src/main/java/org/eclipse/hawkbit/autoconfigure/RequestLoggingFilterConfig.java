/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.HttpLogFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
public class RequestLoggingFilterConfig {

    @Bean
    public HttpLogFormatter jsonFormatter(
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") final ObjectMapper mapper) {
        return new UserJsonHttpLogFormatter(mapper);
    }
}
