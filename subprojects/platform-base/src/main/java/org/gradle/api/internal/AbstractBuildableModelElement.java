/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.api.internal;

import org.gradle.api.BuildableModelElement;
import org.gradle.api.Task;
import org.gradle.api.internal.tasks.DefaultTaskDependency;
import org.gradle.api.tasks.TaskDependency;
import org.gradle.platform.base.component.internal.AbstractComponentSpec;
import org.gradle.platform.base.internal.ComponentSpecIdentifier;
import org.gradle.platform.base.internal.DefaultComponentSpecIdentifier;

import java.util.Collections;
import java.util.Set;

public abstract class AbstractBuildableModelElement extends AbstractComponentSpec implements BuildableModelElement {
    private final DefaultTaskDependency buildDependencies = new DefaultTaskDependency();
    private Task lifecycleTask;

    // This is here to allow the hierarchy to be transitioned
    // TODO - remove this constructor, use the other instead
    public AbstractBuildableModelElement() {
        super(new DefaultComponentSpecIdentifier("project", "name"), BuildableModelElement.class);
        throw new UnsupportedOperationException("Should not be using this constructor.");
    }

    public AbstractBuildableModelElement(ComponentSpecIdentifier identifier, Class<? extends BuildableModelElement> publicType) {
        super(identifier, publicType);
    }

    public Task getBuildTask() {
        return lifecycleTask;
    }

    public void setBuildTask(Task lifecycleTask) {
        this.lifecycleTask = lifecycleTask;
        lifecycleTask.dependsOn(buildDependencies);
    }

    public TaskDependency getBuildDependencies() {
        return new TaskDependency() {
            public Set<? extends Task> getDependencies(Task other) {
                if (lifecycleTask == null) {
                    return buildDependencies.getDependencies(other);
                }
                return Collections.singleton(lifecycleTask);
            }
        };
    }

    public void builtBy(Object... tasks) {
        buildDependencies.add(tasks);
    }

    public boolean hasBuildDependencies() {
        return buildDependencies.getDependencies(lifecycleTask).size() > 0;
    }
}
