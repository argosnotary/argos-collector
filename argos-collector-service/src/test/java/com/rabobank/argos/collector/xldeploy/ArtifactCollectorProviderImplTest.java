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

import com.rabobank.argos.collector.ArtifactCollectorException;
import com.rabobank.argos.collector.rest.api.model.Artifact;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtifactCollectorProviderImplTest {
    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<String> urlArgumentCaptor;

    @Captor
    private ArgumentCaptor<HttpEntity> httpEntityArgumentCaptor;


    private ArtifactCollectorProviderImpl artifactCollectorProvider;
    @Mock
    private XLDeploySpecificationAdapter xlDeploySpecificationAdapter;

    @Mock
    private RestClientResponseException restClientResponseException;

    @BeforeEach
    void setup() {

        artifactCollectorProvider = new ArtifactCollectorProviderImpl(restTemplate);
        ReflectionTestUtils.setField(artifactCollectorProvider, "xlDeployBaseUrl", "http://localhost");
    }

    @Test
    void collectArtifactsShouldReturnArtifactList() {
        XLDeployResponse xlDeployResponse = new XLDeployResponse();
        Artifact artifact = new Artifact().hash("hash").uri("http://uit.nl");
        xlDeployResponse.setEntity(Collections.singletonList(artifact));
        when(xlDeploySpecificationAdapter.getApplicationName()).thenReturn("appName");
        when(xlDeploySpecificationAdapter.getVersion()).thenReturn("v1");
        when(xlDeploySpecificationAdapter.getUsername()).thenReturn("user");
        when(xlDeploySpecificationAdapter.getPassword()).thenReturn("pw");
        when(restTemplate.exchange(anyString(),
                any(HttpMethod.class),
                any(),
                ArgumentMatchers.<Class<XLDeployResponse>>any()))
                .thenReturn(ResponseEntity.ok(xlDeployResponse));
        List<Artifact> response = artifactCollectorProvider.collectArtifacts(xlDeploySpecificationAdapter);
        assertThat(response, hasSize(1));
        assertThat(response.get(0), sameInstance(artifact));
        verify(restTemplate).exchange(urlArgumentCaptor.capture(),
                any(HttpMethod.class), httpEntityArgumentCaptor.capture(),
                ArgumentMatchers.<Class<XLDeployResponse>>any());
        assertThat(urlArgumentCaptor.getValue(), is("http://localhost/api/extension/argosnotary/collectartifacts?application=appName&version=v1"));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders().containsKey("Authorization"), is(true));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders().containsKey("Accept"), is(true));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders().get("Accept").toString(), is("[application/json]"));
        assertThat(httpEntityArgumentCaptor.getValue().getHeaders().get("Authorization").toString(), is("[Basic dXNlcjpwdw==]"));
    }


    @Test
    void collectArtifactsWithIncorrectQueryParamsShouldReturnBadRequest() throws IOException {

        when(xlDeploySpecificationAdapter.getApplicationName()).thenReturn("appName");
        when(xlDeploySpecificationAdapter.getVersion()).thenReturn("v1");
        when(xlDeploySpecificationAdapter.getUsername()).thenReturn("user");
        when(xlDeploySpecificationAdapter.getPassword()).thenReturn("pw");
        String responseJson = IOUtils.toString(getClass().getResourceAsStream("/not-found-xldeploy.json"), UTF_8);
        when(restClientResponseException.getResponseBodyAsString()).thenReturn(responseJson);

        when(restTemplate.exchange(anyString(),
                any(HttpMethod.class),
                any(),
                ArgumentMatchers.<Class<XLDeployResponse>>any()))
                .thenThrow(restClientResponseException);

        ArtifactCollectorException exception = assertThrows(ArtifactCollectorException.class, () -> artifactCollectorProvider.collectArtifacts(xlDeploySpecificationAdapter));
        assertThat(exception.getMessage(), is("ERROR:root:ERROR: On Application [] version [] not found"));
    }

    @Test
    void collectArtifactsWithIncorrectCredentialsShouldReturnBadRequest() throws IOException {

        when(xlDeploySpecificationAdapter.getApplicationName()).thenReturn("appName");
        when(xlDeploySpecificationAdapter.getVersion()).thenReturn("v1");
        when(xlDeploySpecificationAdapter.getUsername()).thenReturn("user");
        when(xlDeploySpecificationAdapter.getPassword()).thenReturn("pw");
        String responseJson = IOUtils.toString(getClass().getResourceAsStream("/not-found-xldeploy.json"), UTF_8);
        when(restClientResponseException.getStatusText()).thenReturn("Unauthorized");
        when(restClientResponseException.getResponseBodyAsString()).thenReturn("");
        when(restTemplate.exchange(anyString(),
                any(HttpMethod.class),
                any(),
                ArgumentMatchers.<Class<XLDeployResponse>>any()))
                .thenThrow(restClientResponseException);

        ArtifactCollectorException exception = assertThrows(ArtifactCollectorException.class, () -> artifactCollectorProvider.collectArtifacts(xlDeploySpecificationAdapter));
        assertThat(exception.getMessage(), is("Unauthorized"));
    }
}