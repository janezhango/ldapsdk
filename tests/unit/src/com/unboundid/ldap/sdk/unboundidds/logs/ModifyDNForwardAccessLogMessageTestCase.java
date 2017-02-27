/*
 * Copyright 2009-2017 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2009-2017 UnboundID Corp.
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
package com.unboundid.ldap.sdk.unboundidds.logs;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.testng.annotations.Test;

import com.unboundid.ldap.sdk.LDAPSDKTestCase;



/**
 * This class provides test coverage for the
 * {@code ModifyDNForwardAccessLogMessage} class.
 */
public class ModifyDNForwardAccessLogMessageTestCase
       extends LDAPSDKTestCase
{
  /**
   * Tests the ability to create a log message from a string containing only a
   * timestamp.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testOnlyTimestamp()
         throws Exception
  {
    // Get a timestamp that doesn't include milliseconds.
    Date d = new Date();
    GregorianCalendar c = new GregorianCalendar();
    c.setTime(d);
    c.set(GregorianCalendar.MILLISECOND, 0);
    d = c.getTime();

    SimpleDateFormat f = new SimpleDateFormat("'['dd/MMM/yyyy:HH:mm:ss Z']'");
    String s = f.format(d);

    ModifyDNForwardAccessLogMessage m = new ModifyDNForwardAccessLogMessage(s);
    m = new ModifyDNForwardAccessLogMessage(m);

    assertNotNull(m);

    assertNotNull(m.getTimestamp());
    assertEquals(m.getTimestamp(), d);

    assertNotNull(m.getNamedValues());
    assertTrue(m.getNamedValues().isEmpty());

    assertNotNull(m.getUnnamedValues());
    assertTrue(m.getUnnamedValues().isEmpty());

    assertNotNull(m.getMessageType());
    assertEquals(m.getMessageType(), AccessLogMessageType.FORWARD);

    assertNotNull(m.getOperationType());
    assertEquals(m.getOperationType(), AccessLogOperationType.MODDN);

    assertNull(m.getProductName());

    assertNull(m.getInstanceName());

    assertNull(m.getStartupID());

    assertNull(m.getConnectionID());

    assertNull(m.getOperationID());

    assertNull(m.getMessageID());

    assertNull(m.getOrigin());

    assertNull(m.getRequesterDN());

    assertNull(m.getRequesterIPAddress());

    assertNull(m.getIntermediateClientRequest());

    assertNull(m.getOperationPurpose());

    assertNull(m.getDN());

    assertNull(m.getNewRDN());

    assertNull(m.deleteOldRDN());

    assertNull(m.getNewSuperiorDN());

    assertNull(m.getTargetHost());

    assertNull(m.getTargetPort());

    assertNull(m.getTargetProtocol());

    assertNotNull(m.toString());
    assertEquals(m.toString(), s);
  }



  /**
   * Tests the ability to create a log message from a string containing only a
   * basic set of information.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testBasicContents()
         throws Exception
  {
    // Get a timestamp that doesn't include milliseconds.
    Date d = new Date();
    GregorianCalendar c = new GregorianCalendar();
    c.setTime(d);
    c.set(GregorianCalendar.MILLISECOND, 0);
    d = c.getTime();

    SimpleDateFormat f = new SimpleDateFormat("'['dd/MMM/yyyy:HH:mm:ss Z']'");
    String s = f.format(d) + " MODDN FORWARD conn=1 op=2 msgID=3 " +
               "targetHost=\"5.6.7.8\" targetPort=389 targetProtocol=\"LDAP\"";

    ModifyDNForwardAccessLogMessage m = new ModifyDNForwardAccessLogMessage(s);
    m = new ModifyDNForwardAccessLogMessage(m);

    assertNotNull(m);

    assertNotNull(m.getTimestamp());
    assertEquals(m.getTimestamp(), d);

    assertNotNull(m.getNamedValues());
    assertFalse(m.getNamedValues().isEmpty());

    assertNotNull(m.getUnnamedValues());
    assertFalse(m.getUnnamedValues().isEmpty());

    assertNotNull(m.getMessageType());
    assertEquals(m.getMessageType(), AccessLogMessageType.FORWARD);

    assertNotNull(m.getOperationType());
    assertEquals(m.getOperationType(), AccessLogOperationType.MODDN);

    assertNull(m.getProductName());

    assertNull(m.getInstanceName());

    assertNull(m.getStartupID());

    assertNotNull(m.getConnectionID());
    assertEquals(m.getConnectionID(), Long.valueOf(1));

    assertNotNull(m.getOperationID());
    assertEquals(m.getOperationID(), Long.valueOf(2));

    assertNotNull(m.getMessageID());
    assertEquals(m.getMessageID(), Integer.valueOf(3));

    assertNull(m.getOrigin());

    assertNull(m.getRequesterDN());

    assertNull(m.getRequesterIPAddress());

    assertNull(m.getIntermediateClientRequest());

    assertNull(m.getOperationPurpose());

    assertNull(m.getDN());

    assertNull(m.getNewRDN());

    assertNull(m.deleteOldRDN());

    assertNull(m.getNewSuperiorDN());

    assertNotNull(m.getTargetHost());
    assertEquals(m.getTargetHost(), "5.6.7.8");

    assertNotNull(m.getTargetPort());
    assertEquals(m.getTargetPort(), Integer.valueOf(389));

    assertNotNull(m.getTargetProtocol());
    assertEquals(m.getTargetProtocol(), "LDAP");

    assertNotNull(m.toString());
    assertEquals(m.toString(), s);
  }



  /**
   * Tests the ability to create a log message from a string containing a
   * complete set of information.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test()
  public void testCompleteContents()
         throws Exception
  {
    // Get a timestamp that doesn't include milliseconds.
    Date d = new Date();
    GregorianCalendar c = new GregorianCalendar();
    c.setTime(d);
    c.set(GregorianCalendar.MILLISECOND, 0);
    d = c.getTime();

    SimpleDateFormat f = new SimpleDateFormat("'['dd/MMM/yyyy:HH:mm:ss Z']'");
    String s = f.format(d) + " MODDN FORWARD " +
               "product=\"Directory Server\" " +
               "instanceName=\"server.example.com:389\" " +
               "startupID=\"ABCDEFG\" conn=1 op=2 msgID=3 " +
               "origin=\"internal\" requesterIP=\"1.2.3.4\" " +
               "requesterDN=\"uid=test.user,ou=People,dc=example,dc=com\" " +
               "via=\"app='UnboundID Directory Proxy Server'\" " +
               "opPurpose=\"app='Some Client' purpose='foo'\" " +
               "dn=\"uid=test.user,ou=People,dc=example,dc=com\" " +
               "newRDN=\"uid=test.user\" deleteOldRDN=false " +
               "newSuperior=\"ou=Users,dc=example,dc=com\" " +
               "targetHost=\"5.6.7.8\" targetPort=389 targetProtocol=\"LDAP\"";

    ModifyDNForwardAccessLogMessage m = new ModifyDNForwardAccessLogMessage(s);
    m = new ModifyDNForwardAccessLogMessage(m);

    assertNotNull(m);

    assertNotNull(m.getTimestamp());
    assertEquals(m.getTimestamp(), d);

    assertNotNull(m.getNamedValues());
    assertFalse(m.getNamedValues().isEmpty());

    assertNotNull(m.getUnnamedValues());
    assertFalse(m.getUnnamedValues().isEmpty());

    assertNotNull(m.getMessageType());
    assertEquals(m.getMessageType(), AccessLogMessageType.FORWARD);

    assertNotNull(m.getOperationType());
    assertEquals(m.getOperationType(), AccessLogOperationType.MODDN);

    assertNotNull(m.getProductName());
    assertEquals(m.getProductName(), "Directory Server");

    assertNotNull(m.getInstanceName());
    assertEquals(m.getInstanceName(), "server.example.com:389");

    assertNotNull(m.getStartupID());
    assertEquals(m.getStartupID(), "ABCDEFG");

    assertNotNull(m.getConnectionID());
    assertEquals(m.getConnectionID(), Long.valueOf(1));

    assertNotNull(m.getOperationID());
    assertEquals(m.getOperationID(), Long.valueOf(2));

    assertNotNull(m.getMessageID());
    assertEquals(m.getMessageID(), Integer.valueOf(3));

    assertNotNull(m.getOrigin());
    assertEquals(m.getOrigin(), "internal");

    assertNotNull(m.getRequesterDN());
    assertEquals(m.getRequesterDN(),
                 "uid=test.user,ou=People,dc=example,dc=com");

    assertNotNull(m.getRequesterIPAddress());
    assertEquals(m.getRequesterIPAddress(), "1.2.3.4");

    assertNotNull(m.getIntermediateClientRequest());
    assertEquals(m.getIntermediateClientRequest(),
                 "app='UnboundID Directory Proxy Server'");

    assertNotNull(m.getOperationPurpose());
    assertEquals(m.getOperationPurpose(),
                 "app='Some Client' purpose='foo'");

    assertNotNull(m.getDN());
    assertEquals(m.getDN(), "uid=test.user,ou=People,dc=example,dc=com");

    assertNotNull(m.getNewRDN());
    assertEquals(m.getNewRDN(), "uid=test.user");

    assertNotNull(m.deleteOldRDN());
    assertEquals(m.deleteOldRDN(), Boolean.FALSE);

    assertNotNull(m.getNewSuperiorDN());
    assertEquals(m.getNewSuperiorDN(), "ou=Users,dc=example,dc=com");

    assertNotNull(m.getTargetHost());
    assertEquals(m.getTargetHost(), "5.6.7.8");

    assertNotNull(m.getTargetPort());
    assertEquals(m.getTargetPort(), Integer.valueOf(389));

    assertNotNull(m.getTargetProtocol());
    assertEquals(m.getTargetProtocol(), "LDAP");

    assertNotNull(m.toString());
    assertEquals(m.toString(), s);
  }
}
