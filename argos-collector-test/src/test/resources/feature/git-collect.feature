#
# Copyright (C) 2020 Argos Notary Coöperatie UA
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
    * url karate.properties['server.git.baseurl']
    * configure headers = {Content-Type: 'application/json'}
    * print  karate.properties['server.git.baseurl']

  Scenario: collect artifacts on git with valid request should return a 200
    Given path '/api/collector/artifacts'
    And request {repository:'argosnotary/argos-collector', commitHash:'39955'}
    When method POST
    Then status 200
    * def expectedResponse = read('classpath:testmessages/git/ok-response.json')
    And match response == expectedResponse

  Scenario: collect artifacts on collect with incorrect request should return a 400
    Given path '/api/collector/artifacts'
    And request {repository:'argos\notary/argos-collector', commitHash:'b65f29a'}
    When method POST
    Then status 400
    * def expectedResponse = read('classpath:testmessages/git/bad-request-not-okay.json')
    And match response == expectedResponse

  Scenario: collect artifacts on xldeploy with incorrect credentials should return a 400
    Given path '/api/collector/artifacts'
    And request {repository:'argosnotary/argos-collectorzzzzz', commitHash:'b65f29a', username:'uzzzer', password:'pazzzzword'}
    When method POST
    Then status 400
    * def expectedResponse = read('classpath:testmessages/git/bad-request-not-authorized.json')
    And match response == expectedResponse



