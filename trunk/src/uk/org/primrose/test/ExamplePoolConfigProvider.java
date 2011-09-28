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
package uk.org.primrose.test; 
 
import uk.org.primrose.GeneralException; 
import uk.org.primrose.pool.core.ExternalPoolConfigProvider; 
 
import java.util.Properties; 
 
 
/** 
*        This class is a very simple example of how to provide your own 
*        settings to a pool at runtime - possibly using an external datastore 
*        via LDAP, or from a relational db. 
* 
*         The example 'datasource' here is just a Properties object - but it does highlight 
*         how you may implement your own. 
* 
*         If you wish to accept defaults for values, then leave them out of the config 
*         and a default value will be used by the pool. For example, if you do not specify 
*         'idleTime', then a value of 120000 (2 minutes) will be used. 
* 
*         See the ExternalPoolConfigProvider class for more info ... 
* 
*/ 
public class ExamplePoolConfigProvider implements ExternalPoolConfigProvider { 
    Properties poolOneProps = new Properties(); 
    Properties poolTwoProps = new Properties(); 
 
    public ExamplePoolConfigProvider() { 
        // Set up our pretend datasources for two pools ... 
        poolOneProps.put("base", "10"); 
        poolOneProps.put("log", "C:/java/primrose_3.0/poolone_${yyyy-MM-dd}.log"); 
        poolOneProps.put("logLevel", "verbose,info,warn,error,crisis"); 
        poolOneProps.put("driverClass", "oracle.jdbc.driver.OracleDriver"); 
        poolOneProps.put("driverURL", 
            "jdbc:oracle:thin:@192.168.154.3:1521:EXHCDEV1"); 
        poolOneProps.put("user", "hcr"); 
        poolOneProps.put("password", "dev"); 
        poolOneProps.put("killActiveConnectionsOverAge", "120000"); 
        poolOneProps.put("cycleConnections", "1000"); 
        poolOneProps.put("connectionAutoCommit", "true"); 
        poolOneProps.put("checkSQL", "select 1 from dual"); 
        poolOneProps.put("connectionTransactionIsolation", 
            "TRANSACTION_READ_COMMITTED"); 
 
        poolTwoProps.put("base", "5"); 
        poolTwoProps.put("log", "C:/java/primrose_3.0/pooltwo_${yyyy-MM-dd}.log"); 
        poolTwoProps.put("idleTime", "5000"); 
        poolTwoProps.put("logLevel", "info,warn,error,crisis"); 
        poolTwoProps.put("driverClass", "oracle.jdbc.driver.OracleDriver"); 
        poolTwoProps.put("driverURL", 
            "jdbc:oracle:thin:@192.168.154.3:1521:EXHCDEV1"); 
        poolTwoProps.put("user", "oraquery"); 
        poolTwoProps.put("password", "dev"); 
        poolTwoProps.put("killActiveConnectionsOverAge", "120000"); 
    } 
 
    public String getConfigItem(String poolName, String itemName) 
        throws GeneralException { 
        // Depending on which pool is being loaded, return our appropriate values 
        if (poolName.equals("poolOne")) { 
            return poolOneProps.getProperty(itemName); 
        } else if (poolName.equals("poolTwo")) { 
            return poolTwoProps.getProperty(itemName); 
        } else { 
            throw new GeneralException("Unknown pool name :'" + poolName + "'"); 
        } 
    } 
} 
