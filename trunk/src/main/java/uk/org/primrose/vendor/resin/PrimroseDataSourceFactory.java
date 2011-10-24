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
package uk.org.primrose.vendor.resin; 
 
import uk.org.primrose.GeneralException; 
import uk.org.primrose.pool.datasource.*; 
 
import java.io.IOException; 
 
 
public class PrimroseDataSourceFactory extends GenericDataSourceFactory { 
    private String primroseConfigFile = null; 
 
    // The 'poolName' pool config property 
    private String poolName; 
 
    /* 
     * Once the file name is set for each datasource, the pool is loaded. 
     */ 
    public void setPrimroseConfigFile(String primroseConfigFile) 
        throws GeneralException, IOException { 
        if (this.primroseConfigFile == null) { 
            this.primroseConfigFile = primroseConfigFile; 
            loadPool(poolName, primroseConfigFile); 
        } 
    } 
 
    /** 
    *        Get the 'poolName' pool config property 
    */ 
    public String getPoolName() { 
        return poolName; 
    } 
 
    /** 
    *        Set the 'poolName' pool config property 
    */ 
    public void setPoolName(String poolName) { 
        this.poolName = poolName; 
    } 
} 
