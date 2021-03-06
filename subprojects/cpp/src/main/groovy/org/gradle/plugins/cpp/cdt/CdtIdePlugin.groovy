/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.plugins.cpp.cdt

import org.gradle.api.Project
import org.gradle.api.Plugin

import org.gradle.api.tasks.Delete

import org.gradle.plugins.cpp.cdt.model.ProjectSettings
import org.gradle.plugins.cpp.cdt.model.ProjectDescriptor
import org.gradle.plugins.cpp.cdt.tasks.GenerateMetadataFileTask

class CdtIdePlugin implements Plugin<Project> {

    void apply(Project project) {
        configureEclipseProject(project)
        
        project.task("cleanCdt", type: Delete) {
            delete ".project"
        }
    }
    
    private configureEclipseProject(Project project) {
        project.task("createCdtProjectFile", type: GenerateMetadataFileTask) {
            inputFile = project.file(".project")
            outputFile = project.file(".project")
            factory { new ProjectDescriptor() }
            onConfigure { new ProjectSettings(name: project.name).applyTo(it) }
        }
    }

}   