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
package uk.org.primrose; 
 
import java.util.*; 
 
 
public class Constants { 
    public static final String POOL_NAME = "poolName"; 
    public static final String USER = "user"; 
    public static final String PASSWORD = "password"; 
    public static final String DRIVER_URL = "driverURL"; 
    public static final String EXTERNAL_CONFIG_PROVIDER = "externalConfigProvider"; 
    public static final String ENCRYPTION_FILE_KEY = "encryptionFileKey"; 
 
    // Email Events 
    public static final String START_EVENT = "START"; 
    public static final String STOP_EVENT = "STOP"; 
    public static final String KILL_EVENT = "KILL"; 
    public static final String UNCLOSED_EVENT = "UNCLOSE"; 
    public static final String EXCEPTION_EVENT = "EXCEPTION"; 
    public static final String DBCRASH_EVENT = "DBCRASH"; 
    public static final String FAILOVER_EVENT = "FAILOVER"; 
    public static final String CUTBACK_EVENT = "CUTBACK"; 
    public static final String NO_FREE_CONNECTIONS_EVENT = "NOFREE"; 
 
    // The names of all config items 
    public static List<String> POOL_CONFIG_ITEM_NAMES = new ArrayList<String>(); 
 
    static { 
        // Note the poolName param is implicit and not added into this list (since 3.0.3) 
        // If you add new config items here, you also need to add them in PoolConfigImpl.java 
        POOL_CONFIG_ITEM_NAMES.add("base"); 
        POOL_CONFIG_ITEM_NAMES.add("log"); 
        POOL_CONFIG_ITEM_NAMES.add("idleTime"); 
        POOL_CONFIG_ITEM_NAMES.add("logLevel"); 
        POOL_CONFIG_ITEM_NAMES.add("driverClass"); 
        POOL_CONFIG_ITEM_NAMES.add("driverURL"); 
        POOL_CONFIG_ITEM_NAMES.add("user"); 
        POOL_CONFIG_ITEM_NAMES.add("password"); 
        POOL_CONFIG_ITEM_NAMES.add("killActiveConnectionsOverAge"); 
        POOL_CONFIG_ITEM_NAMES.add("cycleConnections"); 
        POOL_CONFIG_ITEM_NAMES.add("connectionAutoCommit"); 
        POOL_CONFIG_ITEM_NAMES.add("checkSQL"); 
        POOL_CONFIG_ITEM_NAMES.add("connectionTransactionIsolation"); 
        POOL_CONFIG_ITEM_NAMES.add("runPooledMode"); 
        POOL_CONFIG_ITEM_NAMES.add("encryptionFileKey"); 
        POOL_CONFIG_ITEM_NAMES.add("numberOfConnectionsToInitializeWith"); 
        POOL_CONFIG_ITEM_NAMES.add("adminEmail"); 
        POOL_CONFIG_ITEM_NAMES.add("smtpMailExchangeServer"); 
        POOL_CONFIG_ITEM_NAMES.add("smtpMailExchangeServerPort"); 
        POOL_CONFIG_ITEM_NAMES.add("emailEvents"); 
        POOL_CONFIG_ITEM_NAMES.add("dumpConnectionOnSQLException"); 
        POOL_CONFIG_ITEM_NAMES.add("failoverPool"); 
        POOL_CONFIG_ITEM_NAMES.add("onExceptionCheckSQL"); 
    } 
 
    public static final String VERSION = "3.0.14"; 
    public static final String RELEASE_DATE = "26-July-2009"; 
} 
