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

import com.argosnotary.argos.collector.rest.api.model.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestServiceExceptionHandlerTest {
    @Mock
    private ArtifactCollectorValidationException artifactCollectorValidationException;
    @Mock
    private ArtifactCollectorException artifactCollectorException;
    @Mock
    private RuntimeException runtimeException;

    private RestServiceExceptionHandler restServiceExceptionHandler;

    @BeforeEach
    void setup() {
        restServiceExceptionHandler = new RestServiceExceptionHandler();
    }

    @Test
    void handleValidationErrorShouldReturnBadRequest() {
        when(artifactCollectorValidationException.getValidationMessages())
                .thenReturn(List.of("message 1", "message 2"));
        ResponseEntity<Error> errorResponseEntity = restServiceExceptionHandler.handleValidationError(artifactCollectorValidationException);
        assertThat(errorResponseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(errorResponseEntity.getBody().getMessage(), is("message 1, message 2"));
    }

    @Test
    void handleArtifactCollectorErrorWithTypeBadRequestShouldReturnBadRequest() {
        when(artifactCollectorException.getMessage()).thenReturn("message");
        ResponseEntity<Error> errorResponseEntity = restServiceExceptionHandler.handleArtifactCollectorError(artifactCollectorException);
        assertThat(errorResponseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(errorResponseEntity.getBody().getMessage(), is("message"));
    }

    @Test
    void handleRuntimeExceptionShouldReturnServerError() {
        when(runtimeException.getMessage()).thenReturn("message");
        ResponseEntity<Error> responseEntity = restServiceExceptionHandler.handleRuntimeException(runtimeException);
        assertThat(responseEntity.getBody().getMessage(), is("message"));
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}