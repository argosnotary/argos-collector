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

import com.rabobank.argos.collector.ArtifactCollectorException;
import com.rabobank.argos.collector.rest.api.model.Artifact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GitArtifactCollectorTest {

    @Mock
    private GitSpecificationAdapter spec;
    private GitArtifactCollector gitArtifactCollector;

    @BeforeEach
    void setUp() {
        gitArtifactCollector = new GitArtifactCollector();
        ReflectionTestUtils.setField(gitArtifactCollector, "gitBaseUrl", "https://github.com");
    }

    @Test
    void collectArtifactsFromBranch() {
        when(spec.getRepository()).thenReturn("argosnotary/argos-collector");
        when(spec.getBranch()).thenReturn("master");
        List<Artifact> artifacts = gitArtifactCollector.collectArtifacts(spec);
        assertThat(artifacts.get(0).getHash(), is("32624a6e579e2efd2e3b87d4c83bd825ee82b546d52b352704f91b010c7fc770"));
        assertThat(artifacts.get(0).getUri(), is(".gitignore"));
    }

    @Test
    void collectArtifactsFromCommitHash() {
        when(spec.getRepository()).thenReturn("argosnotary/argos-collector");
        when(spec.getCommitHash()).thenReturn("b65f29a");
        List<Artifact> artifacts = gitArtifactCollector.collectArtifacts(spec);
        assertThat(artifacts.get(0).getHash(), is("dcc1d61ea0ca0dedf6456f467d4c832743f68ea0144ec638a740ab231e3e4ee2"));
        assertThat(artifacts.get(0).getUri(), is(".gitignore"));
    }

    @Test
    void collectArtifactsFromCommitHashNotExists() {
        when(spec.getRepository()).thenReturn("argosnotary/argos-collector");
        when(spec.getCommitHash()).thenReturn("b65f29x");
        ArtifactCollectorException exception = assertThrows(ArtifactCollectorException.class, () -> gitArtifactCollector.collectArtifacts(spec));
        assertThat(exception.getMessage(), is("b65f29x not found"));
    }

    @Test
    void collectArtifactsFromBranchNotExists() {
        when(spec.getRepository()).thenReturn("argosnotary/argos-collector");
        when(spec.getBranch()).thenReturn("branzz");
        ArtifactCollectorException exception = assertThrows(ArtifactCollectorException.class, () -> gitArtifactCollector.collectArtifacts(spec));
        assertThat(exception.getMessage(), is("refs/heads/branzz not found"));
    }

    @Test
    void collectArtifactsRepositoryDoesNotExits() {
        when(spec.getPassword()).thenReturn("pazz");
        when(spec.getUsername()).thenReturn("uzer");
        when(spec.getRepository()).thenReturn("argosnotary/argos-collectorzzz");
        ArtifactCollectorException exception = assertThrows(ArtifactCollectorException.class, () -> gitArtifactCollector.collectArtifacts(spec));
        assertThat(exception.getMessage(), is("https://github.com/argosnotary/argos-collectorzzz: not authorized"));
    }

    @Test
    void collectArtifactsFromTagNotExists() {
        when(spec.getRepository()).thenReturn("argosnotary/argos-collector");

        when(spec.getTag()).thenReturn("tagzz");
        ArtifactCollectorException exception = assertThrows(ArtifactCollectorException.class, () -> gitArtifactCollector.collectArtifacts(spec));
        assertThat(exception.getMessage(), is("refs/tags/tagzz not found"));
    }

    @Test
    void collectArtifactsWithUsernameAndPassword() {
        when(spec.getRepository()).thenReturn("argosnotary/argos-collector");
        when(spec.getBranch()).thenReturn("master");
        when(spec.getPassword()).thenReturn("pazz");
        when(spec.getUsername()).thenReturn("uzer");
        List<Artifact> artifacts = gitArtifactCollector.collectArtifacts(spec);
        assertThat(artifacts.get(0).getHash(), is("32624a6e579e2efd2e3b87d4c83bd825ee82b546d52b352704f91b010c7fc770"));
        assertThat(artifacts.get(0).getUri(), is(".gitignore"));
    }

}