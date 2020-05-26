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

import com.rabobank.argos.collector.rest.api.model.ValidationError;
import com.rabobank.argos.collector.rest.api.model.ValidationMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestServiceExceptionHandlerTest {
    @Mock
    private ArtifactCollectorValidationException artifactCollectorValidationException;

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
                .thenReturn(Collections.singletonList(new ValidationMessage().field("field").message("message")));
        ResponseEntity<ValidationError> errorResponseEntity = restServiceExceptionHandler.handleValidationError(artifactCollectorValidationException);
        assertThat(errorResponseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(errorResponseEntity.getBody().getMessages(), hasSize(1));
    }

    @Test
    void handleRuntimeExceptionShouldReturnServerError() {
        ResponseEntity responseEntity = restServiceExceptionHandler.handleRuntimeException(runtimeException);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}