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
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inserting long text, Shakespeare's Hamlet play, into the table without modifying it. Thousand records take almost 8
 * seconds.
 *
 * @author Albert Attard
 */
public class Example1 {

  private static final Logger LOGGER = LoggerFactory.getLogger(Example1.class);

  public static void main(final String[] args) throws Exception {
    try (BasicDataSource dataSource = DatabaseUtils.createDataSource();
        Connection connection = dataSource.getConnection()) {
      final ExampleTest test = new ExampleTest(connection, "large_text_table", "text") {
        @Override
        protected String parseRow(final ResultSet resultSet) throws Exception {
          return resultSet.getString("text");
        }

        @Override
        protected void setPreparedStatement(final String data, final PreparedStatement statement) throws SQLException {
          statement.setString(1, data);
        }
      };
      test.runTest();
    }
    Example1.LOGGER.debug("Done");
  }
}
