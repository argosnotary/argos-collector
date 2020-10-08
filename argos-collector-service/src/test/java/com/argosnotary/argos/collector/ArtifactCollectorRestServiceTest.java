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

import com.argosnotary.argos.collector.rest.api.model.Artifact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.Validator;
import java.util.List;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtifactCollectorRestServiceTest {

    private ArtifactCollectorType artifactCollectorType = ArtifactCollectorType.XLDEPLOY;

    private ArtifactCollectorRestService artifactCollectorRestService;

    @Mock
    private ArtifactCollectorProvider artifactCollectorProvider;

    @Mock
    private Validator validator;


    @BeforeEach
    void setUp() {
        artifactCollectorRestService = new ArtifactCollectorRestService(artifactCollectorProvider, artifactCollectorType, validator);
    }

    @Test
    void collectArtifactsShouldReturnOk() {
        List<Artifact> artifacts = List.of(new Artifact()
                .uri("/test/test.jar")
                .hash("hash"));
        when(validator.validate(any())).thenReturn(emptySet());
        when(artifactCollectorProvider.collectArtifacts(any())).thenReturn(artifacts);
        ResponseEntity<List<Artifact>> response = artifactCollectorRestService.collectArtifacts(emptyMap());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(artifacts));
        verify(artifactCollectorProvider).collectArtifacts(any());
        verify(validator).validate(any());
    }
}