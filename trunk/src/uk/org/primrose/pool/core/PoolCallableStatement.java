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
 
import java.io.InputStream; 
import java.io.Reader; 
 
import java.math.BigDecimal; 
 
import java.sql.*; 
 
import java.util.Calendar; 
 
 
/** 
*         A wrapper for a vendor specific implementation of CallableStatement. 
*        Allows for complete logging of SQL transactions, aswell as identifying 
*        unclosed statements before Connection close() calls. 
*/ 
public class PoolCallableStatement extends PoolPreparedStatement 
    implements CallableStatement { 
    CallableStatement cs = null; 
 
    public PoolCallableStatement(CallableStatement cs, 
        ConnectionHolder connHolder) { 
        super(cs, connHolder); 
        this.cs = cs; 
    } 
 
    public PoolCallableStatement() { 
        super(); 
    } 
 
    public void registerOutParameter(int parameterIndex, int sqlType) 
        throws SQLException { 
        cs.registerOutParameter(parameterIndex, sqlType); 
    } 
 
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) 
        throws SQLException { 
        cs.registerOutParameter(parameterIndex, sqlType, scale); 
    } 
 
    public boolean wasNull() throws SQLException { 
        return cs.wasNull(); 
    } 
 
    public String getString(int parameterIndex) throws SQLException { 
        return cs.getString(parameterIndex); 
    } 
 
    public boolean getBoolean(int parameterIndex) throws SQLException { 
        return cs.getBoolean(parameterIndex); 
    } 
 
    public byte getByte(int parameterIndex) throws SQLException { 
        return cs.getByte(parameterIndex); 
    } 
 
    public short getShort(int parameterIndex) throws SQLException { 
        return cs.getShort(parameterIndex); 
    } 
 
    public int getInt(int parameterIndex) throws SQLException { 
        return cs.getInt(parameterIndex); 
    } 
 
    public long getLong(int parameterIndex) throws SQLException { 
        return cs.getLong(parameterIndex); 
    } 
 
    public float getFloat(int parameterIndex) throws SQLException { 
        return cs.getFloat(parameterIndex); 
    } 
 
    public double getDouble(int parameterIndex) throws SQLException { 
        return cs.getDouble(parameterIndex); 
    } 
 
    @SuppressWarnings("deprecation") 
    public BigDecimal getBigDecimal(int parameterIndex, int scale) 
        throws SQLException { 
        return cs.getBigDecimal(parameterIndex, scale); 
    } 
 
    public byte[] getBytes(int parameterIndex) throws SQLException { 
        return cs.getBytes(parameterIndex); 
    } 
 
    public java.sql.Date getDate(int parameterIndex) throws SQLException { 
        return cs.getDate(parameterIndex); 
    } 
 
    public java.sql.Time getTime(int parameterIndex) throws SQLException { 
        return cs.getTime(parameterIndex); 
    } 
 
    public java.sql.Timestamp getTimestamp(int parameterIndex) 
        throws SQLException { 
        return cs.getTimestamp(parameterIndex); 
    } 
 
    public Object getObject(int parameterIndex) throws SQLException { 
        return cs.getObject(parameterIndex); 
    } 
 
    public BigDecimal getBigDecimal(int parameterIndex) 
        throws SQLException { 
        return cs.getBigDecimal(parameterIndex); 
    } 
 
    public Object getObject(int i, 
        java.util.Map<java.lang.String, java.lang.Class<?>> map) 
        throws SQLException { 
        return cs.getObject(i, map); 
    } 
 
    public Ref getRef(int i) throws SQLException { 
        return cs.getRef(i); 
    } 
 
    public Blob getBlob(int i) throws SQLException { 
        return cs.getBlob(i); 
    } 
 
    public Clob getClob(int i) throws SQLException { 
        return cs.getClob(i); 
    } 
 
    public Array getArray(int i) throws SQLException { 
        return cs.getArray(i); 
    } 
 
    public java.sql.Date getDate(int parameterIndex, Calendar cal) 
        throws SQLException { 
        return cs.getDate(parameterIndex, cal); 
    } 
 
    public java.sql.Time getTime(int parameterIndex, Calendar cal) 
        throws SQLException { 
        return cs.getTime(parameterIndex, cal); 
    } 
 
    public java.sql.Timestamp getTimestamp(int parameterIndex, Calendar cal) 
        throws SQLException { 
        return cs.getTimestamp(parameterIndex, cal); 
    } 
 
    public void registerOutParameter(int paramIndex, int sqlType, 
        String typeName) throws SQLException { 
        cs.registerOutParameter(paramIndex, sqlType, typeName); 
    } 
 
    public void registerOutParameter(String parameterName, int sqlType) 
        throws SQLException { 
        cs.registerOutParameter(parameterName, sqlType); 
    } 
 
    public void registerOutParameter(String parameterName, int sqlType, 
        int scale) throws SQLException { 
        cs.registerOutParameter(parameterName, sqlType, scale); 
    } 
 
    public void registerOutParameter(String parameterName, int sqlType, 
        String typeName) throws SQLException { 
        cs.registerOutParameter(parameterName, sqlType, typeName); 
    } 
 
    public java.net.URL getURL(int parameterIndex) throws SQLException { 
        return cs.getURL(parameterIndex); 
    } 
 
    public void setURL(String parameterName, java.net.URL val) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + val + 
            ")"); 
        cs.setURL(parameterName, val); 
    } 
 
    public void setNull(String parameterName, int sqlType) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + sqlType + 
            ")"); 
        cs.setNull(parameterName, sqlType); 
    } 
 
    public void setBoolean(String parameterName, boolean x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setBoolean(parameterName, x); 
    } 
 
    public void setByte(String parameterName, byte x) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setByte(parameterName, x); 
    } 
 
    public void setShort(String parameterName, short x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setShort(parameterName, x); 
    } 
 
    public void setInt(String parameterName, int x) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setInt(parameterName, x); 
    } 
 
    public void setLong(String parameterName, long x) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setLong(parameterName, x); 
    } 
 
    public void setFloat(String parameterName, float x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setFloat(parameterName, x); 
    } 
 
    public void setDouble(String parameterName, double x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setDouble(parameterName, x); 
    } 
 
    public void setBigDecimal(String parameterName, BigDecimal x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setBigDecimal(parameterName, x); 
    } 
 
    public void setString(String parameterName, String x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setString(parameterName, x); 
    } 
 
    public void setBytes(String parameterName, byte[] x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setBytes(parameterName, x); 
    } 
 
    public void setDate(String parameterName, java.sql.Date x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setDate(parameterName, x); 
    } 
 
    public void setTime(String parameterName, java.sql.Time x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setTime(parameterName, x); 
    } 
 
    public void setTimestamp(String parameterName, java.sql.Timestamp x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setTimestamp(parameterName, x); 
    } 
 
    public void setAsciiStream(String parameterName, java.io.InputStream x, 
        int length) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setAsciiStream(parameterName, x, length); 
    } 
 
    public void setBinaryStream(String parameterName, java.io.InputStream x, 
        int length) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setBinaryStream(parameterName, x, length); 
    } 
 
    public void setObject(String parameterName, Object x, int targetSqlType, 
        int scale) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setObject(parameterName, x, targetSqlType, scale); 
    } 
 
    public void setObject(String parameterName, Object x, int targetSqlType) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setObject(parameterName, x, targetSqlType); 
    } 
 
    public void setObject(String parameterName, Object x) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setObject(parameterName, x); 
    } 
 
    public void setCharacterStream(String parameterName, java.io.Reader reader, 
        int length) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + reader + 
            ")"); 
        cs.setCharacterStream(parameterName, reader, length); 
    } 
 
    public void setDate(String parameterName, java.sql.Date x, Calendar cal) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setDate(parameterName, x, cal); 
    } 
 
    public void setTime(String parameterName, java.sql.Time x, Calendar cal) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setTime(parameterName, x, cal); 
    } 
 
    public void setTimestamp(String parameterName, java.sql.Timestamp x, 
        Calendar cal) throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + x + ")"); 
        cs.setTimestamp(parameterName, x, cal); 
    } 
 
    public void setNull(String parameterName, int sqlType, String typeName) 
        throws SQLException { 
        connHolder.sql = (connHolder.sql + " " + parameterName + "(" + 
            typeName + ")"); 
        cs.setNull(parameterName, sqlType, typeName); 
    } 
 
    public String getString(String parameterName) throws SQLException { 
        return cs.getString(parameterName); 
    } 
 
    public boolean getBoolean(String parameterName) throws SQLException { 
        return cs.getBoolean(parameterName); 
    } 
 
    public byte getByte(String parameterName) throws SQLException { 
        return cs.getByte(parameterName); 
    } 
 
    public short getShort(String parameterName) throws SQLException { 
        return cs.getShort(parameterName); 
    } 
 
    public int getInt(String parameterName) throws SQLException { 
        return cs.getInt(parameterName); 
    } 
 
    public long getLong(String parameterName) throws SQLException { 
        return cs.getLong(parameterName); 
    } 
 
    public float getFloat(String parameterName) throws SQLException { 
        return cs.getFloat(parameterName); 
    } 
 
    public double getDouble(String parameterName) throws SQLException { 
        return cs.getDouble(parameterName); 
    } 
 
    public byte[] getBytes(String parameterName) throws SQLException { 
        return cs.getBytes(parameterName); 
    } 
 
    public java.sql.Date getDate(String parameterName) 
        throws SQLException { 
        return cs.getDate(parameterName); 
    } 
 
    public java.sql.Time getTime(String parameterName) 
        throws SQLException { 
        return cs.getTime(parameterName); 
    } 
 
    public java.sql.Timestamp getTimestamp(String parameterName) 
        throws SQLException { 
        return cs.getTimestamp(parameterName); 
    } 
 
    public Object getObject(String parameterName) throws SQLException { 
        return cs.getObject(parameterName); 
    } 
 
    public BigDecimal getBigDecimal(String parameterName) 
        throws SQLException { 
        return cs.getBigDecimal(parameterName); 
    } 
 
    public Object getObject(String parameterName, 
        java.util.Map<java.lang.String, java.lang.Class<?>> map) 
        throws SQLException { 
        return cs.getObject(parameterName, map); 
    } 
 
    public Ref getRef(String parameterName) throws SQLException { 
        return cs.getRef(parameterName); 
    } 
 
    public Blob getBlob(String parameterName) throws SQLException { 
        return cs.getBlob(parameterName); 
    } 
 
    public Clob getClob(String parameterName) throws SQLException { 
        return cs.getClob(parameterName); 
    } 
 
    public Array getArray(String parameterName) throws SQLException { 
        return cs.getArray(parameterName); 
    } 
 
    public java.sql.Date getDate(String parameterName, Calendar cal) 
        throws SQLException { 
        return cs.getDate(parameterName, cal); 
    } 
 
    public java.sql.Time getTime(String parameterName, Calendar cal) 
        throws SQLException { 
        return cs.getTime(parameterName, cal); 
    } 
 
    public java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) 
        throws SQLException { 
        return cs.getTimestamp(parameterName, cal); 
    } 
 
    public java.net.URL getURL(String parameterName) throws SQLException { 
        return cs.getURL(parameterName); 
    } 
 
    @Override 
    public Reader getCharacterStream(int arg0) throws SQLException { 
        return cs.getCharacterStream(arg0); 
    } 
 
    @Override 
    public Reader getCharacterStream(String arg0) throws SQLException { 
        return cs.getCharacterStream(arg0); 
    } 
 
    @Override 
    public Reader getNCharacterStream(int arg0) throws SQLException { 
        return cs.getNCharacterStream(arg0); 
    } 
 
    @Override 
    public Reader getNCharacterStream(String arg0) throws SQLException { 
        return cs.getNCharacterStream(arg0); 
    } 
 
    @Override 
    public NClob getNClob(int arg0) throws SQLException { 
        return cs.getNClob(arg0); 
    } 
 
    @Override 
    public NClob getNClob(String arg0) throws SQLException { 
        return cs.getNClob(arg0); 
    } 
 
    @Override 
    public String getNString(int arg0) throws SQLException { 
        return cs.getNString(arg0); 
    } 
 
    @Override 
    public String getNString(String arg0) throws SQLException { 
        return cs.getNString(arg0); 
    } 
 
    @Override 
    public RowId getRowId(int arg0) throws SQLException { 
        return cs.getRowId(arg0); 
    } 
 
    @Override 
    public RowId getRowId(String arg0) throws SQLException { 
        return cs.getRowId(arg0); 
    } 
 
    @Override 
    public SQLXML getSQLXML(int arg0) throws SQLException { 
        return cs.getSQLXML(arg0); 
    } 
 
    @Override 
    public SQLXML getSQLXML(String arg0) throws SQLException { 
        return cs.getSQLXML(arg0); 
    } 
 
    @Override 
    public void setAsciiStream(String arg0, InputStream arg1) 
        throws SQLException { 
        cs.setAsciiStream(arg0, arg1); 
    } 
 
    @Override 
    public void setAsciiStream(String arg0, InputStream arg1, long arg2) 
        throws SQLException { 
        cs.setAsciiStream(arg0, arg1); 
    } 
 
    @Override 
    public void setBinaryStream(String arg0, InputStream arg1) 
        throws SQLException { 
        cs.setBinaryStream(arg0, arg1); 
    } 
 
    @Override 
    public void setBinaryStream(String arg0, InputStream arg1, long arg2) 
        throws SQLException { 
        cs.setBinaryStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setBlob(String arg0, Blob arg1) throws SQLException { 
        cs.setBlob(arg0, arg1); 
    } 
 
    @Override 
    public void setBlob(String arg0, InputStream arg1) 
        throws SQLException { 
        cs.setBlob(arg0, arg1); 
    } 
 
    @Override 
    public void setBlob(String arg0, InputStream arg1, long arg2) 
        throws SQLException { 
        cs.setBlob(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setCharacterStream(String arg0, Reader arg1) 
        throws SQLException { 
        cs.setCharacterStream(arg0, arg1); 
    } 
 
    @Override 
    public void setCharacterStream(String arg0, Reader arg1, long arg2) 
        throws SQLException { 
        cs.setCharacterStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setClob(String arg0, Clob arg1) throws SQLException { 
        cs.setClob(arg0, arg1); 
    } 
 
    @Override 
    public void setClob(String arg0, Reader arg1) throws SQLException { 
        cs.setClob(arg0, arg1); 
    } 
 
    @Override 
    public void setClob(String arg0, Reader arg1, long arg2) 
        throws SQLException { 
        cs.setClob(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setNCharacterStream(String arg0, Reader arg1) 
        throws SQLException { 
        cs.setNCharacterStream(arg0, arg1); 
    } 
 
    @Override 
    public void setNCharacterStream(String arg0, Reader arg1, long arg2) 
        throws SQLException { 
        cs.setNCharacterStream(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setNClob(String arg0, NClob arg1) throws SQLException { 
        cs.setNClob(arg0, arg1); 
    } 
 
    @Override 
    public void setNClob(String arg0, Reader arg1) throws SQLException { 
        cs.setNClob(arg0, arg1); 
    } 
 
    @Override 
    public void setNClob(String arg0, Reader arg1, long arg2) 
        throws SQLException { 
        cs.setNClob(arg0, arg1, arg2); 
    } 
 
    @Override 
    public void setNString(String arg0, String arg1) throws SQLException { 
        cs.setNString(arg0, arg1); 
    } 
 
    @Override 
    public void setRowId(String arg0, RowId arg1) throws SQLException { 
        cs.setRowId(arg0, arg1); 
    } 
 
    @Override 
    public void setSQLXML(String arg0, SQLXML arg1) throws SQLException { 
        cs.setSQLXML(arg0, arg1); 
    } 
} 
