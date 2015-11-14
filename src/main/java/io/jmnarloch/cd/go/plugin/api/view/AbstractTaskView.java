/**
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.cd.go.plugin.api.view;

import io.jmnarloch.cd.go.plugin.api.exception.PluginException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * The base task view.
 *
 * @author Jakub Narloch
 */
public abstract class AbstractTaskView implements TaskView {

    /**
     * The view display name.
     */
    private final String displayValue;

    /**
     * The path to template file.
     */
    private final String templatePath;

    /**
     * Creates new instance of {@link AbstractTaskView} class.
     *
     * @param displayValue the display name
     * @param templatePath the path to the template file
     */
    public AbstractTaskView(String displayValue, String templatePath) {

        this.displayValue = displayValue;
        this.templatePath = templatePath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String displayValue() {
        return displayValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String template() {

        try (InputStream inputStream = getClass().getResourceAsStream(templatePath)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PluginException("The view template could not be loaded.", e);
        }
    }
}
