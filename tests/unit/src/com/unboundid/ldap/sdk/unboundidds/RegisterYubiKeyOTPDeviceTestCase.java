/*
 * Copyright 2016-2017 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2016-2017 UnboundID Corp.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */
package com.unboundid.ldap.sdk.unboundidds;



import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.unboundid.ldap.listener.CannedResponseExtendedOperationHandler;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.LDAPSDKTestCase;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.unboundidds.extensions.
            DeregisterYubiKeyOTPDeviceExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.
            RegisterYubiKeyOTPDeviceExtendedRequest;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.util.StaticUtils;



/**
 * This class provides a set of tests for the register YubiKey OTP device tool.
 */
public final class RegisterYubiKeyOTPDeviceTestCase
       extends LDAPSDKTestCase
{
  // An in-memory directory server instance that will return a non-success
  // result for any register or deregister request.
  private InMemoryDirectoryServer failureDS = null;

  // An in-memory directory server instance that will return a success result
  // for any register or deregister request.
  private InMemoryDirectoryServer successDS = null;



  /**
   * Creates an in-memory directory server instance for testing.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @BeforeClass()
  public void setUp()
         throws Exception
  {
    final InMemoryDirectoryServerConfig failureCfg =
         new InMemoryDirectoryServerConfig("dc=example,dc=com");
    failureCfg.addAdditionalBindCredentials("cn=Directory Manager", "password");
    failureCfg.addExtendedOperationHandler(
         new CannedResponseExtendedOperationHandler(
              ResultCode.UNWILLING_TO_PERFORM,
              RegisterYubiKeyOTPDeviceExtendedRequest.
                   REGISTER_YUBIKEY_OTP_DEVICE_REQUEST_OID,
              DeregisterYubiKeyOTPDeviceExtendedRequest.
                   DEREGISTER_YUBIKEY_OTP_DEVICE_REQUEST_OID));

    failureDS = new InMemoryDirectoryServer(failureCfg);
    failureDS.startListening();

    final InMemoryDirectoryServerConfig successCfg =
         new InMemoryDirectoryServerConfig("dc=example,dc=com");
    successCfg.addAdditionalBindCredentials("cn=Directory Manager", "password");
    successCfg.addExtendedOperationHandler(
         new CannedResponseExtendedOperationHandler(
              RegisterYubiKeyOTPDeviceExtendedRequest.
                   REGISTER_YUBIKEY_OTP_DEVICE_REQUEST_OID,
              DeregisterYubiKeyOTPDeviceExtendedRequest.
                   DEREGISTER_YUBIKEY_OTP_DEVICE_REQUEST_OID));

    successDS = new InMemoryDirectoryServer(successCfg);
    successDS.startListening();
  }



  /**
   * Cleans up after testing is complete.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @AfterClass()
  public void cleanUp()
         throws Exception
  {
    failureDS.shutDown(true);
    successDS.shutDown(true);
  }




  /**
   * Obtains test coverage for the methods that don't require running the tool.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testWithoutRunning()
         throws Exception
  {
    final RegisterYubiKeyOTPDevice tool =
         new RegisterYubiKeyOTPDevice(null, null);

    assertNotNull(tool.getToolName());

    assertNotNull(tool.getToolDescription());

    assertNotNull(tool.getToolVersion());

    assertTrue(tool.supportsInteractiveMode());

    assertTrue(tool.defaultsToInteractiveMode());

    assertTrue(tool.supportsPropertiesFile());

    assertNotNull(tool.getExampleUsages());
    assertFalse(tool.getExampleUsages().isEmpty());
  }



  /**
   * Provides test coverage for the ability to get usage information.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testGetUsage()
         throws Exception
  {
    final String[] args = { "--help" };
    assertEquals(
         RegisterYubiKeyOTPDevice.main(args, null, null),
         ResultCode.SUCCESS);
  }



  /**
   * Tests the behavior when attempting to register a device when no OTP was
   * provided.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testRegisterMissingOTP()
         throws Exception
  {
    final String[] args =
    {
      "--hostname", "127.0.0.1",
      "--port", String.valueOf(successDS.getListenPort()),
      "--bindDN", "cn=Directory Manager",
      "--bindPassword", "password"
    };

    final ResultCode rc = RegisterYubiKeyOTPDevice.main(args, null, null);
    assertFalse(rc == ResultCode.SUCCESS);
  }



  /**
   * Tests the behavior when attempting to register a device when only an OTP
   * is given.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testRegisterOnlyOTP()
         throws Exception
  {
    final String[] args =
    {
      "--hostname", "127.0.0.1",
      "--port", String.valueOf(successDS.getListenPort()),
      "--bindDN", "cn=Directory Manager",
      "--bindPassword", "password",
      "--otp", "YubiKeyOTP"
    };

    final ResultCode rc = RegisterYubiKeyOTPDevice.main(args, null, null);
    assertEquals(rc, ResultCode.SUCCESS);
  }



  /**
   * Tests the behavior when attempting to register a device with an
   * authentication ID and a password provided directly via an argument when a
   * success result is expected.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testRegisterWithPasswordArgumentSuccess()
         throws Exception
  {
    final String[] args =
    {
      "--hostname", "127.0.0.1",
      "--port", String.valueOf(successDS.getListenPort()),
      "--bindDN", "cn=Directory Manager",
      "--bindPassword", "password",
      "--authID", "u:test.user",
      "--userPassword", "userPassword",
      "--otp", "YubiKeyOTP"
    };

    final ResultCode rc = RegisterYubiKeyOTPDevice.main(args, null, null);
    assertEquals(rc, ResultCode.SUCCESS);
  }



  /**
   * Tests the behavior when attempting to register a device with an
   * authentication ID and a password provided directly via an argument when a
   * failure result is expected.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testRegisterWithPasswordArgumentFailure()
         throws Exception
  {
    final String[] args =
    {
      "--hostname", "127.0.0.1",
      "--port", String.valueOf(failureDS.getListenPort()),
      "--bindDN", "cn=Directory Manager",
      "--bindPassword", "password",
      "--authID", "u:test.user",
      "--userPassword", "userPassword",
      "--otp", "YubiKeyOTP"
    };

    final ResultCode rc = RegisterYubiKeyOTPDevice.main(args, null, null);
    assertEquals(rc, ResultCode.UNWILLING_TO_PERFORM);
  }



  /**
   * Tests the behavior when attempting to register a device with an
   * authentication ID and a password read from a file that exists.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testRegisterWithPasswordFromExistingFile()
         throws Exception
  {
    final String passwordFilePath = createTempFile("password").getPath();
    final String[] args =
    {
      "--hostname", "127.0.0.1",
      "--port", String.valueOf(successDS.getListenPort()),
      "--bindDN", "cn=Directory Manager",
      "--bindPassword", "password",
      "--authID", "u:test.user",
      "--userPasswordFile", passwordFilePath,
      "--otp", "YubiKeyOTP"
    };

    final ResultCode rc = RegisterYubiKeyOTPDevice.main(args, null, null);
    assertEquals(rc, ResultCode.SUCCESS);
  }



  /**
   * Tests the behavior when attempting to register a device with an
   * authentication ID and a password read from a file that does not exist.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testRegisterWithPasswordFromNonexistentFile()
         throws Exception
  {
    final File passwordFile = createTempFile("password");
    assertTrue(passwordFile.delete());

    final String[] args =
    {
      "--hostname", "127.0.0.1",
      "--port", String.valueOf(successDS.getListenPort()),
      "--bindDN", "cn=Directory Manager",
      "--bindPassword", "password",
      "--authID", "u:test.user",
      "--userPasswordFile", passwordFile.getAbsolutePath(),
      "--otp", "YubiKeyOTP"
    };

    final ResultCode rc = RegisterYubiKeyOTPDevice.main(args, null, null);
    assertFalse(rc == ResultCode.SUCCESS);
  }



  /**
   * Tests the behavior when attempting to register a device with an
   * authentication ID and a password read from standard input.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testRegisterWithPasswordReadFromStandardInput()
         throws Exception
  {
    final InputStream originalIn = System.in;

    try
    {
      final ByteStringBuffer buffer = new ByteStringBuffer();
      buffer.append("password");
      buffer.append(StaticUtils.EOL_BYTES);

      System.setIn(new ByteArrayInputStream(buffer.toByteArray()));

      final String[] args =
           {
                "--hostname", "127.0.0.1",
                "--port", String.valueOf(successDS.getListenPort()),
                "--bindDN", "cn=Directory Manager",
                "--bindPassword", "password",
                "--authID", "u:test.user",
                "--promptForUserPassword",
                "--otp", "YubiKeyOTP"
           };

      final ResultCode rc = RegisterYubiKeyOTPDevice.main(args, null, null);
      assertEquals(rc, ResultCode.SUCCESS);
    }
    finally
    {
      System.setIn(originalIn);
    }
  }



  /**
   * Tests the behavior when attempting to deregister a device with an
   * authentication ID and a password provided directly via an argument when a
   * success result is expected.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testDeregisterOneDeviceSuccess()
         throws Exception
  {
    final String[] args =
    {
      "--hostname", "127.0.0.1",
      "--port", String.valueOf(successDS.getListenPort()),
      "--bindDN", "cn=Directory Manager",
      "--bindPassword", "password",
      "--deregister",
      "--authID", "u:test.user",
      "--userPassword", "userPassword",
      "--otp", "YubiKeyOTP"
    };

    final ResultCode rc = RegisterYubiKeyOTPDevice.main(args, null, null);
    assertEquals(rc, ResultCode.SUCCESS);
  }



  /**
   * Tests the behavior when attempting to deregister a device with an
   * authentication ID and a password provided directly via an argument when a
   * failure result is expected.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testDeregisterOneDeviceFailure()
         throws Exception
  {
    final String[] args =
    {
      "--hostname", "127.0.0.1",
      "--port", String.valueOf(failureDS.getListenPort()),
      "--bindDN", "cn=Directory Manager",
      "--bindPassword", "password",
      "--deregister",
      "--authID", "u:test.user",
      "--userPassword", "userPassword",
      "--otp", "YubiKeyOTP"
    };

    final ResultCode rc = RegisterYubiKeyOTPDevice.main(args, null, null);
    assertEquals(rc, ResultCode.UNWILLING_TO_PERFORM);
  }



  /**
   * Tests the behavior when attempting to deregister all devices for a
   * specified user when a success result is expected.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testDeregisterAllDevicesSuccess()
         throws Exception
  {
    final String[] args =
    {
      "--hostname", "127.0.0.1",
      "--port", String.valueOf(successDS.getListenPort()),
      "--bindDN", "cn=Directory Manager",
      "--bindPassword", "password",
      "--deregister",
      "--authID", "u:test.user",
      "--userPassword", "userPassword"
    };

    final ResultCode rc = RegisterYubiKeyOTPDevice.main(args, null, null);
    assertEquals(rc, ResultCode.SUCCESS);
  }



  /**
   * Tests the behavior when attempting to deregister all devices for a
   * specified user when a failure result is expected.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testDeregisterAllDevicesFailure()
         throws Exception
  {
    final String[] args =
    {
      "--hostname", "127.0.0.1",
      "--port", String.valueOf(failureDS.getListenPort()),
      "--bindDN", "cn=Directory Manager",
      "--bindPassword", "password",
      "--deregister",
      "--authID", "u:test.user",
      "--userPassword", "userPassword"
    };

    final ResultCode rc = RegisterYubiKeyOTPDevice.main(args, null, null);
    assertEquals(rc, ResultCode.UNWILLING_TO_PERFORM);
  }



  /**
   * Tests the behavior when when the tool is unable to get a connection to the
   * target server.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testCannotGetConnection()
         throws Exception
  {
    final String[] args =
    {
      "--hostname", "127.0.0.1",
      "--port", String.valueOf(successDS.getListenPort()),
      "--bindDN", "cn=Directory Manager",
      "--bindPassword", "wrong",
      "--otp", "YubiKeyOTP"
    };

    final ResultCode rc = RegisterYubiKeyOTPDevice.main(args, null, null);
    assertEquals(rc, ResultCode.INVALID_CREDENTIALS);
  }
}
