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

package org.gradle.tooling.internal.provider;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.tooling.internal.DefaultGradleProject;
import org.gradle.tooling.internal.DefaultGradleTask;
import org.gradle.tooling.internal.protocol.InternalGradleProject;
import org.gradle.tooling.internal.protocol.ProjectVersion3;
import org.gradle.tooling.model.GradleTask;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Builds the GradleProject that contains the project hierarchy and task information
 *
 * @author: Szczepan Faber, created at: 7/27/11
 */
public class GradleProjectBuilder implements BuildsModel {
    public boolean canBuild(Class type) {
        return type == InternalGradleProject.class;
    }

    public ProjectVersion3 buildAll(GradleInternal gradle) {
        DefaultGradleProject root = buildHierarchy(gradle.getRootProject());
        return root;
    }

    private DefaultGradleProject buildHierarchy(Project project) {
        List<DefaultGradleProject> children = new ArrayList<DefaultGradleProject>();
        for (Project child : project.getChildProjects().values()) {
            children.add(buildHierarchy(child));
        }

        DefaultGradleProject gradleProject = new DefaultGradleProject()
                .setPath(project.getPath())
                .setName(project.getName())
                .setDescription(project.getDescription())
                .setChildren((List) children);

        gradleProject.setTasks(tasks(gradleProject, project.getTasks()));

        for (DefaultGradleProject child : children) {
            child.setParent(gradleProject);
        }

        return gradleProject;
    }

    private List<GradleTask> tasks(DefaultGradleProject owner, TaskContainer tasks) {
        List<GradleTask> out = new LinkedList<GradleTask>();

        for (Task t : tasks) {
            out.add(new DefaultGradleTask()
                    .setPath(t.getPath())
                    .setName(t.getName())
                    .setDescription(t.getDescription())
                    .setProject(owner));
        }

        return out;
    }
}
