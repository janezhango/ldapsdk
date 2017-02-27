/*
 * Copyright 2008-2017 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2017 UnboundID Corp.
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
package com.unboundid.ldap.sdk.extensions;



import java.util.HashMap;

import org.testng.annotations.Test;

import com.unboundid.asn1.ASN1Integer;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSDKTestCase;
import com.unboundid.ldap.sdk.ResultCode;



/**
 * This class provides a set of test cases for the
 * EndTransactionExtendedResult class.
 */
public class EndTransactionExtendedResultTestCase
       extends LDAPSDKTestCase
{
  /**
   * Tests the constructors with a success response and no operation response
   * controls.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructorsSuccessNoOpControls()
         throws Exception
  {
    EndTransactionExtendedResult r = new EndTransactionExtendedResult(3,
         ResultCode.SUCCESS, null, null, null, null, null, null);
    r = new EndTransactionExtendedResult(r);

    assertNotNull(r);

    assertEquals(r.getResultCode(), ResultCode.SUCCESS);

    assertNull(r.getDiagnosticMessage());

    assertNull(r.getMatchedDN());

    assertNotNull(r.getReferralURLs());
    assertEquals(r.getReferralURLs().length, 0);

    assertNotNull(r.getResponseControls());
    assertEquals(r.getResponseControls().length, 0);

    assertEquals(r.getFailedOpMessageID(), -1);

    assertNotNull(r.getOperationResponseControls());
    assertTrue(r.getOperationResponseControls().isEmpty());

    assertNotNull(r.getExtendedResultName());
    assertNotNull(r.toString());
  }



  /**
   * Tests the constructors with a success response and operation response
   * controls.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructorsSuccessWithOpControls()
         throws Exception
  {
    Control[] opControls1 =
    {
      new Control("1.2.3.4"),
      new Control("1.2.3.5", true, new ASN1OctetString(new byte[1]))
    };

    Control[] opControls2 =
    {
      new Control("1.2.3.6"),
      new Control("1.2.3.7", true, new ASN1OctetString(new byte[1]))
    };

    HashMap<Integer,Control[]> opControls = new HashMap<Integer,Control[]>();
    opControls.put(1, opControls1);
    opControls.put(2, opControls2);


    EndTransactionExtendedResult r = new EndTransactionExtendedResult(3,
         ResultCode.SUCCESS, null, null, null, null, opControls, null);
    r = new EndTransactionExtendedResult(r);

    assertNotNull(r);

    assertEquals(r.getResultCode(), ResultCode.SUCCESS);

    assertNull(r.getDiagnosticMessage());

    assertNull(r.getMatchedDN());

    assertNotNull(r.getReferralURLs());
    assertEquals(r.getReferralURLs().length, 0);

    assertNotNull(r.getResponseControls());
    assertEquals(r.getResponseControls().length, 0);

    assertEquals(r.getFailedOpMessageID(), -1);

    assertNotNull(r.getOperationResponseControls());
    assertFalse(r.getOperationResponseControls().isEmpty());
    assertNotNull(r.getOperationResponseControls(1));
    assertNotNull(r.getOperationResponseControls(2));
    assertNull(r.getOperationResponseControls(3));

    assertNotNull(r.getExtendedResultName());
    assertNotNull(r.toString());
  }



  /**
   * Tests the constructors with a failure response.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructorsFailure()
         throws Exception
  {
    String[] referralURLs =
    {
      "ldap://server1.example.com/dc=example,dc=com",
      "ldap://server2.example.com/dc=example,dc=com",
    };

    Control[] controls =
    {
      new Control("1.2.3.4"),
      new Control("1.2.3.5", true, new ASN1OctetString(new byte[1]))
    };

    EndTransactionExtendedResult r = new EndTransactionExtendedResult(3,
         ResultCode.UNWILLING_TO_PERFORM, "Not gonna do it",
         "dc=example,dc=com", referralURLs, 1, null, controls);
    r = new EndTransactionExtendedResult(r);

    assertNotNull(r);

    assertEquals(r.getResultCode(), ResultCode.UNWILLING_TO_PERFORM);

    assertNotNull(r.getDiagnosticMessage());

    assertNotNull(r.getMatchedDN());

    assertNotNull(r.getReferralURLs());
    assertEquals(r.getReferralURLs().length, 2);

    assertNotNull(r.getResponseControls());
    assertEquals(r.getResponseControls().length, 2);

    assertEquals(r.getFailedOpMessageID(), 1);

    assertNotNull(r.getOperationResponseControls());
    assertTrue(r.getOperationResponseControls().isEmpty());

    assertNotNull(r.getExtendedResultName());
    assertNotNull(r.toString());
  }



  /**
   * Tests the generic constructor with a value that cannot be decoded as a
   * sequence.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { LDAPException.class })
  public void testConstructorValueNotSequence()
         throws Exception
  {
    new EndTransactionExtendedResult(new ExtendedResult(1, ResultCode.SUCCESS,
         null, null, null, null, new ASN1OctetString("invalid"), null));
  }



  /**
   * Tests the generic constructor with a value that is a sequence with too
   * many elements.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { LDAPException.class })
  public void testConstructorValueSequenceTooManyElements()
         throws Exception
  {
    ASN1Sequence valueSequence = new ASN1Sequence(
         new ASN1Integer(1),
         new ASN1Sequence(),
         new ASN1OctetString());

    new EndTransactionExtendedResult(new ExtendedResult(1, ResultCode.SUCCESS,
         null, null, null, null, new ASN1OctetString(valueSequence.encode()),
         null));
  }



  /**
   * Tests the generic constructor with a value that is a sequence with an
   * unexpected element.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { LDAPException.class })
  public void testConstructorValueSequenceUnexpectedElement()
         throws Exception
  {
    ASN1Sequence valueSequence = new ASN1Sequence(
         new ASN1OctetString());

    new EndTransactionExtendedResult(new ExtendedResult(1, ResultCode.SUCCESS,
         null, null, null, null, new ASN1OctetString(valueSequence.encode()),
         null));
  }



  /**
   * Tests the generic constructor with a value that is a sequence with an
   * element containing an integer type but not an integer value.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { LDAPException.class })
  public void testConstructorValueSequenceMalformedIntegerElement()
         throws Exception
  {
    ASN1Sequence valueSequence = new ASN1Sequence(
         new ASN1OctetString((byte) 0x02));

    new EndTransactionExtendedResult(new ExtendedResult(1, ResultCode.SUCCESS,
         null, null, null, null, new ASN1OctetString(valueSequence.encode()),
         null));
  }



  /**
   * Tests the generic constructor with a value that is a sequence with an
   * element containing a controls sequence that isn't a valid sequence.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructorControlsSequenceEmptySequence()
         throws Exception
  {
    ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Sequence());

    EndTransactionExtendedResult r = new EndTransactionExtendedResult(
         new ExtendedResult(1, ResultCode.SUCCESS, null, null, null, null,
              new ASN1OctetString(valueSequence.encode()), null));

    assertEquals(r.getFailedOpMessageID(), -1);
  }



  /**
   * Tests the generic constructor with a value that is a sequence with an
   * element containing a controls sequence that isn't a valid sequence.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { LDAPException.class })
  public void testConstructorControlsSequenceNotSequence()
         throws Exception
  {
    ASN1Sequence valueSequence = new ASN1Sequence(
         new ASN1OctetString((byte) 0x30, "foo"));

    new EndTransactionExtendedResult(new ExtendedResult(1, ResultCode.SUCCESS,
         null, null, null, null, new ASN1OctetString(valueSequence.encode()),
         null));
  }



  /**
   * Tests the generic constructor with a value that is a sequence with an
   * element containing a controls sequence that isn't a valid sequence.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { LDAPException.class })
  public void testConstructorControlsSequenceDoesNotContainSequence()
         throws Exception
  {
    ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Sequence(
         new ASN1OctetString("foo")));

    new EndTransactionExtendedResult(new ExtendedResult(1, ResultCode.SUCCESS,
         null, null, null, null, new ASN1OctetString(valueSequence.encode()),
         null));
  }



  /**
   * Tests the generic constructor with a value that is a sequence with an
   * element containing a controls sequence that is a sequence with an invalid
   * number of elements.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { LDAPException.class })
  public void testConstructorControlsSequenceInvalidElementCount()
         throws Exception
  {
    ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Sequence(
         new ASN1Sequence()));

    new EndTransactionExtendedResult(new ExtendedResult(1, ResultCode.SUCCESS,
         null, null, null, null, new ASN1OctetString(valueSequence.encode()),
         null));
  }



  /**
   * Tests the generic constructor with a value that is a sequence with an
   * element containing a controls sequence that is a sequence with an invalid
   * number of elements.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { LDAPException.class })
  public void testConstructorControlsSequenceInvalidIntegerElement()
         throws Exception
  {
    ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Sequence(
         new ASN1Sequence(new ASN1OctetString(), new ASN1Sequence())));

    new EndTransactionExtendedResult(new ExtendedResult(1, ResultCode.SUCCESS,
         null, null, null, null, new ASN1OctetString(valueSequence.encode()),
         null));
  }



  /**
   * Tests the generic constructor with a value that is a sequence with an
   * element containing a controls sequence that is a sequence with an invalid
   * sequence element.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(expectedExceptions = { LDAPException.class })
  public void testConstructorControlsSequenceInvalidSequenceElement()
         throws Exception
  {
    ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Sequence(
         new ASN1Sequence(new ASN1Integer(1), new ASN1OctetString("foo"))));

    new EndTransactionExtendedResult(new ExtendedResult(1, ResultCode.SUCCESS,
         null, null, null, null, new ASN1OctetString(valueSequence.encode()),
         null));
  }



  /**
   * Tests the generic constructor with a value that is a sequence with an
   * element containing a controls sequence that is a sequence with an invalid
   * number of controls.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testConstructorControlsSequenceEmptyControls()
         throws Exception
  {
    ASN1Sequence valueSequence = new ASN1Sequence(new ASN1Sequence(
         new ASN1Sequence(new ASN1Integer(1), new ASN1Sequence())));

    new EndTransactionExtendedResult(new ExtendedResult(1, ResultCode.SUCCESS,
         null, null, null, null, new ASN1OctetString(valueSequence.encode()),
         null));
  }
}
