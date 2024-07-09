/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.model.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class ModifySqlLogFilter extends Filter<ILoggingEvent> {

    final Pattern pattern = Pattern.compile("^(UPDATE|INSERT|DELETE)", Pattern.CASE_INSENSITIVE);
    
    @Override
    public FilterReply decide(ILoggingEvent event) {
        Matcher matcher = pattern.matcher(event.getMessage());
        return matcher.find() ? FilterReply.NEUTRAL : FilterReply.DENY;
    }
}
