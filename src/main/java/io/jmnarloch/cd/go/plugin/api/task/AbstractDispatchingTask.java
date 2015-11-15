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

import com.thoughtworks.go.plugin.api.AbstractGoPlugin;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Load;
import com.thoughtworks.go.plugin.api.annotation.UnLoad;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.info.PluginContext;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import io.jmnarloch.cd.go.plugin.api.dispatcher.ApiRequestDispatcher;
import io.jmnarloch.cd.go.plugin.api.dispatcher.ApiRequestDispatcherBuilder;
import io.jmnarloch.cd.go.plugin.api.parser.AbstractJsonParser;
import io.jmnarloch.cd.go.plugin.api.parser.gson.GsonParser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A base implementation of {@link GoPlugin} that instantiates the {@link ApiRequestDispatcher} that is being used for
 * dispatching the request and delegating the execution towards configured handlers.
 *
 * @author Jakub Narloch
 */
public abstract class AbstractDispatchingTask extends AbstractGoPlugin {

    /**
     * The task extension type.
     */
    private static final String TASK_EXTENSION = "task";

    /**
     * The logger instance by this class hierarchy.
     */
    protected final Logger logger = Logger.getLoggerFor(getClass());

    /**
     * The request dispatcher.
     */
    private ApiRequestDispatcher dispatcher;

    /**
     * The JSON parser.
     */
    private AbstractJsonParser parser;

    /**
     * Creates new instance of {@link AbstractDispatchingTask}.
     */
    public AbstractDispatchingTask() {
        this.parser = createParser();
    }

    /**
     * A on load plugin hook. This method will be invoked on successful plugin initialization.
     *
     * @param context the plugin context
     */
    @Load
    public void onLoad(PluginContext context) {
        // empty method
    }

    /**
     * A on unload plugin hook. This method will be invoked on successful plugin initialization.
     *
     * @param context the plugin context
     */
    @UnLoad
    public void onUnload(PluginContext context) {
        // empty method
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest requestMessage) throws UnhandledRequestTypeException {

        try {
            logger.info("Dispatching request: " + requestMessage.requestName());

            // dispatches the request to configured class
            return dispatcher().dispatch(requestMessage);
        } catch (Exception e) {
            logger.error("Unexpected error occurred when processing request.", e);

            return createErrorResponse(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier(TASK_EXTENSION, getSupportedExtensionVersions());
    }

    /**
     * Retrieves the dispatcher instance.
     *
     * @return the dispatcher instance
     */
    protected ApiRequestDispatcher dispatcher() {
        if (dispatcher == null) {
            dispatcher = buildDispatcher();
        }
        return dispatcher;
    }

    /**
     * Creates new instance of {@link AbstractJsonParser}.
     *
     * @return the json parser
     */
    protected AbstractJsonParser createParser() {
        return new GsonParser();
    }

    /**
     * Returns the supported extensions versions.
     *
     * @return the list of supported versions
     */
    protected List<String> getSupportedExtensionVersions() {
        return Collections.singletonList("1.0");
    }

    /**
     * A template method for building the concrete implementation of {@link ApiRequestDispatcher}.
     *
     * @return the request dispatcher instance
     */
    protected ApiRequestDispatcher buildDispatcher() {
        final ApiRequestDispatcherBuilder dispatcherBuilder = ApiRequestDispatcherBuilder.dispatch(parser);
        configureDispatcher(dispatcherBuilder);
        return dispatcherBuilder.build();
    }

    /**
     * Template method that can be used for configuring the dispatcher.
     *
     * @param dispatcherBuilder the dispatcher builder.
     */
    protected void configureDispatcher(ApiRequestDispatcherBuilder dispatcherBuilder) {
        // template method to be overridden by subclasses
    }

    /**
     * Creates the error response.
     *
     * @param e the exception
     * @return the error response
     */
    private GoPluginApiResponse createErrorResponse(Exception e) {
        final Map body = new HashMap();
        body.put("exception", e.getMessage());
        final DefaultGoPluginApiResponse response = new DefaultGoPluginApiResponse(DefaultGoApiResponse.INTERNAL_ERROR);
        response.setResponseBody(parser.toJson(body));
        return response;
    }
}
