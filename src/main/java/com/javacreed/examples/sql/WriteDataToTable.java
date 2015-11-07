/*
 * #%L
 * Compress Large Text Data in Table
 * %%
 * Copyright (C) 2012 - 2015 Java Creed
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.javacreed.examples.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes the data into table
 *
 * @author Albert Attard
 */
public abstract class WriteDataToTable {

  private static final Logger LOGGER = LoggerFactory.getLogger(WriteDataToTable.class);

  private final String tableName;

  private final String columnName;

  private final Connection connection;

  private int testSize = 1000;

  public WriteDataToTable(final Connection connection, final String tableName, final String columnName) {
    this.connection = connection;
    this.tableName = tableName;
    this.columnName = columnName;
  }

  protected void clearTable() throws SQLException {
    try (Statement statement = connection.createStatement()) {
      statement.execute("TRUNCATE TABLE `" + tableName + "`");
    }
  }

  public long insert() throws Exception {
    WriteDataToTable.LOGGER.debug("Starting test");

    // The data to be saved in the database
    final String hamlet = IOUtils.toString(WriteDataToTable.class.getResourceAsStream("/Shakespeare Hamlet.txt"),
        "UTF-8");
    WriteDataToTable.LOGGER.debug("Loaded test ({} bytes long)", hamlet.length());

    // Clear table
    WriteDataToTable.LOGGER.debug("Clearing table so that the test starts on a fresh table");
    clearTable();

    WriteDataToTable.LOGGER.debug("Inserting the sample data {} time in table '{}'", testSize, tableName);
    final long startedInMillis = System.currentTimeMillis();
    // Insert the text several times in the database using prepared statement
    for (int i = 0; i < testSize; i++) {
      try (PreparedStatement statement = connection
          .prepareStatement("INSERT INTO `" + tableName + "` (`" + columnName + "`) VALUES (?)")) {
        setPreparedStatement(hamlet, statement);
        statement.execute();
      }
    }

    final long takenInMillis = System.currentTimeMillis() - startedInMillis;
    WriteDataToTable.LOGGER.debug("Inserted {} records in {} millis", testSize, takenInMillis);
    return takenInMillis;
  }

  protected abstract void setPreparedStatement(String data, PreparedStatement statement) throws Exception;

  public WriteDataToTable testSize(final int testSize) {
    this.testSize = testSize;
    return this;
  }
}
