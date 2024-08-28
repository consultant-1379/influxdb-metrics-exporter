/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2019
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.support.metrics.collection.sonarqube

import com.ericsson.oss.support.metrics.collection.integration.sonarqube.SonarQubeDAO
import com.ericsson.oss.support.metrics.collection.service.SonarQubeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*
import static org.springframework.test.web.client.response.MockRestResponseCreators.*

import static org.hamcrest.Matchers.*

@SpringBootTest
class SonarQubeSpec extends Specification {

    @Autowired
    RestTemplate restTemplate

    @Autowired
    SonarQubeDAO sonarQubeDAO

    @Autowired
    SonarQubeService sonarQubeService

    MockRestServiceServer mockServer

    def setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate)
    }

    def "get available metrics"() {

        given: "mock rest service"
            mockServer.expect(ExpectedCount.once(),
                    requestTo(new URI("https://codeanalyzer2-staging.internal.ericsson.com/api/metrics/search?ps=500")))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getClass().getClassLoader().getResource('./all_metrics.json').text)
            )

        when: "get metrics"
            def response = sonarQubeDAO.getMetricTypes()

        then: "115 metrics should be found"
            response.metrics.size() == 115

    }

    def "get component analysis"() {

        given: "metric keys"
            def projectkey = "com.ericsson.oss.services:topologySearchService"

        and: "mock rest services"
            mockServer.expect(ExpectedCount.once(),
                    requestTo(new URI("https://codeanalyzer2-staging.internal.ericsson.com/api/metrics/search?ps=500")))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getClass().getClassLoader().getResource('./all_metrics.json').text)
            )

            mockServer.expect(ExpectedCount.once(),
                    requestTo(startsWith("https://codeanalyzer2-staging.internal.ericsson.com/api/measures/component")))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withStatus(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getClass().getClassLoader().getResource('./component_analysis.json').text)
            )

        when: "get metrics"
            def response = sonarQubeService.getLastAnalysis(projectkey, "master")

        then: "analysis should be found"
            response != null
    }

}
