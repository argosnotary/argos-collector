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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.await;

@Slf4j
public class ServiceStatusHelper {
    private ServiceStatusHelper() {
    }

    private static Properties properties = Properties.getInstance();

    public static void waitForXlDeployCollectorServiceToStart() {
        log.info("Waiting for argos xldeploy collector service start");
        HttpClient client = HttpClient.newHttpClient();
        await().atMost(1, MINUTES).until(() -> {
            try {
                log.debug(properties.getApiXLDeployBaseUrl());
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(properties.getApiXLDeployBaseUrl() + "/actuator/health"))
                        .build();
                HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
                return 200 == send.statusCode();
            } catch (IOException e) {
                //ignore
                return false;
            }
        });

        log.info("argos xldeploy collector service started");
    }

}
