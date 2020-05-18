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
package com.rabobank.argos.collector.git;

import com.rabobank.argos.collector.ArtifactCollectorProvider;
import com.rabobank.argos.collector.rest.api.model.Artifact;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Profile(ArtifactCollectorProviderImpl.GIT)
public class ArtifactCollectorProviderImpl implements ArtifactCollectorProvider {
    static final String GIT = "GIT";

    @Override
    public List<Artifact> collectArtifacts(Map<String, String> collectorSpecification) {
        return Collections.singletonList(new Artifact()
                .uri("target/target.jar")
                .hash("61a0af2b177f02a14bab478e68d4907cda4dc3f642ade0432da8350ca199302b"));
    }

}
