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

import com.ericsson.oss.support.metrics.collection.model.sonar.SonarAnalysis
import com.ericsson.oss.support.metrics.collection.model.sonar.metrics.DoubleMetric
import com.ericsson.oss.support.metrics.collection.model.sonar.metrics.IntegerMetric
import com.ericsson.oss.support.metrics.collection.model.sonar.metrics.SonarMetric
import org.influxdb.dto.Point
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.influxdb.InfluxDBTemplate
import org.springframework.stereotype.Service

import java.util.concurrent.TimeUnit

@Service
class MetricsService {

    @Autowired
    private InfluxDBTemplate<Point> influxDBTemplate

    private static Logger logger = LoggerFactory.getLogger(MetricsService)

    void saveSonarQubeMetrics(SonarAnalysis analysis, Map<String,String> tags) {

        analysis.metrics.each { SonarMetric metric ->

            if (metric.rawValue != null) {
                logger.info("Saving metric {}", metric.key)
                writePoint(metric, tags)
            }
        }
    }

    private void writePoint(SonarMetric metric, Map<String,String> tags) {

        final Point.Builder pointBuilder = Point.measurement(metric.key)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)

        if (metric.domain != null) {
            logger.info("   -> Domain: {}", metric.domain)
            pointBuilder.addField("domain", metric.domain)
        }

        if (metric.description != null) {
            logger.info("   -> Description: {}", metric.description)
            pointBuilder.addField("description", metric.description)
        }

        addValueField(metric, pointBuilder)
        addTags(tags, pointBuilder)
        influxDBTemplate.write(pointBuilder.build())
    }

    private void addTags(final Map<String, String> tags, Point.Builder pointBuilder) {
        tags.keySet().each { key ->
            pointBuilder.tag(key, tags.get(key))
        }
    }

    private void addValueField(SonarMetric metric, Point.Builder pointBuilder) {
        if (metric instanceof IntegerMetric) {
            logger.info("   -> value (Integer): {}", metric.rawValue)
            pointBuilder.addField("value", ((IntegerMetric)metric).value)

        } else if (metric instanceof DoubleMetric) {
            logger.info("   -> value (Double): {}", metric.rawValue)
            pointBuilder.addField("value", ((DoubleMetric)metric).value)

        } else {
            logger.info("   -> value (String): {}", metric.rawValue)
            pointBuilder.addField("value", metric.value as String)
        }
    }

}
