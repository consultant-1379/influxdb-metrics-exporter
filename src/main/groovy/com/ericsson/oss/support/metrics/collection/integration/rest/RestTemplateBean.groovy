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
package com.ericsson.oss.support.metrics.collection.integration.rest

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.http.client.support.BasicAuthenticationInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class RestTemplateBean {

    private String token = "987750bcb0dc8753dcc130d67edbe155eaa70cca"

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.additionalInterceptors(new BasicAuthenticationInterceptor(token, "")).build()
    }

}
