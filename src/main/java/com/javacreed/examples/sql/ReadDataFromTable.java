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
import java.sql.ResultSet;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads and verify data from table
 *
 * @author Albert Attard
 */
public abstract class ReadDataFromTable {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReadDataFromTable.class);

  private final String tableName;

  private final String columnName;

  private final Connection connection;

  public ReadDataFromTable(final Connection connection, final String tableName, final String columnName) {
    this.connection = connection;
    this.tableName = tableName;
    this.columnName = columnName;
  }

  protected abstract String parseRow(ResultSet resultSet) throws Exception;

  public void verify() throws Exception {
    ReadDataFromTable.LOGGER.debug("Starting verification");

    // The data to be saved in the database
    final String hamlet = IOUtils.toString(ReadDataFromTable.class.getResourceAsStream("/Shakespeare Hamlet.txt"),
        "UTF-8");
    ReadDataFromTable.LOGGER.debug("Loaded test ({} bytes long)", hamlet.length());

    try (
        PreparedStatement preparedStatement = connection
            .prepareStatement("SELECT `" + columnName + "` FROM `" + tableName + "`");
        ResultSet resultSet = preparedStatement.executeQuery()) {

      int rowNumber = 0;
      final long startedOnInMillis = System.currentTimeMillis();
      for (; resultSet.next(); rowNumber++) {
        final String inTable = parseRow(resultSet);
        if (hamlet.equals(inTable) == false) {
          throw new RuntimeException("Invalid data found in row " + rowNumber);
        }
      }

      final long takenInMillis = System.currentTimeMillis() - startedOnInMillis;
      ReadDataFromTable.LOGGER.debug("Verified {} record in {} millis", rowNumber, takenInMillis);
    }

  }
}
