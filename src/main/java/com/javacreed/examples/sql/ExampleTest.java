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

/**
 * A convenience class that combines the writing and reading in one class.
 * 
 * @author Albert Attard
 */
public abstract class ExampleTest {

  private WriteDataToTable writeDataToTable;

  private ReadDataFromTable readDataFromTable;

  public ExampleTest(final Connection connection, final String tableName, final String columnName) {
    writeDataToTable = new WriteDataToTable(connection, tableName, columnName) {
      @Override
      protected void setPreparedStatement(final String data, final PreparedStatement statement) throws Exception {
        ExampleTest.this.setPreparedStatement(data, statement);
      }
    };

    readDataFromTable = new ReadDataFromTable(connection, tableName, columnName) {
      @Override
      protected String parseRow(final ResultSet resultSet) throws Exception {
        return ExampleTest.this.parseRow(resultSet);
      }
    };
  }

  protected abstract String parseRow(ResultSet resultSet) throws Exception;

  public long runTest() throws Exception {
    final long timeTakenInMillis = writeDataToTable.insert();
    readDataFromTable.verify();
    return timeTakenInMillis;
  }

  protected abstract void setPreparedStatement(String data, PreparedStatement statement) throws Exception;

}
