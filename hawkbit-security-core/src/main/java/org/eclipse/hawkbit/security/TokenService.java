/**
 * Copyright (c) 2023 Sabo Mobile IT
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.security;
    
import java.util.Map;

/**
 * Creates and validates credentials.
 */
public interface TokenService {

    /**
     * Create a permanent token
     * @param attributes
     * @return
     */
    String generate(final String tenant, final String username);

    /**
     * Checks the validity of the given credentials.
     *
     * @param token
     * @return attributes if verified
     */
    Map<String, String> verify(String token);
}