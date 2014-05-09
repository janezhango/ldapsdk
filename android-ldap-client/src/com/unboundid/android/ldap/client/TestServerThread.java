/*
 * Copyright 2009-2011 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2009-2011 UnboundID Corp.
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
package com.unboundid.android.ldap.client;



import java.util.LinkedList;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.Entry;

import static com.unboundid.android.ldap.client.Logger.*;
import static com.unboundid.util.StaticUtils.*;



/**
 * This class defines a thread that will be used to test the validity of a
 * server instance and the ability to communicate with that server.
 */
final class TestServerThread
      extends Thread
{
  /**
   * The tag that will be used for log messages generated by this class.
   */
  private static final String LOG_TAG = "TestServerThread";



  // The instance in which the search is to be performed.
  private final ServerInstance instance;

  // The activity that will invoke this test.
  private final ServerTestInvoker invoker;



  /**
   * Creates a new test server thread with the provided information.
   *
   * @param  invoker  The activity that will invoke this test.
   * @param  instance  The instance in which the search is to be performed.
   */
  TestServerThread(final ServerTestInvoker invoker,
                   final ServerInstance instance)
  {
    logEnter(LOG_TAG, "<init>", invoker, instance);

    this.invoker  = invoker;
    this.instance = instance;
  }



  /**
   * Processes the search and returns the result to the caller.
   */
  @Override()
  public void run()
  {
    logEnter(LOG_TAG, "run");

    final LinkedList<String> reasons = new LinkedList<String>();
    boolean acceptable = instance.isDefinitionValid(invoker, reasons);
    if (acceptable)
    {
      LDAPConnection conn = null;
      try
      {
        conn = instance.getConnection(invoker);

        final Entry e = conn.getEntry(instance.getBaseDN());
        if (e == null)
        {
          acceptable = false;
          reasons.add(invoker.getString(
               R.string.test_server_thread_cannot_get_base,
               instance.getBaseDN()));
        }
      }
      catch (Exception e)
      {
        logException(LOG_TAG, "run", e);

        acceptable = false;
        reasons.add(getExceptionMessage(e));
      }
      finally
      {
        if (conn != null)
        {
          conn.close();
        }
      }
    }

    invoker.testCompleted(acceptable, reasons);
  }
}
