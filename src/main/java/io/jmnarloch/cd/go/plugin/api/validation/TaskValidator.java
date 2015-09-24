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
package io.jmnarloch.cd.go.plugin.api.validation;

import java.util.Map;

/**
 * Task configuration validator. Provides the base contract to be implemented by specific configuration validators.
 *
 * @author Jakub Narloch
 */
public interface TaskValidator {

    /**
     * The task validator.
     *
     * @param properties the properties to validate
     * @return the validation result
     */
    ValidationErrors validate(Map<String, Object> properties);
}
