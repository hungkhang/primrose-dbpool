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
 
import java.sql.*; 
 
 
/** 
*         A wrapper for a vendor specific implementation of Statement. 
*        Allows for complete logging of SQL transactions, aswell as identifying 
*        unclosed statements before Connection close() calls. 
*/ 
public class PoolStatement implements Statement { 
    Statement s = null; 
    ConnectionHolder connHolder = null; 
 
    public PoolStatement(Statement s, ConnectionHolder connHolder) { 
        this.connHolder = connHolder; 
        this.connHolder.statementObjects.push(this); 
        this.s = s; 
    } 
 
    public PoolStatement() { 
    } 
 
    protected void closeNoPop() throws SQLException { 
        s.close(); 
    } 
 
    public void close() throws SQLException { 
    	
        for (int i = 0; i < connHolder.statementObjects.size(); i++) { 
            PoolStatement ts = connHolder.statementObjects.get(i); 
 
            if (ts == this) { 
                connHolder.statementObjects.remove(this); 
 
                break; 
            } 
        } 
 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                s.close(); 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on close()"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            s.close(); 
        } 
    } 
 
    public ResultSet executeQuery(String sql) throws SQLException { 
        this.connHolder.sql = sql; 
 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                PoolResultSet prs = new PoolResultSet(s.executeQuery(sql), 
                        this.connHolder, this); 
 
                return prs; 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on execute"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            PoolResultSet prs = new PoolResultSet(s.executeQuery(sql), 
                    this.connHolder, this); 
 
            return prs; 
        } 
    } 
 
    public int executeUpdate(String sql) throws SQLException { 
        this.connHolder.sql = sql; 
 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                return s.executeUpdate(sql); 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on execute"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            return s.executeUpdate(sql); 
        } 
    } 
 
    public int executeUpdate(String sql, int autoGeneratedKeys) 
        throws SQLException { 
        this.connHolder.sql = sql; 
 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                return s.executeUpdate(sql, autoGeneratedKeys); 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on execute"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            return s.executeUpdate(sql, autoGeneratedKeys); 
        } 
    } 
 
    public int executeUpdate(String sql, int[] columnIndexes) 
        throws SQLException { 
        this.connHolder.sql = sql; 
 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                return s.executeUpdate(sql, columnIndexes); 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on execute"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            return s.executeUpdate(sql, columnIndexes); 
        } 
    } 
 
    public int executeUpdate(String sql, String[] columnNames) 
        throws SQLException { 
        this.connHolder.sql = sql; 
 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                return s.executeUpdate(sql, columnNames); 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on execute"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            return s.executeUpdate(sql, columnNames); 
        } 
    } 
 
    public boolean execute(String sql, int autoGeneratedKeys) 
        throws SQLException { 
        this.connHolder.sql = sql; 
 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                return s.execute(sql, autoGeneratedKeys); 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on execute"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            return s.execute(sql, autoGeneratedKeys); 
        } 
    } 
 
    public boolean execute(String sql, int[] columnIndexes) 
        throws SQLException { 
        this.connHolder.sql = sql; 
 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                return s.execute(sql, columnIndexes); 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on execute"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            return s.execute(sql, columnIndexes); 
        } 
    } 
 
    public boolean execute(String sql, String[] columnNames) 
        throws SQLException { 
        this.connHolder.sql = sql; 
 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                return s.execute(sql, columnNames); 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on execute"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            return s.execute(sql, columnNames); 
        } 
    } 
 
    public int[] executeBatch() throws SQLException { 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                return s.executeBatch(); 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on execute"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            return s.executeBatch(); 
        } 
    } 
 
    public int getMaxFieldSize() throws SQLException { 
        return s.getMaxFieldSize(); 
    } 
 
    public void setMaxFieldSize(int max) throws SQLException { 
        s.setMaxFieldSize(max); 
    } 
 
    public int getMaxRows() throws SQLException { 
        return s.getMaxRows(); 
    } 
 
    public void setMaxRows(int max) throws SQLException { 
        s.setMaxRows(max); 
    } 
 
    public void setEscapeProcessing(boolean enable) throws SQLException { 
        s.setEscapeProcessing(enable); 
    } 
 
    public int getQueryTimeout() throws SQLException { 
        return s.getQueryTimeout(); 
    } 
 
    public void setQueryTimeout(int seconds) throws SQLException { 
        s.setQueryTimeout(seconds); 
    } 
 
    public void cancel() throws SQLException { 
        s.cancel(); 
    } 
 
    public SQLWarning getWarnings() throws SQLException { 
        return s.getWarnings(); 
    } 
 
    public void clearWarnings() throws SQLException { 
        s.clearWarnings(); 
    } 
 
    public void setCursorName(String name) throws SQLException { 
        s.setCursorName(name); 
    } 
 
    public boolean execute(String sql) throws SQLException { 
        this.connHolder.sql = sql; 
 
        return s.execute(sql); 
    } 
 
    public ResultSet getResultSet() throws SQLException { 
        PoolResultSet prs = new PoolResultSet(s.getResultSet(), 
                this.connHolder, this); 
 
        return prs; 
    } 
 
    public int getUpdateCount() throws SQLException { 
        return s.getUpdateCount(); 
    } 
 
    public boolean getMoreResults() throws SQLException { 
        return s.getMoreResults(); 
    } 
 
    public void setFetchDirection(int direction) throws SQLException { 
        s.setFetchDirection(direction); 
    } 
 
    public int getFetchDirection() throws SQLException { 
        return s.getFetchDirection(); 
    } 
 
    public void setFetchSize(int rows) throws SQLException { 
        s.setFetchSize(rows); 
    } 
 
    public int getFetchSize() throws SQLException { 
        return s.getFetchSize(); 
    } 
 
    public int getResultSetConcurrency() throws SQLException { 
        return s.getResultSetConcurrency(); 
    } 
 
    public int getResultSetType() throws SQLException { 
        return s.getResultSetType(); 
    } 
 
    public void addBatch(String sql) throws SQLException { 
        s.addBatch(sql); 
    } 
 
    public void clearBatch() throws SQLException { 
        s.clearBatch(); 
    } 
 
    public Connection getConnection() throws SQLException { 
        return s.getConnection(); 
    } 
 
    public boolean getMoreResults(int current) throws SQLException { 
        return s.getMoreResults(current); 
    } 
 
    public ResultSet getGeneratedKeys() throws SQLException { 
        PoolResultSet prs = new PoolResultSet(s.getGeneratedKeys(), 
                this.connHolder, this); 
 
        return prs; 
    } 
 
    public int getResultSetHoldability() throws SQLException { 
        return s.getResultSetHoldability(); 
    } 
 
    @Override 
    public boolean isClosed() throws SQLException { 
        return s.isClosed(); 
    } 
 
    @Override 
    public boolean isPoolable() throws SQLException { 
        return s.isPoolable(); 
    } 
 
    @Override 
    public void setPoolable(boolean arg0) throws SQLException { 
        s.setPoolable(arg0); 
    } 
 
    @Override 
    public boolean isWrapperFor(Class<?> arg0) throws SQLException { 
        return s.isWrapperFor(arg0); 
    } 
 
    @Override 
    public <T> T unwrap(Class<T> arg0) throws SQLException { 
        return s.unwrap(arg0); 
    } 
} 