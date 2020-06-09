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
package com.rabobank.argos.collector.git;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitSpecificationAdapterTest {

    @Mock
    private Map<String, String> map;
    private GitSpecificationAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new GitSpecificationAdapter(map);
    }

    @Test
    void getRepository() {
        when(map.getOrDefault("repository", null)).thenReturn("repository");
        assertThat(adapter.getRepository(), is("repository"));
    }

    @Test
    void getBranch() {
        when(map.getOrDefault("branch", null)).thenReturn("branch");
        assertThat(adapter.getBranch(), is("branch"));
    }

    @Test
    void getTag() {
        when(map.getOrDefault("tag", null)).thenReturn("tag");
        assertThat(adapter.getTag(), is("tag"));
    }

    @Test
    void getCommitHash() {
        when(map.getOrDefault("commitHash", null)).thenReturn("commitHash");
        assertThat(adapter.getCommitHash(), is("commitHash"));
    }

    @Test
    void getUsername() {
        when(map.getOrDefault("username", null)).thenReturn("username");
        assertThat(adapter.getUsername(), is("username"));
    }

    @Test
    void getPassword() {
        when(map.getOrDefault("password", null)).thenReturn("password");
        assertThat(adapter.getPassword(), is("password"));
    }
}