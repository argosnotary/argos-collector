/*
 * Copyright (C) 2020 Argos Notary Coöperatie UA
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
package com.argosnotary.argos.collector.git;

import com.argosnotary.argos.collector.SpecificationAdapter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Map;

@RequiredArgsConstructor
public class GitSpecificationAdapter implements SpecificationAdapter {
    private final Map<String, String> collectorSpecification;

    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9_.\\-/]*$")
    @Size(max = 255)
    public String getRepository() {
        return collectorSpecification.getOrDefault("repository", null);
    }

    @Pattern(regexp = "^[^\\\\~^:?\\]*]*$")
    @Size(max = 255)
    public String getBranch() {
        return collectorSpecification.getOrDefault("branch", null);
    }

    @Size(max = 255)
    @Pattern(regexp = "^[^\\\\~^:?\\]*]*$")
    @Size(max = 255)
    public String getTag() {
        return collectorSpecification.getOrDefault("tag", null);
    }

    @Size(max = 255)
    @Pattern(regexp = "\\b[0-9a-f]{5,40}\\b")
    public String getCommitHash() {
        return collectorSpecification.getOrDefault("commitHash", null);
    }

    @Size(max = 255)
    public String getUsername() {
        return collectorSpecification.getOrDefault("username", null);
    }

    @Size(max = 255)
    public String getPassword() {
        return collectorSpecification.getOrDefault("password", null);
    }

}
