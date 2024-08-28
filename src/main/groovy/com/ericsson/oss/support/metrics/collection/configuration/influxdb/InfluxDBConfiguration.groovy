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
package com.ericsson.oss.support.metrics.collection.configuration.influxdb

import org.influxdb.dto.Point
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.influxdb.DefaultInfluxDBTemplate
import org.springframework.data.influxdb.InfluxDBConnectionFactory
import org.springframework.data.influxdb.InfluxDBProperties
import org.springframework.data.influxdb.InfluxDBTemplate
import org.springframework.data.influxdb.converter.PointConverter

@Configuration
@EnableConfigurationProperties(InfluxDBProperties.class)
class InfluxDBConfiguration {

    @Bean
    InfluxDBConnectionFactory connectionFactory(final InfluxDBProperties properties)
    {
        return new InfluxDBConnectionFactory(properties)
    }

    @Bean
    InfluxDBTemplate<Point> influxDBTemplate(final InfluxDBConnectionFactory connectionFactory)
    {
        /*
         * You can use your own 'PointCollectionConverter' implementation, e.g. in case
         * you want to use your own custom measurement object.
         */
        return new InfluxDBTemplate<>(connectionFactory, new PointConverter())
    }

    @Bean
    DefaultInfluxDBTemplate defaultTemplate(final InfluxDBConnectionFactory connectionFactory)
    {
        /*
         * If you are just dealing with Point objects from 'influxdb-java' you could
         * also use an instance of class DefaultInfluxDBTemplate.
         */
        return new DefaultInfluxDBTemplate(connectionFactory)
    }

}
