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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compressing strings using GZIP (Java's implementation {@link GZIPOutputStream}) is quite slow. This class performs
 * worse that the one that does not compress the data (see {@link Example1}). Thousand records take about 19 seconds,
 * whereas the same number of records take 8 seconds without compressing the data.
 *
 * @author Albert Attard
 *
 * @see GZIPInputStream
 * @see GZIPOutputStream
 */
public class Example2 {

  private static final Logger LOGGER = LoggerFactory.getLogger(Example2.class);

  public static void main(final String[] args) throws Exception {
    try (BasicDataSource dataSource = DatabaseUtils.createDataSource();
        Connection connection = dataSource.getConnection()) {
      final ExampleTest test = new ExampleTest(connection, "compressed_table", "compressed") {
        @Override
        protected String parseRow(final ResultSet resultSet) throws Exception {
          try (GZIPInputStream in = new GZIPInputStream(resultSet.getBinaryStream("compressed"))) {
            return IOUtils.toString(in, "UTF-8");
          }
        }

        @Override
        protected void setPreparedStatement(final String data, final PreparedStatement statement) throws Exception {
          // Compress the data before inserting it. We need to compress before inserting the data to make this process
          // as realistic as possible.
          final ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length());
          try (OutputStream out = new GZIPOutputStream(baos, data.length())) {
            out.write(data.getBytes("UTF-8"));
          }
          statement.setBinaryStream(1, new ByteArrayInputStream(baos.toByteArray()));
        }
      };
      test.runTest();
    }
    Example2.LOGGER.debug("Done");
  }
}
