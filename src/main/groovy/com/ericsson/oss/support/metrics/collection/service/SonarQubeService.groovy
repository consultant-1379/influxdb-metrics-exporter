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
package com.ericsson.oss.support.metrics.collection.service

import com.ericsson.oss.support.metrics.collection.integration.sonarqube.SonarQubeDAO
import com.ericsson.oss.support.metrics.collection.model.sonar.SonarAnalysis
import com.ericsson.oss.support.metrics.collection.model.sonar.metrics.DoubleMetric
import com.ericsson.oss.support.metrics.collection.model.sonar.metrics.IntegerMetric
import com.ericsson.oss.support.metrics.collection.model.sonar.metrics.SonarMetric
import com.ericsson.oss.support.metrics.collection.model.sonar.metrics.StringMetric
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SonarQubeService {

    @Autowired
    SonarQubeDAO dao

    // These are manual metrics we don't use.
    // They will trigger a bad request because they have no value
    def metricsBlackList = ["development_cost","new_development_cost", "quality_gate_details",
                            "quality_profiles", "ncloc_language_distribution"]

    SonarAnalysis getLastAnalysis(String projectKey) {

        def metricsResponse = dao.getMetricTypes()
        def metricsPartitions = partition(metricsResponse.metrics*.key - metricsBlackList, 20)

        SonarAnalysis analysis = new SonarAnalysis()
        metricsPartitions.each { partition ->
            def componentResponse = dao.getComponentMetrics(projectKey, partition as String[])
            if (!analysis.projectKey) {
                analysis.projectKey = componentResponse.component?.key
                analysis.projectName = componentResponse.component?.name
            }

            componentResponse.metrics.each { metric ->
                analysis.addMetric(parseMetric(metric))
            }

            componentResponse.component.measures.each {measure ->
                def metric = analysis.findMetric(measure.metric)
                metric.rawValue = measure.value
            }
        }

        return analysis
    }

    private parseMetric(metric) {
        SonarMetric sonarMetric
        switch (metric.type) {
            case "INT":
                sonarMetric = new IntegerMetric(metric.key)
                break
            case "FLOAT":
            case "PERCENT":
                sonarMetric = new DoubleMetric(metric.key)
                break
            default:
                sonarMetric = new StringMetric(metric.key)
        }
        sonarMetric.name = metric.name
        sonarMetric.description = metric.description
        sonarMetric.domain = metric.domain

        return sonarMetric
    }

    private partition(array, size) {
        def partitions = []
        int partitionCount = array.size() / size

        partitionCount.times { partitionNumber ->
            def start = partitionNumber * size
            def end = start + size - 1
            partitions << array[start..end]
        }

        if (array.size() % size) partitions << array[partitionCount * size..-1]
        return partitions
    }

}
