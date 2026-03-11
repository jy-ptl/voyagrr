package com.voyagrr.processingservice.config.liquibase;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;

@Configuration
public class LoggingConfig {

    @Value("${logging.level.liquibase}")
    private String liquobaseLoggingLevel;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Bean
    public LoggerContext loggerContext() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        configureLogging(context);
        return context;
    }

    private void configureLogging(LoggerContext context) {

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(context);
        consoleAppender.setName("CONSOLE");

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} %highlight(%-5level) %cyan(%-50.50logger{19}) : %msg%n");
        encoder.start();

        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        DatabaseLogAppender databaseLogAppender = new DatabaseLogAppender();
        databaseLogAppender.setContext(context);
        databaseLogAppender.setName("DB");
        databaseLogAppender.setDbUrl(dbUrl);
        databaseLogAppender.setDbUser(dbUser);
        databaseLogAppender.setDbPassword(dbPassword);

        ThresholdFilter thresholdFilter = new ThresholdFilter();
        thresholdFilter.setLevel(Level.ERROR.toString());
        thresholdFilter.start();
        databaseLogAppender.addFilter(thresholdFilter);

        databaseLogAppender.start();

        Logger logger = context.getLogger("liquibase");
        logger.setLevel(getLogbackLevel(liquobaseLoggingLevel));
        logger.addAppender(databaseLogAppender);
        logger.setAdditive(false);

    }

    private Level getLogbackLevel(String level) {
        Level levelNew;

        switch (level) {
            case "OFF":
                levelNew = Level.OFF;
                break;
            case "ERROR":
                levelNew = Level.ERROR;
                break;
            case "WARN":
                levelNew = Level.WARN;
                break;
            case "DEBUG":
                levelNew = Level.DEBUG;
                break;
            case "TRACE":
                levelNew = Level.TRACE;
                break;
            default:
                levelNew = Level.INFO;
        }
        return levelNew;
    }
}
