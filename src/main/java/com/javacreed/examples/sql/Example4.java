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
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;

/**
 * Encrypts the LZ4 which makes this implementation the slowest.
 *
 * @author Albert Attard
 */
public class Example4 {

  private static final Logger LOGGER = LoggerFactory.getLogger(Example4.class);

  public static void main(final String[] args) throws Exception {
    try (BasicDataSource dataSource = DatabaseUtils.createDataSource();
        Connection connection = dataSource.getConnection()) {
      final ExampleTest test = new ExampleTest(connection, "compressed_table", "compressed") {

        @Override
        protected String parseRow(final ResultSet resultSet) throws Exception {
          try (InputStream in = new LZ4BlockInputStream(
              CryptoUtils.wrapInToCipheredInputStream(resultSet.getBinaryStream("compressed")))) {
            return IOUtils.toString(in, "UTF-8");
          }
        }

        @Override
        protected void setPreparedStatement(final String data, final PreparedStatement statement) throws Exception {
          final ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length());
          try (OutputStream out = new LZ4BlockOutputStream(CryptoUtils.wrapInToCipheredOutputStream(baos))) {
            out.write(data.getBytes("UTF-8"));
          }
          statement.setBinaryStream(1, new ByteArrayInputStream(baos.toByteArray()));
        }

      };
      test.runTest();
    }
    Example4.LOGGER.debug("Done");
  }
}
