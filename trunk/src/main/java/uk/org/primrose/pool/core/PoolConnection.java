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
 
import uk.org.primrose.*; 
 
import java.sql.*; 
 
import java.util.Properties; 
 
 
public class PoolConnection implements Connection { 
    private Connection conn = null; 
    private ConnectionHolder connHolder = null; 
 
    protected PoolConnection(Connection conn, ConnectionHolder connHolder) { 
        this.conn = conn; 
        this.connHolder = connHolder; 
    } 
 
    /** 
     * Check for and close any unclosed result sets 
     */ 
    private void checkAndCloseResultSets() { 
        // Check the resultsets are all closed 
        if (connHolder.resultsetObjects!=null && connHolder.resultsetObjects.size() > 0) { 
            try { 
                throw new Exception(); 
            } catch (Exception e) { 
                StackTraceElement[] els = e.getStackTrace(); 
                StringBuffer stack = new StringBuffer(); 
 
                for (StackTraceElement el : els) { 
                    stack.append("\t"); 
                    stack.append(el.toString()); 
                    stack.append("\n"); 
                } 
 
                String message = "[PoolConnection@" + this.hashCode() + "@" + 
                    connHolder.poolName + "] close() : Got " + 
                    connHolder.resultsetObjects.size() + 
                    " unclosed ResultSets !\n" + stack.toString(); 
                connHolder.logger.email(Constants.UNCLOSED_EVENT, message); 
                connHolder.logger.error(message); 
            } 
 
            while (!connHolder.resultsetObjects.empty()) { 
                PoolResultSet rs = connHolder.resultsetObjects.pop(); 
                connHolder.logger.error("[PoolConnection@" + this.hashCode() + 
                    "@" + connHolder.poolName + "] close() : ResultSet@" + 
                    rs.hashCode() + " used but not closed (I'm closing it)"); 
 
                try { 
                    rs.closeNoPop(); 
                } catch (SQLException sqle) { 
                    connHolder.logger.printStackTrace(sqle); 
                } 
            } 
        } 
    } 
 
    /** 
     * Check for and close any unclosed statements 
     */ 
    private void checkAndCloseStatements() { 
        // Check the statements are all closed 
        if (connHolder.statementObjects!=null && connHolder.statementObjects.size() > 0) { 
            try { 
                throw new Exception(); 
            } catch (Exception e) { 
                StackTraceElement[] els = e.getStackTrace(); 
                StringBuffer stack = new StringBuffer(); 
 
                for (StackTraceElement el : els) { 
                    stack.append("\t"); 
                    stack.append(el.toString()); 
                    stack.append("\n"); 
                } 
 
                String message = "[PoolConnection@" + this.hashCode() + "@" + 
                    connHolder.poolName + "] close() : Got " + 
                    connHolder.statementObjects.size() + 
                    " unclosed [Callable/Prepared]Statements !\n" + 
                    stack.toString(); 
                connHolder.logger.email(Constants.UNCLOSED_EVENT, message); 
                connHolder.logger.error(message); 
            } 
 
            while (!connHolder.statementObjects.empty()) { 
                PoolStatement s = connHolder.statementObjects.pop(); 
                connHolder.logger.error("[PoolConnection@" + this.hashCode() + 
                    "@" + connHolder.poolName + 
                    "] close() : [Callable/Prepared]Statement@" + s.hashCode() + 
                    " used but not closed (I'm closing it)"); 
 
                try { 
                    s.closeNoPop(); 
                } catch (SQLException sqle) { 
                    connHolder.logger.printStackTrace(sqle); 
                } 
            } 
        } 
    } 
 
    public void close() { 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[PoolConnection@" + this.hashCode() + "@" + 
                connHolder.poolName + "] close() called ..."); 
        } 
 
        //synchronized(connHolder.lock) { 
 
        // Check and close down any unclosed statements/cursors 
        checkAndCloseStatements(); 
        checkAndCloseResultSets(); 
 
        // Check for more than one closes on this ... 
        if ((connHolder.numberOfCloses + 1) > connHolder.numberOfOpens) { 
            connHolder.logger.verbose("[PoolConnection@" + this.hashCode() + 
                "@" + connHolder.poolName + 
                "] close() called twice - this could have serious implications on your pooling ..."); 
 
            return; 
        } 
 
        // set some info for the log 
        connHolder.numberOfCloses++; 
        connHolder.lastUsedTimestamp = System.currentTimeMillis(); 
        connHolder.lastSql = connHolder.sql; 
        connHolder.lastSqlTook = (System.currentTimeMillis() - 
            connHolder.connOpenedDate); 
 
