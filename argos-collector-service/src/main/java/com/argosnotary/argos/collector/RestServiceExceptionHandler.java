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
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j(topic = "ExceptionHandler")
public class RestServiceExceptionHandler {

    @ExceptionHandler(value = {ArtifactCollectorValidationException.class})
    public ResponseEntity<Error> handleValidationError(ArtifactCollectorValidationException exception) {
        return ResponseEntity.badRequest().contentType(APPLICATION_JSON).body(createValidationError(exception));
    }

    @ExceptionHandler(value = {ArtifactCollectorException.class})
    public ResponseEntity<Error> handleArtifactCollectorError(ArtifactCollectorException exception) {
        return createErrorResponse(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Error> handleRuntimeException(RuntimeException e) {
        log.error("{}", e.getMessage(), e);
        return createErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Error> createErrorResponse(RuntimeException e, HttpStatus status) {
        return ResponseEntity.status(status).contentType(APPLICATION_JSON).body(new Error().message(e.getMessage()));
    }

    private Error createValidationError(ArtifactCollectorValidationException ex) {
        return new Error().message(String.join(", ", ex.getValidationMessages()));
    }
}
