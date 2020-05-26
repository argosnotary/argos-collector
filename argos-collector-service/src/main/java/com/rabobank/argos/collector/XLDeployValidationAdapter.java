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
package com.rabobank.argos.collector;

import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Map;

@RequiredArgsConstructor
public class XLDeployValidationAdapter implements ValidationAdapter {
    private static final String CHARACTERS_ARE_NOT_ALLOWED = "(no `/`, `\\`, `:`, `[`, `]`, `|`, `,` or `*`) characters are allowed";
    private final Map<String, String> collectorSpecification;

    @NotNull
    @Pattern(regexp = "^[^/\\\\:\\[\\]|,*]]*$", message = CHARACTERS_ARE_NOT_ALLOWED)
    @Size(max = 255)
    public String getVersion() {
        return collectorSpecification
                .getOrDefault("version", null);
    }

    @NotNull
    @Pattern(regexp = "^[^/\\\\:\\[\\]|,*]]*$", message = CHARACTERS_ARE_NOT_ALLOWED)
    @Size(max = 255)
    public String getApplicationName() {
        return collectorSpecification
                .getOrDefault("applicationName", null);
    }

    @NotNull
    public String getUser() {
        return collectorSpecification
                .getOrDefault("user", null);
    }

    @NotNull
    public String getPassword() {
        return collectorSpecification
                .getOrDefault("password", null);
    }
}
