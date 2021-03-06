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



package org.gradle.api.internal.artifacts

import spock.lang.Specification
import org.gradle.util.Matchers

class ResolvedConfigurationIdentifierTest extends Specification {
    def equalsAndHashCode() {
        when:
        ResolvedConfigurationIdentifier id = new ResolvedConfigurationIdentifier('group', 'name', 'version', 'config')
        ResolvedConfigurationIdentifier same = new ResolvedConfigurationIdentifier('group', 'name', 'version', 'config')
        ResolvedConfigurationIdentifier differentGroup = new ResolvedConfigurationIdentifier('other', 'name', 'version', 'config')
        ResolvedConfigurationIdentifier differentName = new ResolvedConfigurationIdentifier('group', 'other', 'version', 'config')
        ResolvedConfigurationIdentifier differentVersion = new ResolvedConfigurationIdentifier('group', 'name', 'other', 'config')
        ResolvedConfigurationIdentifier differentConfig = new ResolvedConfigurationIdentifier('group', 'name', 'version', 'other')

        then:
        Matchers.strictlyEquals(id, same)
        id != differentGroup
        id != differentName
        id != differentVersion
        id != differentConfig
    }
}
