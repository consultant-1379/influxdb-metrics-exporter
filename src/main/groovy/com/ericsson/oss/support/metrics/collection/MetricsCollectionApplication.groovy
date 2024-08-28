package com.ericsson.oss.support.metrics.collection

import com.ericsson.oss.support.metrics.collection.service.MetricsService
import com.ericsson.oss.support.metrics.collection.service.SonarQubeService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class MetricsCollectionApplication implements CommandLineRunner {

	@Autowired
	SonarQubeService sonarQubeService

	@Autowired
	MetricsService metricsService

	private static Logger logger = LoggerFactory.getLogger(MetricsCollectionApplication)

	static void main(String[] args) {
		SpringApplication.run(MetricsCollectionApplication, args)
	}

	@Override
	void run(final String... args) throws Exception {

		def tags = getTags()
		logger.info("Tags -> {}", tags)

		if (args.size() == 0) {
			throw new IllegalArgumentException("projectKey needs to be given as argument")
		}

		def projectKey = args[0]
		metricsService.saveSonarQubeMetrics(sonarQubeService.getLastAnalysis(projectKey), tags)
	}

	def getTags() {
		def tags = [:]
		setTag("jiraId", tags)
		setTag("commiter", tags)
		setTag("team", tags)
		setTag("version", tags)
		setTag("sprint", tags)
		setTag("deliveryGroup", tags)
		setTag("codeReview", tags)
		setTag("application", tags)
		setTag("area", tags)
		setTag("product", tags)
		setTag("mr", tags)
		setTag("epic", tags)
		setTag("type", tags)
		setTag("key", tags)
		setTag("group", tags)

		return tags
	}

	def setTag(String key, Map tags) {
		def value = System.getProperty(key)
		if (value) tags[(key)] = value
	}
}
