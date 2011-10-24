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
package uk.org.primrose.pool.core; 
 
import uk.org.primrose.Logger; 
 
 
public class ConnectionHolder { 
    public StackTraceElement[] callStack = null; 
    public PoolConnection conn; 
    public String poolName = null; 
    public int status; 
    protected int closeBehaviour; 
    public String sql = "none"; 
    public String lastSql = "none"; 
    protected Pool.PoolLock lock; 
    public int numberOfOpens = 0; 
    public int numberOfCloses = 0; 
    public int numberOfJDBCCallableStatementsRun = 0; 
    public int numberOfJDBCPreparedStatementsRun = 0; 
    public int numberOfJDBCStatementsRun = 0; 
    public long connOpenedDate = 0L; 
    public long lastSqlTook = 0L; 
    protected java.util.Stack<PoolResultSet> resultsetObjects = null; 
    protected java.util.Stack<PoolStatement> statementObjects = null; 
    public long lastUsedTimestamp = System.currentTimeMillis(); 
    public Logger logger = null; 
    protected boolean bDumpConnectionOnSQLException = true; 
    protected Pool myPool = null; 
    public long id = -1; 
 
    public void removeFromPool() { 
        myPool.connections.remove(this); 
    } 
     
    public String toString() { 
        String s = "numberOfOpens(" + numberOfOpens + "), " + 
            "numberOfCloses(" + numberOfCloses + "), " + "lastSqlTook(" + 
            lastSqlTook + " ms), " + "numberOfOpens(" + numberOfOpens + "), " + 
            "status(" + PoolData.getStringStatus(status) + "), " + "conn(" + 
            conn + "), "; 
 
        return s; 
    } 
} 
