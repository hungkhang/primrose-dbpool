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
package uk.org.primrose.pool.datasource; 
 
import uk.org.primrose.Constants; 
import uk.org.primrose.pool.PoolException; 
import uk.org.primrose.pool.core.Pool; 
import uk.org.primrose.pool.core.PoolLoader; 
 
import java.io.PrintWriter; 
import java.io.Serializable; 
 
import java.sql.Connection; 
import java.sql.SQLException; 
 
import javax.sql.DataSource; 
 
 
public class PrimroseDataSource implements DataSource, Serializable { 
    /** 
     * 
     */ 
    private static final long serialVersionUID = -3545935342768603717L; 
    private String poolName = ""; 
 
    public PrimroseDataSource() { 
    } 
 
    public void setPoolName(String poolName) { 
        this.poolName = poolName; 
    } 
 
    public String getPoolName() { 
        return poolName; 
    } 
 
    /** 
    *   Attempts to establish a connection with the data source that this DataSource object represents. 
    */ 
    public Connection getConnection() throws SQLException { 
        Pool pool = PoolLoader.findExistingPool(poolName); 
 
        if (pool == null) { 
            throw new SQLException("Cannot find primrose pool under name '" + 
                poolName + "'"); 
        } 
 
        try { 
            return pool.getConnection(); 
        } catch (PoolException pe) { 
            pool.getLogger().printStackTrace(pe); 
            throw new SQLException(pe.toString()); 
        } 
    } 
 
    /** 
    *   Attempts to establish a connection with the data source that this DataSource object represents. 
    */ 
    public Connection getConnection(String username, String password) 
        throws SQLException { 
        return getConnection(); 
    } 
 
    /** 
    *   Gets the maximum time in seconds that this data source can wait while attempting to connect to a database. 
    */ 
    public int getLoginTimeout() { 
        return -1; 
    } 
 
    /** 
    *    Retrieves the log writer for this DataSource object. 
    */ 
    public PrintWriter getLogWriter() { 
        return null; 
    } 
 
    /** 
    *   Sets the maximum time in seconds that this data source will wait while attempting to connect to a database. 
    */ 
    public void setLoginTimeout(int seconds) { 
    } 
 
    /** 
    *   Sets the log writer for this DataSource object to the given java.io.PrintWriter object. 
    */ 
    public void setLogWriter(PrintWriter out) { 
    } 
 
    @Override 
    public boolean isWrapperFor(Class<?> arg0) throws SQLException { 
        throw new SQLException("Unimplemented in Primrose " + 
            Constants.VERSION); 
    } 
 
    @Override 
    public <T> T unwrap(Class<T> arg0) throws SQLException { 
        throw new SQLException("Unimplemented in Primrose " + 
            Constants.VERSION); 
    } 
} 
