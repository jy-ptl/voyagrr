package com.voyagrr.sharingservice.config.liquibase;

import java.sql.*;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class DatabaseLogAppender extends AppenderBase<ILoggingEvent> {

    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    @Override
    protected void append(ILoggingEvent event) {

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet table = metaData.getTables(null, null, "liquibase_exceptions", null);

            if (!table.next()) {
                String TABLE_SQL = """
                          CREATE TABLE IF NOT EXISTS liquibase_exceptions(
                                liquibase_exception_id BIGSERIAL PRIMARY KEY,
                                changeset_id VARCHAR(30),
                                author VARCHAR(50),
                                file_path TEXT,
                                classname TEXT,
                                exception_message TEXT,
                                stack_trace TEXT,
                                created_on TIMESTAMP
                            )
                        """;
                try (Statement statement = connection.createStatement()) {
                    statement.execute(TABLE_SQL);
                    log.info("created liquibase_exceptions table");
                }
            }

            String sql = """
                        INSERT INTO liquibase_exceptions
                              (changeset_id, author, file_path, classname, exception_message, stack_trace, created_on)
                        VALUES(?, ?, ?, ?, ?, ?, ?)
                    """;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                String[] messageString = event.getMessage().split("::");

                statement.setString(1, StringUtils.isBlank(messageString[1]) ? "" : messageString[1]);
                statement.setString(2, StringUtils.isBlank(messageString[2]) ? ""
                        : messageString[2].replace("encountered an exception", "").trim());
                statement.setString(3,
                        StringUtils.isBlank(messageString[0]) ? "" : messageString[0].replaceFirst("^ChangeSet ", ""));

                if (event.getThrowableProxy() != null) {
                    statement.setString(4, event.getThrowableProxy().getClassName());
                    statement.setString(5, event.getThrowableProxy().getMessage());
                    statement.setString(6, Arrays.toString(event.getThrowableProxy().getStackTraceElementProxyArray()));
                } else {
                    statement.setNull(4, Types.VARCHAR);
                    statement.setNull(5, Types.VARCHAR);
                    statement.setNull(6, Types.VARCHAR);
                }
                statement.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
                log.warn("INSERTING LIQUIBASE ERROR INTO THE TABLE");
                statement.executeUpdate();

            }

        } catch (SQLException e) {
            log.error(e.getLocalizedMessage());
        }

    }

}
