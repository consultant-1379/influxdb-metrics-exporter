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
package com.ericsson.oss.support.metrics.collection.model.sonar


import com.ericsson.oss.support.metrics.collection.model.sonar.metrics.SonarMetric
import lombok.Data

@Data
class SonarAnalysis {

    String projectKey
    String projectName
    String branch

    private metrics = [:]

    def getMetrics() {
        metrics.values()
    }

    void addMetric(SonarMetric metric) {
        metrics.put(metric.key, metric)
    }

    SonarMetric findMetric(String key) {
        metrics.get(key)
    }

}
