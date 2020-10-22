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
package com.argosnotary.argos.collector.xldeploy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.argosnotary.argos.collector.ArtifactCollectorException;
import com.argosnotary.argos.collector.ArtifactCollectorProvider;
import com.argosnotary.argos.collector.rest.api.model.Artifact;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Component
@Slf4j
@Profile(ArtifactCollectorProviderImpl.XLDEPLOY)
@RequiredArgsConstructor
public class ArtifactCollectorProviderImpl implements ArtifactCollectorProvider<XLDeploySpecificationAdapter> {
    static final String XLDEPLOY = "XLDEPLOY";
    @Value("${argos-collector.baseurl}")
    private String xlDeployBaseUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Artifact> collectArtifacts(XLDeploySpecificationAdapter specificationAdapter) {
        HttpHeaders headers = createHeaders(specificationAdapter);
        HttpEntity request = new HttpEntity(headers);
        String xlDeployUrl = createResourceUrl(specificationAdapter);
        log.info("collecting xldeploy artifacts with url [" + xlDeployUrl + "]");
        try {
            ResponseEntity<XLDeployResponse> response = restTemplate.exchange(xlDeployUrl, HttpMethod.GET, request, XLDeployResponse.class);
            return requireNonNull(response.getBody()).getEntity();
        } catch (RestClientResponseException e) {
            log.error("collecting xldeploy artifacts has response exception: [" + e.getMessage() + "]");
            String message;
            try {
                message = objectMapper
                        .readValue(e.getResponseBodyAsString(), XLDeployError.class)
                        .getStdout();
            } catch (JsonProcessingException ex) {
                message = e.getStatusText();
                log.debug(ex.getMessage());
            }
            throw new ArtifactCollectorException(message);
        }
    }

    private String createResourceUrl(XLDeploySpecificationAdapter specificationAdapter) {
        return UriComponentsBuilder.fromHttpUrl(xlDeployBaseUrl + "/api/extension/argosnotary/collectartifacts")
                .queryParam("application", specificationAdapter.getApplicationName())
                .queryParam("version", specificationAdapter.getApplicationVersion()).toUriString();
    }

    private HttpHeaders createHeaders(XLDeploySpecificationAdapter specificationAdapter) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.setBasicAuth(specificationAdapter.getUsername(), specificationAdapter.getPassword());
        return headers;
    }

}
