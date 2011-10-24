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
import java.sql.Ref; 
import java.sql.ResultSet; 
import java.sql.ResultSetMetaData; 
import java.sql.RowId; 
import java.sql.SQLException; 
import java.sql.SQLWarning; 
import java.sql.SQLXML; 
import java.sql.Statement; 
 
import java.util.Calendar; 
 
 
/** 
 * A wrapper for a vendor specific implementation of ResultSet. Allows for 
 * identifying unclosed statements before Connection close() calls. 
 */ 
public class PoolResultSet implements ResultSet { 
    ResultSet rs = null; 
    ConnectionHolder connHolder = null; 
    PoolStatement parentStatement = null; 
 
    public PoolResultSet(ResultSet rs, ConnectionHolder connHolder, 
        PoolStatement parentStatement) { 
        this.connHolder = connHolder; 
        connHolder.resultsetObjects.push(this); 
        this.rs = rs; 
        this.parentStatement = parentStatement; 
    } 
 
    protected void closeNoPop() throws SQLException { 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                rs.close(); 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on close()"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            rs.close(); 
        } 
    } 
 
    public void close() throws SQLException { 
        for (int i = 0; i < connHolder.resultsetObjects.size(); i++) { 
            PoolResultSet trs = connHolder.resultsetObjects.get(i); 
 
            if (trs == this) { 
                connHolder.resultsetObjects.remove(this); 
 
                break; 
            } 
        } 
 
        if (this.connHolder.bDumpConnectionOnSQLException) { 
            try { 
                rs.close(); 
            } catch (SQLException sqle) { 
                this.connHolder.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                this.connHolder.logger.warn( 
                    "Closing connection due to SQLException on close()"); 
                this.connHolder.myPool.notifyExceptionEvent(); 
                throw sqle; 
            } 
        } else { 
            rs.close(); 
        } 
    } 
 
    public boolean next() throws SQLException { 
        return rs.next(); 
    } 
 
    public boolean wasNull() throws SQLException { 
        return rs.wasNull(); 
    } 
 
    public String getString(int columnIndex) throws SQLException { 
        return rs.getString(columnIndex); 
    } 
 
    public boolean getBoolean(int columnIndex) throws SQLException { 
        return rs.getBoolean(columnIndex); 
    } 
 
    public byte getByte(int columnIndex) throws SQLException { 
        return rs.getByte(columnIndex); 
    } 
 
    public short getShort(int columnIndex) throws SQLException { 
        return rs.getShort(columnIndex); 
    } 
 
    public int getInt(int columnIndex) throws SQLException { 
        return rs.getInt(columnIndex); 
    } 
 
    public long getLong(int columnIndex) throws SQLException { 
        return rs.getLong(columnIndex); 
    } 
 
    public float getFloat(int columnIndex) throws SQLException { 
        return rs.getFloat(columnIndex); 
    } 
 
    public double getDouble(int columnIndex) throws SQLException { 
        return rs.getDouble(columnIndex); 
    } 
 
    @SuppressWarnings("deprecation") 
    public BigDecimal getBigDecimal(int columnIndex, int scale) 
        throws SQLException { 
        return rs.getBigDecimal(columnIndex, scale); 
    } 
 
    public byte[] getBytes(int columnIndex) throws SQLException { 
        return rs.getBytes(columnIndex); 
    } 
 
    public java.sql.Date getDate(int columnIndex) throws SQLException { 
        return rs.getDate(columnIndex); 
    } 
 
    public java.sql.Time getTime(int columnIndex) throws SQLException { 
        return rs.getTime(columnIndex); 
    } 
 
    public java.sql.Timestamp getTimestamp(int columnIndex) 
        throws SQLException { 
        return rs.getTimestamp(columnIndex); 
    } 
 
    public java.io.InputStream getAsciiStream(int columnIndex) 
        throws SQLException { 
        return rs.getAsciiStream(columnIndex); 
    } 
 
    @SuppressWarnings("deprecation") 
    public java.io.InputStream getUnicodeStream(int columnIndex) 
        throws SQLException { 
        return rs.getUnicodeStream(columnIndex); 
    } 
 
    public java.io.InputStream getBinaryStream(int columnIndex) 
        throws SQLException { 
        return rs.getBinaryStream(columnIndex); 
    } 
 
    public String getString(String columnName) throws SQLException { 
        return rs.getString(columnName); 
    } 
 
    public boolean getBoolean(String columnName) throws SQLException { 
        return rs.getBoolean(columnName); 
    } 
 
    public byte getByte(String columnName) throws SQLException { 
        return rs.getByte(columnName); 
    } 
 
    public short getShort(String columnName) throws SQLException { 
        return rs.getShort(columnName); 
    } 
 
    public int getInt(String columnName) throws SQLException { 
        return rs.getInt(columnName); 
    } 
 
    public long getLong(String columnName) throws SQLException { 
        return rs.getLong(columnName); 
    } 
 
    public float getFloat(String columnName) throws SQLException { 
        return rs.getFloat(columnName); 
    } 
 
    public double getDouble(String columnName) throws SQLException { 
        return rs.getDouble(columnName); 
    } 
 
    public BigDecimal getBigDecimal(String columnName, int scale) 
        throws SQLException { 
        return rs.getBigDecimal(columnName); 
    } 
 
    public byte[] getBytes(String columnName) throws SQLException { 
        return rs.getBytes(columnName); 
    } 
 
    public java.sql.Date getDate(String columnName) throws SQLException { 
        return rs.getDate(columnName); 
    } 
 
    public java.sql.Time getTime(String columnName) throws SQLException { 
        return rs.getTime(columnName); 
    } 
 
    public java.sql.Timestamp getTimestamp(String columnName) 
        throws SQLException { 
        return rs.getTimestamp(columnName); 
    } 
 
    public java.io.InputStream getAsciiStream(String columnName) 
        throws SQLException { 
        return rs.getAsciiStream(columnName); 
    } 
 
    @SuppressWarnings("deprecation") 
    public java.io.InputStream getUnicodeStream(String columnName) 
        throws SQLException { 
        return rs.getUnicodeStream(columnName); 
    } 
 
    public java.io.InputStream getBinaryStream(String columnName) 
        throws SQLException { 
        return rs.getBinaryStream(columnName); 
    } 
 
    public SQLWarning getWarnings() throws SQLException { 
        return rs.getWarnings(); 
    } 
 
    public void clearWarnings() throws SQLException { 
        rs.clearWarnings(); 
    } 
 
    public String getCursorName() throws SQLException { 
        return rs.getCursorName(); 
    } 
 
    public ResultSetMetaData getMetaData() throws SQLException { 
        return rs.getMetaData(); 
    } 
 
    public Object getObject(int columnIndex) throws SQLException { 
        return rs.getObject(columnIndex); 
    } 
 
    public Object getObject(String columnName) throws SQLException { 
        return rs.getObject(columnName); 
    } 
 
    public int findColumn(String columnName) throws SQLException { 
        return rs.findColumn(columnName); 
    } 
 
    public java.io.Reader getCharacterStream(int columnIndex) 
        throws SQLException { 
        return rs.getCharacterStream(columnIndex); 
    } 
 
    public java.io.Reader getCharacterStream(String columnName) 
        throws SQLException { 
        return rs.getCharacterStream(columnName); 
    } 
 
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException { 
        return rs.getBigDecimal(columnIndex); 
    } 
 
    public BigDecimal getBigDecimal(String columnName) 
        throws SQLException { 
        return rs.getBigDecimal(columnName); 
    } 
 
    public boolean isBeforeFirst() throws SQLException { 
        return rs.isBeforeFirst(); 
    } 
 
    public boolean isAfterLast() throws SQLException { 
        return rs.isAfterLast(); 
    } 
 
    public boolean isFirst() throws SQLException { 
        return rs.isFirst(); 
    } 
 
    public boolean isLast() throws SQLException { 
        return rs.isLast(); 
    } 
 
    public void beforeFirst() throws SQLException { 
        rs.beforeFirst(); 
    } 
 
    public void afterLast() throws SQLException { 
        rs.afterLast(); 
    } 
 
    public boolean first() throws SQLException { 
        return rs.first(); 
    } 
 
    public boolean last() throws SQLException { 
        return rs.last(); 
    } 
 
    public int getRow() throws SQLException { 
        return rs.getRow(); 
    } 
 
    public boolean absolute(int row) throws SQLException { 
        return rs.absolute(row); 
    } 
 
    public boolean relative(int rows) throws SQLException { 
        return rs.relative(rows); 
    } 
 
    public boolean previous() throws SQLException { 
        return rs.previous(); 
    } 
 
    public void setFetchDirection(int direction) throws SQLException { 
        rs.setFetchDirection(direction); 
    } 
 
    public int getFetchDirection() throws SQLException { 
        return rs.getFetchDirection(); 
    } 
 
    public void setFetchSize(int rows) throws SQLException { 
        rs.setFetchSize(rows); 
    } 
 
    public int getFetchSize() throws SQLException { 
        return rs.getFetchSize(); 
    } 
 
    public int getConcurrency() throws SQLException { 
        return rs.getConcurrency(); 
    } 
 
    public boolean rowUpdated() throws SQLException { 
        return rs.rowUpdated(); 
    } 
 
    public int getType() throws SQLException { 
        return rs.getType(); 
    } 
 
    public boolean rowInserted() throws SQLException { 
        return rs.rowInserted(); 
    } 
 
    public boolean rowDeleted() throws SQLException { 
        return rs.rowDeleted(); 
    } 
 
    public void updateNull(int columnIndex) throws SQLException { 
        rs.updateNull(columnIndex); 
    } 
 
    public void updateBoolean(int columnIndex, boolean x) 
        throws SQLException { 
        rs.updateBoolean(columnIndex, x); 
    } 
 
    public void updateByte(int columnIndex, byte x) throws SQLException { 
        rs.updateByte(columnIndex, x); 
    } 
 
    public void updateShort(int columnIndex, short x) throws SQLException { 
        rs.updateShort(columnIndex, x); 
    } 
 
    public void updateInt(int columnIndex, int x) throws SQLException { 
        rs.updateInt(columnIndex, x); 
    } 
 
    public void updateLong(int columnIndex, long x) throws SQLException { 
        rs.updateLong(columnIndex, x); 
    } 
 
    public void updateFloat(int columnIndex, float x) throws SQLException { 
        rs.updateFloat(columnIndex, x); 
    } 
 
    public void updateDouble(int columnIndex, double x) 
        throws SQLException { 
        rs.updateDouble(columnIndex, x); 
    } 
 
    public void updateBigDecimal(int columnIndex, BigDecimal x) 
        throws SQLException { 
        rs.updateBigDecimal(columnIndex, x); 
    } 
 
    public void updateString(int columnIndex, String x) 
        throws SQLException { 
        rs.updateString(columnIndex, x); 
    } 
 
    public void updateBytes(int columnIndex, byte[] x) 
        throws SQLException { 
        rs.updateBytes(columnIndex, x); 
    } 
 
    public void updateDate(int columnIndex, java.sql.Date x) 
        throws SQLException { 
        rs.updateDate(columnIndex, x); 
    } 
 
    public void updateTime(int columnIndex, java.sql.Time x) 
        throws SQLException { 
        rs.updateTime(columnIndex, x); 
    } 
 
    public void updateTimestamp(int columnIndex, java.sql.Timestamp x) 
        throws SQLException { 
        rs.updateTimestamp(columnIndex, x); 
    } 
 
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, 
        int length) throws SQLException { 
        rs.updateAsciiStream(columnIndex, x, length); 
    } 
 
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, 
        int length) throws SQLException { 
        rs.updateBinaryStream(columnIndex, x, length); 
    } 
 
    public void updateCharacterStream(int columnIndex, java.io.Reader x, 
        int length) throws SQLException { 
        rs.updateCharacterStream(columnIndex, x, length); 
    } 
 
    public void updateObject(int columnIndex, Object x, int scale) 
        throws SQLException { 
        rs.updateObject(columnIndex, x, scale); 
    } 
 
    public void updateObject(int columnIndex, Object x) 
        throws SQLException { 
        rs.updateObject(columnIndex, x); 
    } 
 
    public void updateNull(String columnName) throws SQLException { 
        rs.updateNull(columnName); 
    } 
 
    public void updateBoolean(String columnName, boolean x) 
        throws SQLException { 
        rs.updateBoolean(columnName, x); 
    } 
 
    public void updateByte(String columnName, byte x) throws SQLException { 
        rs.updateByte(columnName, x); 
    } 
 
    public void updateShort(String columnName, short x) 
        throws SQLException { 
        rs.updateShort(columnName, x); 
    } 
 
    public void updateInt(String columnName, int x) throws SQLException { 
        rs.updateInt(columnName, x); 
    } 
 
    public void updateLong(String columnName, long x) throws SQLException { 
        rs.updateLong(columnName, x); 
    } 
 
    public void updateFloat(String columnName, float x) 
        throws SQLException { 
        rs.updateFloat(columnName, x); 
    } 
 
    public void updateDouble(String columnName, double x) 
        throws SQLException { 
        rs.updateDouble(columnName, x); 
    } 
 
    public void updateBigDecimal(String columnName, BigDecimal x) 
        throws SQLException { 
        rs.updateBigDecimal(columnName, x); 
    } 
 
    public void updateString(String columnName, String x) 
        throws SQLException { 
        rs.updateString(columnName, x); 
    } 
 
    public void updateBytes(String columnName, byte[] x) 
        throws SQLException { 
        rs.updateBytes(columnName, x); 
    } 
 
    public void updateDate(String columnName, java.sql.Date x) 
        throws SQLException { 
        rs.updateDate(columnName, x); 
    } 
 
    public void updateTime(String columnName, java.sql.Time x) 
        throws SQLException { 
        rs.updateTime(columnName, x); 
    } 
 
    public void updateTimestamp(String columnName, java.sql.Timestamp x) 
        throws SQLException { 
        rs.updateTimestamp(columnName, x); 
    } 
 
    public void updateAsciiStream(String columnName, java.io.InputStream x, 
        int length) throws SQLException { 
        rs.updateAsciiStream(columnName, x, length); 
    } 
 
    public void updateBinaryStream(String columnName, java.io.InputStream x, 
        int length) throws SQLException { 
        rs.updateBinaryStream(columnName, x, length); 
    } 
 
    public void updateCharacterStream(String columnName, java.io.Reader reader, 
        int length) throws SQLException { 
        rs.updateCharacterStream(columnName, reader, length); 
    } 
 
    public void updateObject(String columnName, Object x, int scale) 
        throws SQLException { 
        rs.updateObject(columnName, x, scale); 
    } 
 
    public void updateObject(String columnName, Object x) 
        throws SQLException { 
        rs.updateObject(columnName, x); 
    } 
 
    public void insertRow() throws SQLException { 
        rs.insertRow(); 
    } 
 
    public void updateRow() throws SQLException { 
        rs.updateRow(); 
    } 
 
    public void deleteRow() throws SQLException { 
        rs.deleteRow(); 
    } 
 
    public void refreshRow() throws SQLException { 
        rs.refreshRow(); 
    } 
 
    public void cancelRowUpdates() throws SQLException { 
        rs.cancelRowUpdates(); 
    } 
 
    public void moveToInsertRow() throws SQLException { 
        rs.moveToInsertRow(); 
    } 
 
    public void moveToCurrentRow() throws SQLException { 
        rs.moveToCurrentRow(); 
    } 
 
    public Statement getStatement() throws SQLException { 
        return parentStatement; 
 
        //return rs.getStatement(); altered for 3.0.12 
    } 
 
    public Object getObject(int i, 
        java.util.Map<java.lang.String, java.lang.Class<?>> map) 
        throws SQLException { 
        return rs.getObject(i, map); 
    } 
 
    public Ref getRef(int i) throws SQLException { 
        return rs.getRef(i); 
    } 
 
    public Blob getBlob(int i) throws SQLException { 
        return rs.getBlob(i); 
    } 
 
    public Clob getClob(int i) throws SQLException { 
        return rs.getClob(i); 
    } 
 
    public Array getArray(int i) throws SQLException { 
        return rs.getArray(i); 
    } 
 
    public Object getObject(String colName, 
        java.util.Map<java.lang.String, java.lang.Class<?>> map) 
        throws SQLException { 
        return rs.getObject(colName, map); 
    } 
 
    public Ref getRef(String colName) throws SQLException { 
        return rs.getRef(colName); 
    } 
 
    public Blob getBlob(String colName) throws SQLException { 
        return rs.getBlob(colName); 
    } 
 
    public Clob getClob(String colName) throws SQLException { 
        return rs.getClob(colName); 
    } 
 
    public Array getArray(String colName) throws SQLException { 
        return rs.getArray(colName); 
    } 
 
    public java.sql.Date getDate(int columnIndex, Calendar cal) 
        throws SQLException { 
        return rs.getDate(columnIndex, cal); 
    } 
 
    public java.sql.Date getDate(String columnName, Calendar cal) 
        throws SQLException { 
        return rs.getDate(columnName, cal); 
    } 
 
    public java.sql.Time getTime(int columnIndex, Calendar cal) 
        throws SQLException { 
        return rs.getTime(columnIndex, cal); 
    } 
 
    public java.sql.Time getTime(String columnName, Calendar cal) 
        throws SQLException { 
        return rs.getTime(columnName, cal); 
    } 
 
    public java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal) 
        throws SQLException { 
        return rs.getTimestamp(columnIndex, cal); 
    } 
 
    public java.sql.Timestamp getTimestamp(String columnName, Calendar cal) 
        throws SQLException { 
        return rs.getTimestamp(columnName, cal); 
    } 
 
    public java.net.URL getURL(int columnIndex) throws SQLException { 
        return rs.getURL(columnIndex); 
    } 
 
    public java.net.URL getURL(String columnName) throws SQLException { 
        return rs.getURL(columnName); 
    } 
 
    public void updateRef(int columnIndex, java.sql.Ref x) 
        throws SQLException { 
        rs.updateRef(columnIndex, x); 
    } 
 
    public void updateRef(String columnName, java.sql.Ref x) 
        throws SQLException { 
        rs.updateRef(columnName, x); 
    } 
 
    public void updateBlob(int columnIndex, java.sql.Blob x) 
        throws SQLException { 
        rs.updateBlob(columnIndex, x); 
    } 
 
    public void updateBlob(String columnName, java.sql.Blob x) 
        throws SQLException { 
        rs.updateBlob(columnName, x); 
    } 
 
    public void updateClob(int columnIndex, java.sql.Clob x) 
        throws SQLException { 
        rs.updateClob(columnIndex, x); 
    } 
 
    public void updateClob(String columnName, java.sql.Clob x) 
        throws SQLException { 
        rs.updateClob(columnName, x); 
    } 
 
    public void updateArray(int columnIndex, java.sql.Array x) 
        throws SQLException { 
        rs.updateArray(columnIndex, x); 
    } 
 
    public void updateArray(String columnName, java.sql.Array x) 
        throws SQLException { 
        rs.updateArray(columnName, x); 
    } 
 
    @Override 
    public int getHoldability() throws SQLException { 
        return rs.getHoldability(); 
    } 
 
    @Override 
    public Reader getNCharacterStream(int arg0) throws SQLException { 
        return rs.getNCharacterStream(arg0); 
    } 
 
    @Override 
    public Reader getNCharacterStream(String arg0) throws SQLException { 
        return rs.getNCharacterStream(arg0); 
    } 
 
    @Override 
    public NClob getNClob(int arg0) throws SQLException { 
        return rs.getNClob(arg0); 
    } 
 
    @Override 
    public NClob getNClob(String arg0) throws SQLException { 
        return rs.getNClob(arg0); 
    } 
 
    @Override 
    public String getNString(int arg0) throws SQLException { 
        return rs.getNString(arg0); 
    } 
 
    @Override 
    public String getNString(String arg0) throws SQLException { 
        return rs.getNString(arg0); 
    } 
 
    @Override 
    public RowId getRowId(int arg0) throws SQLException { 
        return rs.getRowId(arg0); 
    } 
 
    @Override 
    public RowId getRowId(String arg0) throws SQLException { 
        return rs.getRowId(arg0); 
    } 
 
    @Override 
    public SQLXML getSQLXML(int arg0) throws SQLException { 
        return rs.getSQLXML(arg0); 
    } 
 
    @Override 
    public SQLXML getSQLXML(String arg0) throws SQLException { 
        return rs.getSQLXML(arg0); 
    } 
 
    @Override 
    public boolean isClosed() throws SQLException { 
        return rs.isClosed(); 
    } 
 
    @Override 
    public void updateAsciiStream(int arg0, InputStream arg1) 
        throws SQLException { 
        rs.updateAsciiStream(arg0, arg1); 
    } 
 
    @Override 
    public void updateAsciiStream(String arg0, InputStream arg1) 
        throws SQLException { 
        rs.updateAsciiStream(arg0, arg1); 
    } 
 
    @Override 
    public void updateAsciiStream(int arg0, InputStream arg1, long arg2) 
        throws SQLException { 
        rs.updateAsciiStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateAsciiStream(String arg0, InputStream arg1, long arg2) 
        throws SQLException { 
        rs.updateAsciiStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateBinaryStream(int arg0, InputStream arg1) 
        throws SQLException { 
        rs.updateBinaryStream(arg0, arg1); 
    } 
 
    @Override 
    public void updateBinaryStream(String arg0, InputStream arg1) 
        throws SQLException { 
        rs.updateBinaryStream(arg0, arg1); 
    } 
 
    @Override 
    public void updateBinaryStream(int arg0, InputStream arg1, long arg2) 
        throws SQLException { 
        rs.updateBinaryStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateBinaryStream(String arg0, InputStream arg1, long arg2) 
        throws SQLException { 
        rs.updateBinaryStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateBlob(int arg0, InputStream arg1) 
        throws SQLException { 
        rs.updateBlob(arg0, arg1); 
    } 
 
    @Override 
    public void updateBlob(String arg0, InputStream arg1) 
        throws SQLException { 
        rs.updateBlob(arg0, arg1); 
    } 
 
    @Override 
    public void updateBlob(int arg0, InputStream arg1, long arg2) 
        throws SQLException { 
        rs.updateBlob(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateBlob(String arg0, InputStream arg1, long arg2) 
        throws SQLException { 
        rs.updateBlob(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateCharacterStream(int arg0, Reader arg1) 
        throws SQLException { 
        rs.updateCharacterStream(arg0, arg1); 
    } 
 
    @Override 
    public void updateCharacterStream(String arg0, Reader arg1) 
        throws SQLException { 
        rs.updateCharacterStream(arg0, arg1); 
    } 
 
    @Override 
    public void updateCharacterStream(int arg0, Reader arg1, long arg2) 
        throws SQLException { 
        rs.updateCharacterStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateCharacterStream(String arg0, Reader arg1, long arg2) 
        throws SQLException { 
        rs.updateCharacterStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateClob(int arg0, Reader arg1) throws SQLException { 
        rs.updateClob(arg0, arg1); 
    } 
 
    @Override 
    public void updateClob(String arg0, Reader arg1) throws SQLException { 
        rs.updateClob(arg0, arg1); 
    } 
 
    @Override 
    public void updateClob(int arg0, Reader arg1, long arg2) 
        throws SQLException { 
        rs.updateClob(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateClob(String arg0, Reader arg1, long arg2) 
        throws SQLException { 
        rs.updateClob(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateNCharacterStream(int arg0, Reader arg1) 
        throws SQLException { 
        rs.updateNCharacterStream(arg0, arg1); 
    } 
 
    @Override 
    public void updateNCharacterStream(String arg0, Reader arg1) 
        throws SQLException { 
        rs.updateNCharacterStream(arg0, arg1); 
    } 
 
    @Override 
    public void updateNCharacterStream(int arg0, Reader arg1, long arg2) 
        throws SQLException { 
        rs.updateNCharacterStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateNCharacterStream(String arg0, Reader arg1, long arg2) 
        throws SQLException { 
        rs.updateNCharacterStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateNClob(int arg0, NClob arg1) throws SQLException { 
        rs.updateNClob(arg0, arg1); 
    } 
 
    @Override 
    public void updateNClob(String arg0, NClob arg1) throws SQLException { 
        rs.updateNClob(arg0, arg1); 
    } 
 
    @Override 
    public void updateNClob(int arg0, Reader arg1) throws SQLException { 
        rs.updateNClob(arg0, arg1); 
    } 
 
    @Override 
    public void updateNClob(String arg0, Reader arg1) throws SQLException { 
        rs.updateNClob(arg0, arg1); 
    } 
 
    @Override 
    public void updateNClob(int arg0, Reader arg1, long arg2) 
        throws SQLException { 
        rs.updateNClob(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateNClob(String arg0, Reader arg1, long arg2) 
        throws SQLException { 
        rs.updateNClob(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void updateNString(int arg0, String arg1) throws SQLException { 
        rs.updateNString(arg0, arg1); 
    } 
 
    @Override 
    public void updateNString(String arg0, String arg1) 
        throws SQLException { 
        rs.updateNString(arg0, arg1); 
    } 
 
    @Override 
    public void updateRowId(int arg0, RowId arg1) throws SQLException { 
        rs.updateRowId(arg0, arg1); 
    } 
 
    @Override 
    public void updateRowId(String arg0, RowId arg1) throws SQLException { 
        rs.updateRowId(arg0, arg1); 
    } 
 
    @Override 
    public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException { 
        rs.updateSQLXML(arg0, arg1); 
    } 
 
    @Override 
    public void updateSQLXML(String arg0, SQLXML arg1) 
        throws SQLException { 
        rs.updateSQLXML(arg0, arg1); 
    } 
 
    @Override 
    public boolean isWrapperFor(Class<?> arg0) throws SQLException { 
        return rs.isWrapperFor(arg0); 
    } 
 
    @Override 
    public <T> T unwrap(Class<T> arg0) throws SQLException { 
        return rs.unwrap(arg0); 
    } 
} 
