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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtifactCollectorTypeTest {

    private static final String PATH = "path";
    private static final String MESSAGE = "message";
    private ArtifactCollectorType artifactCollectorType = ArtifactCollectorType.XLDEPLOY;
    @Mock
    private Path propertyPath;
    @Mock
    private Validator validator;
    @Mock
    private ConstraintViolation constraintViolation;

    @Mock
    private SpecificationAdapter specificationAdapter;


    @Test
    void createSpecificationAdapter() {
        SpecificationAdapter collectorTypeSpecificationAdapter = artifactCollectorType.createSpecificationAdapter(Collections.emptyMap());
        assertThat(collectorTypeSpecificationAdapter, instanceOf(XLDeploySpecificationAdapter.class));
    }

    @Test
    void validateWithConstraintViolationsShouldThrowException() {
        when(constraintViolation.getMessage()).thenReturn(MESSAGE);
        when(constraintViolation.getPropertyPath()).thenReturn(propertyPath);
        when(propertyPath.toString()).thenReturn(PATH);
        when(validator.validate(any())).thenReturn(Set.of(constraintViolation));
        ArtifactCollectorValidationException artifactCollectorValidationException = assertThrows(ArtifactCollectorValidationException.class,
                () -> artifactCollectorType.validate(specificationAdapter, validator));
        assertThat(artifactCollectorValidationException.getValidationMessages(), hasSize(1));
        assertThat(artifactCollectorValidationException.getValidationMessages().get(0), is(PATH + " : " + MESSAGE));
    }

    @Test
    void validate() {
        when(validator.validate(any())).thenReturn(Collections.emptySet());
        artifactCollectorType.validate(specificationAdapter, validator);
        verify(validator).validate(any());
    }
}