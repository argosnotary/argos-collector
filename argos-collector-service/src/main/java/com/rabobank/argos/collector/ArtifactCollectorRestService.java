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

import com.rabobank.argos.collector.rest.api.handler.ArtifactCollectorApi;
import com.rabobank.argos.collector.rest.api.model.Artifact;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.Validator;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ArtifactCollectorRestService implements ArtifactCollectorApi {
    private final ArtifactCollectorProvider artifactCollectorProvider;
    private final ArtifactCollectorType configuredArtifactCollectorType;
    private final Validator validator;
    @Override
    public ResponseEntity<List<Artifact>> collectArtifacts(@Valid Map<String, String> collectorSpecification) {
        SpecificationAdapter specificationAdapter = configuredArtifactCollectorType.createSpecificationAdapter(collectorSpecification);
        configuredArtifactCollectorType.validate(specificationAdapter, validator);
        List<Artifact> artifacts = artifactCollectorProvider.collectArtifacts(specificationAdapter);
        return ResponseEntity.ok(artifacts);
    }
}
