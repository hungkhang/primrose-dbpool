/** 
*        Library name : Primrose - A Java Database Connection Pool. 
*        Published by Ben Keeping, http://primrose.org.uk . 
*        Copyright (C) 2004 Ben Keeping, primrose.org.uk 
*        Email: Use "Contact Us Form" on website 
* 
*        This library is free software; you can redistribute it and/or 
*        modify it under the terms of the GNU Lesser General Public 
*        License as published by the Free Software Foundation; either 
*        version 2.1 of the License, or (at your option) any later version. 
* 
*        This library is distributed in the hope that it will be useful, 
*        but WITHOUT ANY WARRANTY; without even the implied warranty of 
*        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU 
*        Lesser General Public License for more details. 
* 
*        You should have received a copy of the GNU Lesser General Public 
*        License along with this library; if not, write to the Free Software 
*        Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
*/ 
package uk.org.primrose.pool.core.loadrules; 
 
import uk.org.primrose.Logger; 
import uk.org.primrose.pool.core.Pool; 
 
 
/** 
 * 
 * @author sedj 
 *        If the 'failoverPool' parameter is set, the 'checkSQL' parameter 
 *        must also be set. This is because to determine if a DB is down, and the failoverPool 
 *        should be used, a piece of SQL needs to be defined (such as 'select 1 from dual') 
 *        which will not throw a user exception (basically some SQL guaranteed to work if the DB is up). 
 */ 
public class FailoverPool implements LoadRule { 
    public void runCheck(Pool pool, Logger logger) throws LoadRuleException { 
        logger.info("[Pool@" + pool.getPoolName() + 
            "] Checking 'failoverPool' rule  ..."); 
 
        if ((pool.getOnExceptionCheckSQL() == null) || 
                (pool.getOnExceptionCheckSQL().length() == 0)) { 
            throw new LoadRuleException( 
                "Pool config parameter 'onExceptionCheckSQL' must be specified if you wish to use the 'failoverPool' parameter."); 
        } 
 
        if ((pool.getDumpConnectionOnSQLException() == null) || 
                !pool.getDumpConnectionOnSQLException().equalsIgnoreCase("true")) { 
            throw new LoadRuleException( 
                "Pool config parameter 'dumpConnectionOnSQLException' must be set to 'true' if you wish to use the 'failoverPool' parameter. (dumpConnectionOnSQLException=" + 
                pool.getDumpConnectionOnSQLException() + ")"); 
        } 
 
        if (pool.getWaitForConnectionIfDatabaseIsDown().equalsIgnoreCase("true")) { 
            throw new LoadRuleException( 
                "Pool config parameter 'waitForConnectionIfDatabaseIsDown' must be set to 'false' if you wish to use the 'failoverPool' parameter. (waitForConnectionIfDatabaseIsDown=" + 
                pool.getWaitForConnectionIfDatabaseIsDown() + ")"); 
        } 
    } 
} 
