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
package io.jmnarloch.cd.go.plugin.api.command;

import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import io.jmnarloch.cd.go.plugin.api.exception.PluginException;
import io.jmnarloch.cd.go.plugin.api.parser.AbstractJsonParser;
import io.jmnarloch.cd.go.plugin.api.view.TaskView;

import java.util.HashMap;
import java.util.Map;

/**
 * The view command.
 *
 * @author Jakub Narloch
 */
public class ViewCommand extends BaseCommand {

    /**
     * The task view.
     */
    private final TaskView taskView;

    /**
     * Creates new instance of {@link TaskView}.
     *
     * @param parser the JSON parser
     * @param taskView the task view
     */
    public ViewCommand(AbstractJsonParser parser, TaskView taskView) {
        // TODO check input
        super(parser);
        this.taskView = taskView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoPluginApiResponse execute(GoPluginApiRequest request) {

        int responseCode = DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;
        final Map<String, Object> response = new HashMap<>();
        try {
            response.put("displayValue", taskView.displayValue());
            response.put("template", taskView.template());
        } catch(PluginException ex) {

            responseCode = DefaultGoApiResponse.INTERNAL_ERROR;
            response.put("exception", ex.getMessage());
        }
        return createResponse(responseCode, response);
    }
}
