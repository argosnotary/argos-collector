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

import com.rabobank.argos.collector.rest.api.model.ValidationMessage;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public enum ArtifactCollectorType {
    XLDEPLOY(XLDeployValidationAdapter.class);
    private Class<? extends ValidationAdapter> validationAdapterClass;

    ArtifactCollectorType(Class<? extends ValidationAdapter> validationAdapterClass) {
        this.validationAdapterClass = validationAdapterClass;
    }

    public void validate(Map<String, String> collectorSpecification, Validator validator) {
        try {
            Constructor<? extends ValidationAdapter> constructor = validationAdapterClass.getConstructor(Map.class);
            assert constructor != null;
            ValidationAdapter validationAdapter = constructor.newInstance(collectorSpecification);
            List<ValidationMessage> validationMessages = validateInput(validator, validationAdapter);
            if (!validationMessages.isEmpty()) {
                throw new ArtifactCollectorValidationException(validationMessages);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ArtifactCollectorException("error while instantiating validator class", e);
        }
    }

    private List<ValidationMessage> validateInput(Validator validator, ValidationAdapter validationAdapter) {
        return validator.validate(validationAdapter).stream()
                .sorted(Comparator.comparing((ConstraintViolation<? extends ValidationAdapter> cv) -> cv.getPropertyPath().toString())
                        .thenComparing(ConstraintViolation::getMessage))
                .map(error -> new ValidationMessage()
                        .field(error.getPropertyPath().toString())
                        .message(error.getMessage())
                )
                .collect(Collectors.toList());
    }
}
