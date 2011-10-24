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
 *        If the 'waitForConnectionIfDatabaseIsDown' parameter is set to true 
 *        and the 'failoverPool' parameter is also set, then error, because the two 
 *        are mutually exclusive (you can't wait for a DB to come back up, if you 
 *        also want to fail over when a DB is down). 
 */ 
public class WaitForConnectionIfDatabaseIsDown implements LoadRule { 
    public void runCheck(Pool pool, Logger logger) throws LoadRuleException { 
        logger.info("[Pool@" + pool.getPoolName() + 
            "] Checking 'waitForConnectionIfDatabaseIsDown' rule  ..."); 
 
        if ((pool.getFailoverPool() != null) && 
                pool.getWaitForConnectionIfDatabaseIsDown() 
                        .equalsIgnoreCase("true")) { 
            throw new LoadRuleException( 
                "Pool config parameter 'failoverPool' must not be set if you wish to use the 'waitForConnectionIfDatabaseIsDown' parameter.)"); 
        } 
    } 
} 
