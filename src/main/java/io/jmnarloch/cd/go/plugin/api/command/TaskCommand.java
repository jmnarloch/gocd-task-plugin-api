/**
 * Copyright (c) 2015 the original author or authors
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmnarloch.cd.go.plugin.api.command;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.task.JobConsoleLogger;
import io.jmnarloch.cd.go.plugin.api.exception.PluginException;
import io.jmnarloch.cd.go.plugin.api.executor.ExecutionConfiguration;
import io.jmnarloch.cd.go.plugin.api.executor.ExecutionContext;
import io.jmnarloch.cd.go.plugin.api.executor.ExecutionResult;
import io.jmnarloch.cd.go.plugin.api.executor.TaskExecutor;
import io.jmnarloch.cd.go.plugin.api.parser.AbstractJsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * The actual task execution command.
 *
 * @author Jakub Narloch
 */
public class TaskCommand extends BaseCommand {

    /**
     * The task executor.
     */
    private final TaskExecutor taskExecutor;

    /**
     * Creates new instance of {@link TaskCommand}.
     *
     * @param parser the JSON parser
     * @param taskExecutor task executor
     */
    public TaskCommand(AbstractJsonParser parser, TaskExecutor taskExecutor) {
        // TODO validate input
        super(parser);
        this.taskExecutor = taskExecutor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoPluginApiResponse execute(GoPluginApiRequest request) {

        final Map<String, Object> req = parseRequest(request);
        final ExecutionContext ctx = new ExecutionContext((Map) req.get("context"));
        final ExecutionConfiguration cfg = new ExecutionConfiguration((Map) req.get("config"));

        try {
            final ExecutionResult result = taskExecutor.execute(ctx, cfg, JobConsoleLogger.getConsoleLogger());

            final Map<String, Object> response = toMap(result);
            return createResponse(result.isSuccess() ? DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE
                    : DefaultGoPluginApiResponse.INTERNAL_ERROR, response);
        } catch (PluginException ex) {

            final Map<String, Object> response = new HashMap<>();
            response.put("exception", ex.getMessage());
            return createResponse(DefaultGoPluginApiResponse.INTERNAL_ERROR, response);
        }
    }

    private Map<String, Object> toMap(ExecutionResult result) {
        final Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        response.put("exception", result.getException());
        return response;
    }
}
