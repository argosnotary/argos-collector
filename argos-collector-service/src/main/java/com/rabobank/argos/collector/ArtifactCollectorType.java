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

import com.rabobank.argos.collector.xldeploy.XLDeploySpecificationAdapter;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public enum ArtifactCollectorType {
    XLDEPLOY(XLDeploySpecificationAdapter.class);
    private Class<? extends SpecificationAdapter> validationAdapterClass;

    ArtifactCollectorType(Class<? extends SpecificationAdapter> validationAdapterClass) {
        this.validationAdapterClass = validationAdapterClass;
    }

    public <T extends SpecificationAdapter> T createSpecificationAdapter(Map<String, String> collectorSpecification) {
        try {
            Constructor<? extends SpecificationAdapter> constructor = validationAdapterClass.getConstructor(Map.class);
            assert constructor != null;
            return (T) constructor.newInstance(collectorSpecification);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ArtifactCollectorException("error while instantiating validator class", e);
        }
    }

    public <T extends SpecificationAdapter> void validate(T specificationAdapter, Validator validator) {
        List<String> validationMessages = validateInput(validator, specificationAdapter);
        if (!validationMessages.isEmpty()) {
            throw new ArtifactCollectorValidationException(validationMessages);
        }
    }

    private List<String> validateInput(Validator validator, SpecificationAdapter specificationAdapter) {
        return validator.validate(specificationAdapter).stream()
                .sorted(Comparator.comparing((ConstraintViolation<? extends SpecificationAdapter> cv) -> cv.getPropertyPath().toString())
                        .thenComparing(ConstraintViolation::getMessage))
                .map(error -> error.getPropertyPath().toString() + " : " + error.getMessage())
                .collect(Collectors.toList());
    }
}
