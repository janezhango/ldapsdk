/*
 * Copyright 2007-2017 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2007-2017 UnboundID Corp.
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
package com.unboundid.ldap.sdk.controls;



import org.testng.annotations.Test;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSDKTestCase;
import com.unboundid.ldap.sdk.ResultCode;



/**
 * This class provides a set of test cases for the ManageDsaITRequestControl
 * class.
 */
public class ManageDsaITRequestControlTestCase
       extends LDAPSDKTestCase
{
  /**
   * Tests the first constructor.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor1()
         throws Exception
  {
    ManageDsaITRequestControl c = new ManageDsaITRequestControl();
    c = new ManageDsaITRequestControl(c);

    assertFalse(c.isCritical());

    assertNotNull(c.getControlName());
    assertNotNull(c.toString());
  }



  /**
   * Tests the second constructor.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructor2()
         throws Exception
  {
    ManageDsaITRequestControl c = new ManageDsaITRequestControl(true);
    c = new ManageDsaITRequestControl(c);

    assertTrue(c.isCritical());

    assertNotNull(c.getControlName());
    assertNotNull(c.toString());
  }



  /**
   * Tests the third constructor with a generic control that contains a value.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { LDAPException.class })
  public void testConstructor3WithValue()
         throws Exception
  {
    Control c = new Control(ManageDsaITRequestControl.MANAGE_DSA_IT_REQUEST_OID,
                            true, new ASN1OctetString("foo"));
    new ManageDsaITRequestControl(c);
  }



  /**
   * Creates a smart referral entry, verifies that it will result in a referral
   * when trying to delete it without the ManageDsaIT control, and that the
   * delete will be successful with the ManageDsaIT control.
   * <BR><BR>
   * Access to a Directory Server instance is required for complete processing.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testSendRequestWithManageDsaITControl()
         throws Exception
  {
    if (! isDirectoryInstanceAvailable())
    {
      return;
    }

    LDAPConnection conn = getAdminConnection();
    conn.add(getTestBaseDN(), getBaseEntryAttributes());

    String dn = "ou=Test Referral," + getTestBaseDN();
    Attribute[] attrs =
    {
      new Attribute("objectClass", "top", "referral", "extensibleObject"),
      new Attribute("ou", "Test Referral"),
      new Attribute("ref", "ldap://test1.example.com/" + dn,
                    "ldap://test2.example.com/" + dn)
    };

    conn.add(dn, attrs);

    try
    {
      conn.delete(dn);
      fail("Expected an exception when trying to delete a referral entry " +
           "without the ManageDsaIT control.");
    }
    catch (LDAPException le)
    {
      assertEquals(le.getResultCode(), ResultCode.REFERRAL);
    }

    Control[] controls =
    {
      new ManageDsaITRequestControl()
    };

    conn.delete(new DeleteRequest(dn, controls));
    conn.delete(getTestBaseDN());
    conn.close();
  }
}
