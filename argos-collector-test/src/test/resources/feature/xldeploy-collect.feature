#
# Copyright (C) 2019 - 2020 Rabobank Nederland
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

Feature: XLDeploy

  Background:
    * url karate.properties['server.xldeploy.baseurl']
    * configure headers = {Content-Type: 'application/json'}
    * print  karate.properties['server.xldeploy.baseurl']

  Scenario: collect artifacts on xldeploy with valid request should return a 200
    Given path '/api/collector/artifacts'
    And request {applicationName:'application',version:'version',username:'admin',password:'adm1n'}
    When method POST
    Then status 200
    * def response = read('classpath:testmessages/xldeploy/ok-response.json')
    And match response contains response

  Scenario: collect artifacts on xldeploy with incorrect request should return a 400
    Given path '/api/collector/artifacts'
    And request {applicationName:'string',version:'string',username:'admin',password:'adm1n'}
    When method POST
    Then status 400
    * def response = read('classpath:testmessages/xldeploy/bad-request-not-found.json')
    And match response contains response

  Scenario: collect artifacts on xldeploy with incorrect credentials should return a 400
    Given path '/api/collector/artifacts'
    And request {applicationName:'string',version:'string',username:'wrong',password:'wrong'}
    When method POST
    Then status 400
    * def response = read('classpath:testmessages/xldeploy/bad-request-not-authorized.json')
    And match response contains response



