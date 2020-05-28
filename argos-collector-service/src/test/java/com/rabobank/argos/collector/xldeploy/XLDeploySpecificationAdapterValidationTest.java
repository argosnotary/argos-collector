/*
 * Copyright (C) 2019 - 2020 Rabobank Nederland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rabobank.argos.collector.xldeploy;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.rabobank.argos.collector.ValidateHelper.expectedErrors;
import static com.rabobank.argos.collector.ValidateHelper.validate;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;


class XLDeploySpecificationAdapterValidationTest {

    private static final String APPLICATION_NAME = "applicationName";
    private static final String FIELD = "password";
    private static final String USER_NAME = "username";
    private static final String VERSION = "version";
    private static final String MUST_NOT_BE_NULL = "must not be null";

    @Test
    void validateNotEmpty() {
        Map<String, String> specification = emptyMap();
        assertThat(validate(new XLDeploySpecificationAdapter(specification)), contains(expectedErrors(
                APPLICATION_NAME, MUST_NOT_BE_NULL,
                FIELD, MUST_NOT_BE_NULL,
                USER_NAME, MUST_NOT_BE_NULL,
                VERSION, MUST_NOT_BE_NULL)
        ));
    }

    @Test
    void validateWrongCharacters() {
        Map<String, String> specification = Map.of(APPLICATION_NAME, "app*lication", USER_NAME, "user", FIELD, "pw", VERSION, "ver|ion");
        assertThat(validate(new XLDeploySpecificationAdapter(specification)), contains(expectedErrors(
                APPLICATION_NAME, "(no `/`, `\\`, `:`, `[`, `]`, `|`, `,` or `*`) characters are allowed",
                VERSION, "(no `/`, `\\`, `:`, `[`, `]`, `|`, `,` or `*`) characters are allowed"
                )
        ));
    }


}