        // If when called, we should just close the real connection 
        // then do so, else set the status to inactive 
        if (connHolder.closeBehaviour == Pool.ON_CLOSE_SHOULD_DIE) { 
            connHolder.myPool.connections.remove(connHolder); 
 
            try { 
                if (DebugLogger.getEnabled()) { 
                    DebugLogger.log("[PoolConnection@" + this.hashCode() + "@" + 
                        connHolder.poolName + 
                        "] close() closing physical connection (Pool.ON_CLOSE_SHOULD_DIE=true)"); 
                } 
 
                this.closePhysical(); 
                connHolder.sql = "connection_dumped"; 
                connHolder.status = Pool.CONNECTION_DUMPED; 
            } catch (SQLException e) { 
                connHolder.logger.printStackTrace(e); 
            } 
        } else { 
            connHolder.sql = "none"; 
            connHolder.connOpenedDate = 0L; 
            connHolder.status = Pool.CONNECTION_INACTIVE; 
        } 
 
        //} 
        connHolder.logger.verbose("[Pool@" + connHolder.poolName + ",id=" + 
            connHolder.id + "] close() on conn hashcode " + 
            connHolder.conn.hashCode() + ", SQL(took " + 
            connHolder.lastSqlTook + " ms) : " + connHolder.lastSql); 
 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[PoolConnection@" + this.hashCode() + "@" + 
                connHolder.poolName + "] close() leaving ..."); 
        } 
    } 
 
    /** 
     * Execute some SQL to check if the connection is OK (if user configured pool to do this) 
     * @param checkSQL 
     * @return true for OK, false for broken 
     */ 
    protected boolean runCheckSQL(String checkSQL) { 
        Statement s = null; 
        ResultSet rs = null; 
 
        try { 
            s = conn.createStatement(); 
            rs = s.executeQuery(checkSQL); 
 
            if (!conn.getAutoCommit()) { 
                conn.commit(); 
            } 
        } catch (Exception e) { 
            connHolder.logger.printStackTrace(e); 
            connHolder.logger.error("[Pool@" + connHolder.poolName + 
                "] checkIfConnectionIsValid() : SQL Check failed."); 
 
            return false; 
        } finally { 
            try { 
                if (rs != null) { 
                    rs.close(); 
                } 
 
                if (s != null) { 
                    s.close(); 
                } 
            } catch (SQLException sqle) { /*don't care*/ 
            } 
        } 
 
        return true; 
    } 
 
    /** 
     * Close the underlying connection 
     * @throws SQLException 
     */ 
    protected void closePhysical() throws SQLException { 
         
        this.conn.close(); 
    } 
 
    /** 
     * Close the underlying connection asynchronously, 
     * i.e. does not block new connections to the pool  
     * if closing the physical connection takes a long time. 
     *  
     * @throws SQLException 
     */ 
    protected void closePhysicalAsynch() throws SQLException { 
         
        new PoolConnectionCloseThread().start(); 
    } 
     
    /** 
    *        Get the real underlying connection (as created by your driver). 
    *        Only ever use this if you are using a non-JDBC standard driver method. 
    *        And be very careful when using it. Make sure you close the pool connection - not the real one 
    *        Eg : 
    * 
    *        // Get a pooled connection from primrose 
    *        Connection poolconn = getConnection(Constants.DB_POOL_NAME); 
    * 
    *        // Get a real connection from it 
    *        // Do NOT close the real connection - close the pooled one (c) 
    *        Connection rc = ((uk.org.primrose.pool.core.PoolConnection)poolconn).getRealConnection(); 
    * 
    *        // Do some stuff 
    * 
    *        // Close the pooled connection 
    *        poolconn.close(); 
    * 
    */ 
    public Connection getRealConnection() { 
        return conn; 
    } 
 
    public Statement createStatement() throws SQLException { 
        connHolder.numberOfJDBCStatementsRun++; 
 
        PoolStatement ps = new PoolStatement(conn.createStatement(), connHolder); 
 
        return ps; 
    } 
 
    public PreparedStatement prepareStatement(String sql) 
        throws SQLException { 
        connHolder.numberOfJDBCPreparedStatementsRun++; 
 
        PoolPreparedStatement pps = new PoolPreparedStatement(conn.prepareStatement( 
                    sql), connHolder); 
        this.connHolder.sql = sql; 
 
        return pps; 
    } 
 
    public CallableStatement prepareCall(String sql) throws SQLException { 
        connHolder.numberOfJDBCCallableStatementsRun++; 
 
        PoolCallableStatement pcs = new PoolCallableStatement(conn.prepareCall( 
                    sql), connHolder); 
        this.connHolder.sql = sql; 
 
        return pcs; 
    } 
 
    public String nativeSQL(String sql) throws SQLException { 
        this.connHolder.sql = sql; 
 
        return conn.nativeSQL(sql); 
    } 
 
    public void setAutoCommit(boolean autoCommit) throws SQLException { 
        conn.setAutoCommit(autoCommit); 
    } 
 
    public boolean getAutoCommit() throws SQLException { 
        return conn.getAutoCommit(); 
    } 
 
    public void commit() throws SQLException { 
        conn.commit(); 
    } 
 
    public void rollback() throws SQLException { 
        conn.rollback(); 
    } 
 
    public boolean isClosed() { 
        if (conn == null) { 
            return false; 
        } 
 
        try { 
            return conn.isClosed(); 
        } catch (SQLException sqle) { 
            sqle.printStackTrace(System.err); 
 
            return true; 
        } 
    } 
 
    public DatabaseMetaData getMetaData() throws SQLException { 
        return conn.getMetaData(); 
    } 
 
    public void setReadOnly(boolean readOnly) throws SQLException { 
        conn.setReadOnly(readOnly); 
    } 
 
    public boolean isReadOnly() throws SQLException { 
        return conn.isReadOnly(); 
    } 
 
    public void setCatalog(String catalog) throws SQLException { 
        conn.setCatalog(catalog); 
    } 
 
    public String getCatalog() throws SQLException { 
        return conn.getCatalog(); 
    } 
 
    public void setTransactionIsolation(int level) throws SQLException { 
        conn.setTransactionIsolation(level); 
    } 
 
    public int getTransactionIsolation() throws SQLException { 
        return conn.getTransactionIsolation(); 
    } 
 
    public SQLWarning getWarnings() throws SQLException { 
        return conn.getWarnings(); 
    } 
 
    public void clearWarnings() throws SQLException { 
        conn.clearWarnings(); 
    } 
 
    public Statement createStatement(int resultSetType, int resultSetConcurrency) 
        throws SQLException { 
        connHolder.numberOfJDBCPreparedStatementsRun++; 
 
        PoolStatement ps = new PoolStatement(conn.createStatement( 
                    resultSetType, resultSetConcurrency), connHolder); 
        this.connHolder.sql = ""; 
 
        return ps; 
    } 
 
    public PreparedStatement prepareStatement(String sql, int resultSetType, 
        int resultSetConcurrency) throws SQLException { 
        connHolder.numberOfJDBCPreparedStatementsRun++; 
 
        PoolPreparedStatement pps = new PoolPreparedStatement(conn.prepareStatement( 
                    sql, resultSetType, resultSetConcurrency), connHolder); 
        this.connHolder.sql = sql; 
 
        return pps; 
    } 
 
    public CallableStatement prepareCall(String sql, int resultSetType, 
        int resultSetConcurrency) throws SQLException { 
        connHolder.numberOfJDBCCallableStatementsRun++; 
 
        PoolCallableStatement pcs = new PoolCallableStatement(conn.prepareCall( 
                    sql, resultSetType, resultSetConcurrency), connHolder); 
        this.connHolder.sql = sql; 
 
        return pcs; 
    } 
 
    public java.util.Map<String, Class<?>> getTypeMap() 
        throws SQLException { 
        return conn.getTypeMap(); 
    } 
 
    public void setTypeMap(java.util.Map<String, Class<?>> map) 
        throws SQLException { 
        conn.setTypeMap(map); 
    } 
 
    public void setHoldability(int holdability) throws SQLException { 
        conn.setHoldability(holdability); 
    } 
 
    public int getHoldability() throws SQLException { 
        return conn.getHoldability(); 
    } 
 
    public Savepoint setSavepoint() throws SQLException { 
        return conn.setSavepoint(); 
    } 
 
    public Savepoint setSavepoint(String name) throws SQLException { 
        return conn.setSavepoint(name); 
    } 
 
    public void rollback(Savepoint savepoint) throws SQLException { 
        conn.rollback(savepoint); 
    } 
 
    public void releaseSavepoint(Savepoint savepoint) throws SQLException { 
        conn.releaseSavepoint(savepoint); 
    } 
 
    public Statement createStatement(int resultSetType, 
        int resultSetConcurrency, int resultSetHoldability) 
        throws SQLException { 
        connHolder.numberOfJDBCStatementsRun++; 
 
        PoolStatement ps = new PoolStatement(conn.createStatement( 
                    resultSetType, resultSetConcurrency, resultSetHoldability), 
                connHolder); 
 
        return ps; 
    } 
 
    public PreparedStatement prepareStatement(String sql, int resultSetType, 
        int resultSetConcurrency, int resultSetHoldability) 
        throws SQLException { 
        connHolder.numberOfJDBCPreparedStatementsRun++; 
 
        PoolPreparedStatement pps = new PoolPreparedStatement(conn.prepareStatement( 
                    sql, resultSetType, resultSetConcurrency, 
                    resultSetHoldability), connHolder); 
        this.connHolder.sql = sql; 
 
        return pps; 
    } 
 
    public CallableStatement prepareCall(String sql, int resultSetType, 
        int resultSetConcurrency, int resultSetHoldability) 
        throws SQLException { 
        connHolder.numberOfJDBCCallableStatementsRun++; 
 
        PoolCallableStatement pcs = new PoolCallableStatement(conn.prepareCall( 
                    sql, resultSetType, resultSetConcurrency, 
                    resultSetHoldability), connHolder); 
        this.connHolder.sql = sql; 
 
        return pcs; 
    } 
 
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) 
        throws SQLException { 
        connHolder.numberOfJDBCPreparedStatementsRun++; 
 
        PoolPreparedStatement pps = new PoolPreparedStatement(conn.prepareStatement( 
                    sql, autoGeneratedKeys), connHolder); 
        this.connHolder.sql = sql; 
 
        return pps; 
    } 
 
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) 
        throws SQLException { 
        connHolder.numberOfJDBCPreparedStatementsRun++; 
 
        PoolPreparedStatement pps = new PoolPreparedStatement(conn.prepareStatement( 
                    sql, columnIndexes), connHolder); 
        this.connHolder.sql = sql; 
 
        return pps; 
    } 
 
    public PreparedStatement prepareStatement(String sql, String[] columnNames) 
        throws SQLException { 
        connHolder.numberOfJDBCPreparedStatementsRun++; 
 
        PoolPreparedStatement pps = new PoolPreparedStatement(conn.prepareStatement( 
                    sql, columnNames), connHolder); 
        this.connHolder.sql = sql; 
 
        return pps; 
    } 
 
    @Override 
    public Array createArrayOf(String arg0, Object[] arg1) 
        throws SQLException { 
        return conn.createArrayOf(arg0, arg1); 
    } 
 
    @Override 
    public Blob createBlob() throws SQLException { 
        return conn.createBlob(); 
    } 
 
    @Override 
    public Clob createClob() throws SQLException { 
        return conn.createClob(); 
    } 
 
    @Override 
    public NClob createNClob() throws SQLException { 
        return conn.createNClob(); 
    } 
 
    @Override 
    public SQLXML createSQLXML() throws SQLException { 
        return conn.createSQLXML(); 
    } 
 
    @Override 
    public Struct createStruct(String arg0, Object[] arg1) 
        throws SQLException { 
        return conn.createStruct(arg0, arg1); 
    } 
 
    @Override 
    public Properties getClientInfo() throws SQLException { 
        return conn.getClientInfo(); 
    } 
 
    @Override 
    public String getClientInfo(String arg0) throws SQLException { 
        return conn.getClientInfo(arg0); 
    } 
 
    @Override 
    public boolean isValid(int arg0) throws SQLException { 
        return conn.isValid(arg0); 
    } 
 
    @Override 
    public void setClientInfo(Properties arg0) throws SQLClientInfoException { 
        conn.setClientInfo(arg0); 
    } 
 
    @Override 
    public void setClientInfo(String arg0, String arg1) 
        throws SQLClientInfoException { 
        conn.setClientInfo(arg0, arg1); 
    } 
 
    @Override 
    public boolean isWrapperFor(Class<?> arg0) throws SQLException { 
        return conn.isWrapperFor(arg0); 
    } 
 
    @Override 
    public <T> T unwrap(Class<T> arg0) throws SQLException { 
        return conn.unwrap(arg0); 
    } 
     
    /** 
     * Thread instance used by PoolConnection to asynchronously 
     * close a physical DB connection and on success, remove 
     * the ConnectionHolder from the designated pool. 
     *  
     * This allows the PoolMonitor to release connections that  
     * have been held longer than iKillActiveConnectionsOverAge 
     * without locking out all new connections until this  
     * connection has been dropped. 
     *  
     * @author mretal 
     * 
     */ 
    private class PoolConnectionCloseThread extends Thread { 
         
        public void run() { 
 
            synchronized (connHolder.lock) { 
                connHolder.status = Pool.CONNECTION_CLOSING; 
            } 
 
            try 
            { 
                conn.close(); 
                 
                synchronized (connHolder.lock) { 
                    connHolder.status = Pool.CONNECTION_DUMPED; 
                    connHolder.removeFromPool(); 
                } 
            } 
            catch (SQLException e) 
            { 
                connHolder.logger.error("[Pool@" + connHolder.poolName + ",id=" + 
                        connHolder.id + "] failed to close() on conn hashcode " + 
                        connHolder.conn.hashCode() + " : " + connHolder.lastSql); 
                connHolder.logger.printStackTrace(e); 
            } 
        } 
    } 
} 
