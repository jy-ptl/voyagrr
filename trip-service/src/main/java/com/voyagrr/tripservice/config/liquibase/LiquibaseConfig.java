package com.voyagrr.tripservice.config.liquibase;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LiquibaseConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${spring.liquibase.change-log}")
    private String liquibasePath;

    @Value("${spring.liquibase.enabled}")
    private boolean enabled;

    private final DataSource dataSource;
    private final Environment environment;
    private final ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (enabled) {
            try {
                runLiquibase();
            } catch (LiquibaseException e) {
                throw new RuntimeException(e);
            }
            enabled = false;
        }
    }

    public void runLiquibase() throws LiquibaseException {
        SpringLiquibase liquibase = new SpringLiquibase();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("spring.datasource.username", environment.getProperty("spring.datasource.username"));

        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(liquibasePath);
        liquibase.setChangeLogParameters(parameters);
        liquibase.setResourceLoader(applicationContext);

        try {
            liquibase.afterPropertiesSet();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
