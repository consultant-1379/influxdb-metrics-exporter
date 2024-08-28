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
package com.ericsson.oss.support.metrics.collection.integration.sonarqube

import com.ericsson.oss.support.metrics.collection.integration.sonarqube.response.MetricsResponse
import com.ericsson.oss.support.metrics.collection.integration.sonarqube.response.component.ComponentResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class SonarQubeDAO {

    @Autowired
    RestTemplate restTemplate

    private Logger logger = LoggerFactory.getLogger(SonarQubeDAO.class);

    private String sonarQubeUrl = "https://codeanalyzer2-staging.internal.ericsson.com"


    /**
     * gets the metric types supported by SonarQube
     */
    MetricsResponse getMetricTypes() {

        def restTemplate = getRestTemplate()
        MetricsResponse response = restTemplate.getForObject("${sonarQubeUrl}/api/metrics/search?ps={pageSize}", MetricsResponse.class, "500")

        logger.debug("Metrics found: {}", response.metrics*.key)
        return response

    }

    ComponentResponse getComponentMetrics(String projectKey, String...metrics) {
        def restTemplate = getRestTemplate()

        if (!metrics) {
            metrics = getMetricTypes().metrics[0..10]*.key
        }

        ComponentResponse response = restTemplate.getForObject(
                "${sonarQubeUrl}/api/measures/component?metricKeys={metrics}" +
                "&componentKey={projectKey}&additionalFields=periods,metrics", ComponentResponse.class,
                metrics.join(","), projectKey)

        logger.debug("Metrics found: {}", response.metrics*.key)
        return response
    }

    def getRestTemplate() {
        return restTemplate
    }

}
