/** 
 * Library name : Primrose - A Java Database Connection Pool. Published by Ben 
 * Keeping, http://primrose.org.uk . Copyright (C) 2004 Ben Keeping, 
 * primrose.org.uk Email: Use "Contact Us Form" on website This library is free 
 * software; you can redistribute it and/or modify it under the terms of the GNU 
 * Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or (at your option) any later version. 
 * This library is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License along with this library; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 */ 
package uk.org.primrose.pool.core; 
 
import java.io.InputStream; 
import java.io.Reader; 
 
import java.math.BigDecimal; 
 
import java.sql.Array; 
import java.sql.Blob; 
import java.sql.Clob; 
import java.sql.NClob; 
import java.sql.ParameterMetaData; 
import java.sql.PreparedStatement; 
import java.sql.Ref; 
import java.sql.ResultSet; 
import java.sql.ResultSetMetaData; 
import java.sql.RowId; 
import java.sql.SQLException; 
import java.sql.SQLXML; 
 
import java.util.Calendar; 
 
 
/** 
 * A wrapper for a vendor specific implementation of PreparedStatement. Allows 
 * for complete logging of SQL transactions, aswell as identifying unclosed 
 * statements before Connection close() calls. 
 */ 
public class PoolPreparedStatement extends PoolStatement 
    implements PreparedStatement { 
    PreparedStatement ps = null; 
 
    public PoolPreparedStatement(PreparedStatement ps, 
        ConnectionHolder connHolder) { 
        super(ps, connHolder); 
        this.ps = ps; 
    } 
 
    public PoolPreparedStatement() { 
        super(); 
    } 
 
    public ResultSet executeQuery() throws SQLException { 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                PoolResultSet prs = new PoolResultSet(ps.executeQuery(), 
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
            PoolResultSet prs = new PoolResultSet(ps.executeQuery(), 
                    this.connHolder, this); 
 
            return prs; 
        } 
    } 
 
    public int executeUpdate() throws SQLException { 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                return ps.executeUpdate(); 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on execute"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            return ps.executeUpdate(); 
        } 
    } 
 
    public void setNull(int parameterIndex, int sqlType) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + 
            sqlType + ")"); 
        ps.setNull(parameterIndex, sqlType); 
    } 
 
    public void setBoolean(int parameterIndex, boolean x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setBoolean(parameterIndex, x); 
    } 
 
    public void setByte(int parameterIndex, byte x) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setByte(parameterIndex, x); 
    } 
 
    public void setShort(int parameterIndex, short x) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setShort(parameterIndex, x); 
    } 
 
    public void setInt(int parameterIndex, int x) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setInt(parameterIndex, x); 
    } 
 
    public void setLong(int parameterIndex, long x) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setLong(parameterIndex, x); 
    } 
 
    public void setFloat(int parameterIndex, float x) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setFloat(parameterIndex, x); 
    } 
 
    public void setDouble(int parameterIndex, double x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setDouble(parameterIndex, x); 
    } 
 
    public void setBigDecimal(int parameterIndex, BigDecimal x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setBigDecimal(parameterIndex, x); 
    } 
 
    public void setString(int parameterIndex, String x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setString(parameterIndex, x); 
    } 
 
    public void setBytes(int parameterIndex, byte[] x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setBytes(parameterIndex, x); 
    } 
 
    public void setDate(int parameterIndex, java.sql.Date x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setDate(parameterIndex, x); 
    } 
 
    public void setTime(int parameterIndex, java.sql.Time x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setTime(parameterIndex, x); 
    } 
 
    public void setTimestamp(int parameterIndex, java.sql.Timestamp x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setTimestamp(parameterIndex, x); 
    } 
 
    public void setAsciiStream(int parameterIndex, java.io.InputStream x, 
        int length) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setAsciiStream(parameterIndex, x, length); 
    } 
 
    @SuppressWarnings("deprecation") 
    public void setUnicodeStream(int parameterIndex, java.io.InputStream x, 
        int length) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setUnicodeStream(parameterIndex, x, length); 
    } 
 
    public void setBinaryStream(int parameterIndex, java.io.InputStream x, 
        int length) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setBinaryStream(parameterIndex, x, length); 
    } 
 
    public void clearParameters() throws SQLException { 
        ps.clearParameters(); 
    } 
 
    public void setObject(int parameterIndex, Object x, int targetSqlType, 
        int scale) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setObject(parameterIndex, x, targetSqlType, scale); 
    } 
 
    public void setObject(int parameterIndex, Object x, int targetSqlType) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setObject(parameterIndex, x, targetSqlType); 
    } 
 
    public void setObject(int parameterIndex, Object x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setObject(parameterIndex, x); 
    } 
 
    public boolean execute() throws SQLException { 
        return ps.execute(); 
    } 
 
    public void addBatch() throws SQLException { 
        ps.addBatch(); 
    } 
 
    public void setCharacterStream(int parameterIndex, java.io.Reader reader, 
        int length) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + reader + 
            ")"); 
        ps.setCharacterStream(parameterIndex, reader, length); 
    } 
 
    public void setRef(int i, Ref x) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + i + "(" + x + ")"); 
        ps.setRef(i, x); 
    } 
 
    public void setBlob(int i, Blob x) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + i + "(" + x + ")"); 
        ps.setBlob(i, x); 
    } 
 
    public void setClob(int i, Clob x) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + i + "(" + x + ")"); 
        ps.setClob(i, x); 
    } 
 
    public void setArray(int i, Array x) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + i + "(" + x + ")"); 
        ps.setArray(i, x); 
    } 
 
    public ResultSetMetaData getMetaData() throws SQLException { 
        return ps.getMetaData(); 
    } 
 
    public void setDate(int parameterIndex, java.sql.Date x, Calendar cal) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setDate(parameterIndex, x, cal); 
    } 
 
    public void setTime(int parameterIndex, java.sql.Time x, Calendar cal) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setTime(parameterIndex, x, cal); 
    } 
 
    public void setTimestamp(int parameterIndex, java.sql.Timestamp x, 
        Calendar cal) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setTimestamp(parameterIndex, x, cal); 
    } 
 
    public void setNull(int parameterIndex, int sqlType, String typeName) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + 
            typeName + ")"); 
        ps.setNull(parameterIndex, sqlType, typeName); 
    } 
 
    public void setURL(int parameterIndex, java.net.URL x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterIndex + "(" + x + 
            ")"); 
        ps.setURL(parameterIndex, x); 
    } 
 
    public ParameterMetaData getParameterMetaData() throws SQLException { 
        return ps.getParameterMetaData(); 
    } 
 
    @Override 
    public void setAsciiStream(int arg0, InputStream arg1) 
        throws SQLException { 
        ps.setAsciiStream(arg0, arg1); 
    } 
 
    @Override 
    public void setAsciiStream(int arg0, InputStream arg1, long arg2) 
        throws SQLException { 
        ps.setAsciiStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setBinaryStream(int arg0, InputStream arg1) 
        throws SQLException { 
        ps.setBinaryStream(arg0, arg1); 
    } 
 
    @Override 
    public void setBinaryStream(int arg0, InputStream arg1, long arg2) 
        throws SQLException { 
        ps.setBinaryStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setBlob(int arg0, InputStream arg1) throws SQLException { 
        ps.setBlob(arg0, arg1); 
    } 
 
    @Override 
    public void setBlob(int arg0, InputStream arg1, long arg2) 
        throws SQLException { 
        ps.setBlob(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setCharacterStream(int arg0, Reader arg1) 
        throws SQLException { 
        ps.setCharacterStream(arg0, arg1); 
    } 
 
    @Override 
    public void setCharacterStream(int arg0, Reader arg1, long arg2) 
        throws SQLException { 
        ps.setCharacterStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setClob(int arg0, Reader arg1) throws SQLException { 
        ps.setClob(arg0, arg1); 
    } 
 
    @Override 
    public void setClob(int arg0, Reader arg1, long arg2) 
        throws SQLException { 
        ps.setClob(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setNCharacterStream(int arg0, Reader arg1) 
        throws SQLException { 
        ps.setNCharacterStream(arg0, arg1); 
    } 
 
    @Override 
    public void setNCharacterStream(int arg0, Reader arg1, long arg2) 
        throws SQLException { 
        ps.setNCharacterStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setNClob(int arg0, NClob arg1) throws SQLException { 
        ps.setNClob(arg0, arg1); 
    } 
 
    @Override 
    public void setNClob(int arg0, Reader arg1) throws SQLException { 
        ps.setNClob(arg0, arg1); 
    } 
 
    @Override 
    public void setNClob(int arg0, Reader arg1, long arg2) 
        throws SQLException { 
        ps.setNClob(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setNString(int arg0, String arg1) throws SQLException { 
        ps.setNString(arg0, arg1); 
    } 
 
    @Override 
    public void setRowId(int arg0, RowId arg1) throws SQLException { 
        ps.setRowId(arg0, arg1); 
    } 
 
    @Override 
    public void setSQLXML(int arg0, SQLXML arg1) throws SQLException { 
        ps.setSQLXML(arg0, arg1); 
    } 
} 
