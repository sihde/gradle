/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.logging;

import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.LoggingManager;

public interface LoggingManagerInternal extends LoggingManager, StandardOutputCapture {
    @Override
    LoggingManagerInternal start();

    @Override
    LoggingManagerInternal stop();

    @Override
    LoggingManagerInternal captureStandardOutput(LogLevel level);

    @Override
    LoggingManagerInternal captureStandardError(LogLevel level);

    @Override
    LoggingManagerInternal setLevel(LogLevel logLevel);
}