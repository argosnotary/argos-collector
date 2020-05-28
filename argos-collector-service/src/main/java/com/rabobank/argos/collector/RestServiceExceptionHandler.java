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
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j(topic = "ExceptionHandler")
public class RestServiceExceptionHandler {

    @ExceptionHandler(value = {ArtifactCollectorValidationException.class})
    public ResponseEntity<ValidationError> handleValidationError(ArtifactCollectorValidationException exception) {
        return ResponseEntity.badRequest().contentType(APPLICATION_JSON).body(createValidationError(exception));
    }

    @ExceptionHandler(value = {ArtifactCollectorException.class})
    public ResponseEntity<ValidationError> handleArtifactCollectorError(ArtifactCollectorException exception) {
        if (ArtifactCollectorException.Type.BAD_REQUEST == exception.getExceptionType()) {
            return ResponseEntity.badRequest().contentType(APPLICATION_JSON).body(createValidationError(exception));
        } else {
            log.error("{}", exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ValidationError createValidationError(ArtifactCollectorException exception) {
        return new ValidationError()
                .addMessagesItem(new ValidationMessage()
                        .message(exception.getMessage()));
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity handleRuntimeException(RuntimeException e) {
        log.error("{}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    private ValidationError createValidationError(ArtifactCollectorValidationException ex) {
        ValidationError validationError = new ValidationError();
        List<ValidationMessage> validationMessages = new ArrayList<>(ex.getValidationMessages());
        validationError.setMessages(validationMessages);
        return validationError;
    }
}
