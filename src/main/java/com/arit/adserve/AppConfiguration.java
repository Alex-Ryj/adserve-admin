package com.arit.adserve;

import com.arit.adserve.providers.ebay.EbayCamelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Alex Ryjoukhine
 * @since May 12, 2020
 * 
 */
@Slf4j
@Configuration
@EnableTransactionManagement
public class AppConfiguration {

	@Bean
	public CamelContextConfiguration contextConfiguration(EbayCamelService ebayCamelService) {
		return new CamelContextConfiguration() {
			@Override
			public void beforeApplicationStart(CamelContext context) {
				log.info("camel context before start: " + context);
			}

			@Override
			public void afterApplicationStart(CamelContext camelContext) {
				log.info("camel context after start: " + camelContext);
				try {
					camelContext.start();
					camelContext.addRoutes(configureGeneralRoutes());
					camelContext.addRoutes(ebayCamelService.configureRoutes());
				} catch (Exception e) {
					log.error("camel failed to start", e);
					System.exit(1);
				}
			}
		};
	}	

	@Bean(name="transactionReadUncommitted")	
	public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		return transactionTemplate;
	}

	
	private RouteBuilder configureGeneralRoutes() {
		return new RouteBuilder() {
			@SuppressWarnings("unchecked")
			public void configure() throws Exception {
				// camel general exception handling
				onException(RuntimeCamelException.class).log("${body}");
			}
		};
	}

}
