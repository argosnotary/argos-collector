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

import com.rabobank.argos.collector.ArtifactCollectorProvider;
import com.rabobank.argos.collector.XLDeploySpecificationAdapter;
import com.rabobank.argos.collector.rest.api.model.Artifact;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@Slf4j
@Profile(ArtifactCollectorProviderImpl.XLDEPLOY)
@RequiredArgsConstructor
public class ArtifactCollectorProviderImpl implements ArtifactCollectorProvider<XLDeploySpecificationAdapter> {
    static final String XLDEPLOY = "XLDEPLOY";
    @Value("${argos-collector.collectortypes.xldeploy.baseurl}")
    private String xlDeployBaseUrl;
    private final RestTemplate restTemplate;

    @Override
    public List<Artifact> collectArtifacts(XLDeploySpecificationAdapter specificationAdapter) {
        log.info("collecting artifacts with :" + XLDEPLOY + " collector");
        HttpHeaders headers = createHeaders(specificationAdapter);
        HttpEntity request = new HttpEntity(headers);
        String xlDeployUrl = createResourceUrl(specificationAdapter);
        ResponseEntity<XLDeployResponse> response = restTemplate.exchange(xlDeployUrl, HttpMethod.GET, request, XLDeployResponse.class);
        return response.getBody().getEntity();
    }

    private String createResourceUrl(XLDeploySpecificationAdapter specificationAdapter) {
        return UriComponentsBuilder.fromHttpUrl(xlDeployBaseUrl + "/api/extension/argosnotary/collectartifacts")
                .queryParam("application", specificationAdapter.getApplicationName())
                .queryParam("version", specificationAdapter.getVersion()).toUriString();
    }

    private HttpHeaders createHeaders(XLDeploySpecificationAdapter specificationAdapter) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.setBasicAuth(specificationAdapter.getUserName(), specificationAdapter.getPassword());
        return headers;
    }

}
