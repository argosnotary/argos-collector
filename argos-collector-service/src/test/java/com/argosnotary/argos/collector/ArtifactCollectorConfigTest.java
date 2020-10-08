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
package com.argosnotary.argos.collector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtifactCollectorConfigTest {

    @Mock
    private ConfigurableEnvironment configurableEnvironment;

    private ArtifactCollectorConfig artifactCollectorConfig;

    @BeforeEach
    void setUp() {
        artifactCollectorConfig = new ArtifactCollectorConfig(configurableEnvironment);
    }

    @Test
    void shouldReturnCorrectArtifactCollectorType() {
        when(configurableEnvironment.getActiveProfiles()).thenReturn(new String[]{"XLDEPLOY"});
        ArtifactCollectorType artifactCollectorType = artifactCollectorConfig.configuredArtifactCollectorType();
        assertThat(artifactCollectorType.name(), is("XLDEPLOY"));
    }

    @Test
    void shouldThrowArtifactCollectorException() {
        when(configurableEnvironment.getActiveProfiles()).thenReturn(new String[]{});
        ArtifactCollectorException artifactCollectorException =
                assertThrows(ArtifactCollectorException.class, () -> artifactCollectorConfig.configuredArtifactCollectorType());
        assertThat(artifactCollectorException.getMessage(), is("service should be running with one collector type configured"));
    }
}