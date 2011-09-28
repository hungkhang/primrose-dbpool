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
package uk.org.primrose.vendor.tomcat; 
 
import uk.org.primrose.GeneralException; 
import uk.org.primrose.pool.datasource.GenericDataSourceFactory; 
import uk.org.primrose.pool.datasource.PrimroseDataSource; 
 
import java.util.Enumeration; 
import java.util.Hashtable; 
import java.util.Properties; 
 
import javax.naming.Context; 
import javax.naming.Name; 
import javax.naming.Reference; 
import javax.naming.StringRefAddr; 
import javax.naming.spi.ObjectFactory; 
 
 
public class PrimroseDataSourceFactory extends GenericDataSourceFactory 
    implements ObjectFactory { 
    /** 
     * Initialize a pool as defined in a config file. The config file may contain 
     * multiple pools, but only the one specified is loaded because Tomcat does 
     * the JNDI binding itself (one datasource at a time) 
     */ 
    public Object getObjectInstance(Object refObj, Name nm, Context ctx, 
        Hashtable<?, ?> env) throws Exception { 
        PrimroseDataSource pds = null; 
 
        Reference ref = (Reference) refObj; 
 
        if (ref.get("poolName") == null) { 
            throw new Exception( 
                "You must specify a 'poolName' in the <Resource> tag"); 
        } 
 
        String poolName = (String) ref.get("poolName").getContent(); 
 
        Properties poolProps = null; 
 
        try { 
            // Load from properties defined in the Resource tag 
            // if they have not specified a config file 
            if (ref.get("primroseConfigFile") == null) { 
                poolProps = new Properties(); 
 
                for (Enumeration<?> e = ref.getAll(); e.hasMoreElements();) { 
                    StringRefAddr refAddr = (StringRefAddr) e.nextElement(); 
                    String name = refAddr.getType(); 
                    String value = (String) refAddr.getContent(); 
 
                    poolProps.setProperty(name, value); 
                } 
 
                pds = loadPool(poolName, poolProps); 
 
                // Load pool from a file 
            } else { 
                String primroseConfigFile = (String) ref.get( 
                        "primroseConfigFile").getContent(); 
                pds = loadPool(poolName, primroseConfigFile); 
            } 
        } catch (GeneralException ge) { 
            throw new Exception("Error loading pool '" + poolName + "' : " + 
                ge); 
        } 
 
        if (pds == null) { 
            throw new Exception( 
                "Could not generate PrimroseDataSource object due to previous errors - check the settings in your config"); 
        } 
 
        return pds; 
    } 
} 
