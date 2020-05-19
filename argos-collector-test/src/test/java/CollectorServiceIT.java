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

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.BeforeAll;

public class CollectorServiceIT {
    private static final String SERVER_XLDEPLOY_BASEURL = "server.xldeploy.baseurl";
    private static Properties properties = Properties.getInstance();

    @BeforeAll
    static void setup() {
        System.setProperty(SERVER_XLDEPLOY_BASEURL, properties.getApiXLDeployBaseUrl());
        ServiceStatusHelper.waitForXlDeployCollectorServiceToStart();
    }

    @Karate.Test
    Karate xldeployCollector() {
        return new Karate().feature("classpath:feature/xldeploy-collect.feature");
    }

}
