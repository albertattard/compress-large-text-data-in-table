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

import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Cryptographic utilities methods
 *
 * @author Albert Attard
 */
public class CryptoUtils {

  private static final byte[] IV = new byte[] { 48, -54, -23, -75, 110, 14, 7, 33, -44, -21, 17, 19, 23, 79, 1, 5 };

  private static Cipher createCipher() throws Exception {
    final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    return cipher;
  }

  private static SecretKeySpec createKeySpec() throws Exception {
    final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    final KeySpec keySpec = new PBEKeySpec("javacreed".toCharArray(), new byte[] { 28, 114, 71, 21, -14, -98, 7, 19 },
        1024, 128);
    final SecretKey tempSecretKey = factory.generateSecret(keySpec);
    final SecretKeySpec secretKeySpec = new SecretKeySpec(tempSecretKey.getEncoded(), "AES");
    return secretKeySpec;
  }

  public static InputStream wrapInToCipheredInputStream(final InputStream in) throws Exception {
    final Cipher cipher = CryptoUtils.createCipher();
    cipher.init(Cipher.DECRYPT_MODE, CryptoUtils.createKeySpec(), new IvParameterSpec(CryptoUtils.IV));
    return new CipherInputStream(in, cipher);
  }

  public static OutputStream wrapInToCipheredOutputStream(final OutputStream out) throws Exception {
    final Cipher cipher = CryptoUtils.createCipher();
    cipher.init(Cipher.ENCRYPT_MODE, CryptoUtils.createKeySpec(), new IvParameterSpec(CryptoUtils.IV));
    return new CipherOutputStream(out, cipher);
  }

  private CryptoUtils() {}
}
