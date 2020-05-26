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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.List;

import static java.util.Arrays.asList;

@Configuration
@RequiredArgsConstructor
public class ArtifactCollectorConfig {
    private final ConfigurableEnvironment environment;

    @Bean
    public ArtifactCollectorType configuredArtifactCollectorType() {
        List<ArtifactCollectorType> availableCollectorTypes = asList(ArtifactCollectorType.values());
        List<String> profiles = asList(environment.getActiveProfiles());
        return availableCollectorTypes
                .stream()
                .filter(artifactCollectorType -> profiles.contains(artifactCollectorType.name()))
                .findFirst()
                .orElseThrow(() -> new ArtifactCollectorException("service should be running with one collector type configured"));
    }

}
