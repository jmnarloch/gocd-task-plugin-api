/**
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.cd.go.plugin.api.task;

import io.jmnarloch.cd.go.plugin.api.annotation.Configuration;
import io.jmnarloch.cd.go.plugin.api.annotation.Executor;
import io.jmnarloch.cd.go.plugin.api.annotation.Validator;
import io.jmnarloch.cd.go.plugin.api.annotation.View;
import io.jmnarloch.cd.go.plugin.api.config.AnnotatedEnumConfigurationProvider;
import io.jmnarloch.cd.go.plugin.api.dispatcher.ApiRequestDispatcherBuilder;
import io.jmnarloch.cd.go.plugin.api.exception.PluginException;
import io.jmnarloch.cd.go.plugin.api.executor.TaskExecutor;
import io.jmnarloch.cd.go.plugin.api.validation.TaskValidator;
import io.jmnarloch.cd.go.plugin.api.view.TaskView;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * An abstract annotation based dispatching task. It scans the specific base packages for annotated classes. It
 * automatically registers any {@link View}, {@link Executor}, {@link Validator} and {@link Configuration} classes.
 * Every class needs to implement a corresponding interface.
 *
 * @author Jakub Narloch
 */
public abstract class AbstractAnnotationDispatchingTask extends AbstractDispatchingTask {

    /**
     * The classpath scanning registry.
     */
    protected final Reflections reflections;

    /**
     * Creates new instance of {@link AbstractAnnotationDispatchingTask} with default package initialized to the base
     * class package.
     */
    protected AbstractAnnotationDispatchingTask() {
        reflections = new Reflections(getClass().getPackage().getName());
    }

    /**
     * Creates new instance of {@link AbstractAnnotationDispatchingTask} with specific base package to perform
     * classpath scanning.
     *
     * @param basePackage the base package
     */
    protected AbstractAnnotationDispatchingTask(String basePackage) {
        reflections = new Reflections(basePackage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void configureDispatcher(ApiRequestDispatcherBuilder dispatcherBuilder) {

        final TaskExecutor executor = getSingleInstance(Executor.class, TaskExecutor.class);
        if (executor != null) {
            dispatcherBuilder.toExecutor(executor);
        }

        final TaskView view = getSingleInstance(View.class, TaskView.class);
        if (view != null) {
            dispatcherBuilder.toView(view);
        }

        final TaskValidator validator = getSingleInstance(Validator.class, TaskValidator.class);
        if (validator != null) {
            dispatcherBuilder.toValidator(validator);
        }

        final Class<?> configuration = getSingleType(Configuration.class);
        if (configuration != null) {
            verifyRequiredType(configuration, Configuration.class, Enum.class);
            dispatcherBuilder.toConfiguration(new AnnotatedEnumConfigurationProvider<>((Class<? extends Enum>) configuration));
        }
    }

    /**
     * Retrieves the single object instance.
     *
     * @param annotation   the annotation
     * @param expectedType the expected type
     * @param <A>          the annotation type
     * @param <T>          the expected type
     * @return the single instance or null
     * @throws PluginException if more then one type has been annotated with {@code annotation} or when the instance
     *                         type does not implemented the {@code expectedType}
     */
    @SuppressWarnings("unchecked")
    protected <A extends Annotation, T> T getSingleInstance(Class<A> annotation, Class<T> expectedType) {
        try {
            final Class<?> type = getSingleType(annotation);
            if (type != null) {
                verifyRequiredType(type, annotation, expectedType);
                return (T) type.newInstance();
            }
            return null;
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Could not instantiate required type", e);
            throw new PluginException("Could not instantiate required type", e);
        }
    }

    /**
     * Retrieves the single instance type.
     *
     * @param annotation the annotation
     * @return the single type
     * @throws PluginException if any error occurs
     */
    protected Class<?> getSingleType(Class<? extends Annotation> annotation) {
        final Set<Class<?>> types = reflections.getTypesAnnotatedWith(annotation);
        if (types.size() > 0) {
            throw new PluginException(String.format(
                    "Could not instantiate %s types more then one has been registered: %s",
                    annotation.getSimpleName(), types.toString()));
        }
        if (!types.isEmpty()) {
            return types.iterator().next();
        }
        return null;
    }

    /**
     * Verifies that the given class implements specific base type.
     *
     * @param type         the actual type
     * @param annotation   the annotation type used for discovery
     * @param expectedType the expected type
     * @param <A>          the annotation type
     * @param <T>          the expected type
     */
    private <A extends Annotation, T> void verifyRequiredType(Class<?> type, Class<A> annotation, Class<T> expectedType) {
        if (expectedType.isAssignableFrom(type)) {
            throw new PluginException(String.format(
                    "The type %s annotated with %s does not implement the required type %s",
                    type.getName(),
                    annotation.getSimpleName(),
                    expectedType.getSimpleName()
            ));
        }
    }
}
