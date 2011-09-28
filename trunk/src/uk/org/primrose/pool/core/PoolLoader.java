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
 
import uk.org.primrose.ConfigEncrypter; 
import uk.org.primrose.Constants; 
import uk.org.primrose.DebugLogger; 
import uk.org.primrose.GeneralException; 
import uk.org.primrose.Util; 
import uk.org.primrose.pool.PoolException; 
 
import java.io.BufferedReader; 
import java.io.FileInputStream; 
import java.io.IOException; 
import java.io.InputStreamReader; 
 
import java.util.ArrayList; 
import java.util.Enumeration; 
import java.util.List; 
import java.util.Properties; 
 
 
public class PoolLoader { 
    // A list of the current loaded pools 
    private static List<Pool> loadedPools = new ArrayList<Pool>(); 
    private static PoolLoader singleton = new PoolLoader(); 
 
    // Indicator to see if we've been loaded before 
    private static boolean alreadyStarted = false; 
 
    static { 
        // Add a JVM exit shutdown hook 
        if (!alreadyStarted) { 
            alreadyStarted = true; 
            Runtime.getRuntime().addShutdownHook(new PoolStopper()); 
        } 
    } 
 
    /** 
     * Load multiple pools from a primrose config file 
     */ 
    public static List<String> loadPool(String configFile, 
        boolean createNewPools) throws GeneralException, IOException { 
        // The properties of our pool 
        Properties p = null; 
 
        // Read the config file, and populate the pool Properties object 
        BufferedReader br = new BufferedReader(new InputStreamReader( 
                    new FileInputStream(configFile))); 
        String line; 
 
        // A list of pools that have been loaded 
        List<String> loadedPoolNames = new ArrayList<String>(); 
        String lastKey = null; 
 
        while ((line = br.readLine()) != null) { 
            line = line.trim(); 
 
            // Ignore comments and whitespace 
            if (line.startsWith("#") || (line.length() == 0)) { 
                continue; 
            } 
 
            KeyValuePair keyValuePair = new KeyValuePair(line); 
            keyValuePair.splitLine(); 
            String key = keyValuePair.getKey(); 
            String value = keyValuePair.getValue(); 
 
            // handle multi line driverURL's 
            if (!Util.isConfigParameterValid(key) && (lastKey != null) && 
                    lastKey.equalsIgnoreCase(Constants.DRIVER_URL)) { 
                if (p != null) { 
                    p.setProperty(lastKey, p.getProperty(lastKey) + line); 
                } 
 
                continue; // continue on so we don't reset the true lastKey (ie lines of 
                          // multi-line driver are more than 1 ...) 
            } 
 
            // Got a new poolName entry - means the beginning of 
            // a new pool 
            // If we have a pool ready to load (p != null) 
            // then start it up 
            if (key.equals(Constants.POOL_NAME)) { 
                if (p != null) { 
                    // Start the pool 
                    String poolName = loadPool(p, createNewPools); 
 
                    // Add to our list the loaded pools 
                    loadedPoolNames.add(poolName); 
                } 
 
                p = new Properties(); 
            } 
 
            if (p != null) { 
                if (key.equals("=")) { 
                    key = ""; 
                } 
 
                if (value.equals("=")) { 
                    value = ""; 
                } 
 
                p.setProperty(key, value); 
            } 
 
            lastKey = key; 
        } 
 
        // Load the last pool in the list 
        if (p != null) { 
            String poolName = loadPool(p, createNewPools); 
            loadedPoolNames.add(poolName); 
        } 
 
        br.close(); 
 
        return loadedPoolNames; 
    } 
 
    /** 
     * If the property Constants.ENCRYPTION_KEY_FILE is present (ie passwords are 
     * encypted) then decrpyt them and reset the entry in the Properties object 
     */ 
    public static void decryptPassword(Properties p) throws GeneralException { 
        Object encKeyFileObj = p.get(Constants.ENCRYPTION_FILE_KEY); 
 
        if (encKeyFileObj != null) { 
            String encKeyFile = (String) encKeyFileObj; 
            String encPassword = (String) p.get(Constants.PASSWORD); 
            String password = null; 
            DebugLogger.log("About to decrypt password (" + encPassword + ")"); 
            DebugLogger.log("Using keyfile (" + encKeyFile + ")"); 
 
            try { 
                password = ConfigEncrypter.getDecryptedString(encPassword, 
                        encKeyFile); 
                DebugLogger.log("Decrypt OK : (" + password + ")"); 
            } catch (Exception e) { 
                DebugLogger.log("Got error decrpyting : " + e); 
                throw new GeneralException( 
                    "Error decrypting password String for pool : " + 
                    p.getProperty(Constants.POOL_NAME), e.getCause()); 
            } 
 
            p.setProperty(Constants.PASSWORD, password); 
        } else { 
            DebugLogger.log( 
                "envKeyFileObj is null ... not decrpyting passwords"); 
        } 
    } 
 
    /** 
     * Get a list of loaded pools 
     */ 
    public static List<Pool> getLoadedPools() { 
        return loadedPools; 
    } 
 
    /** 
     * Stop all pools 
     */ 
    public static void stopAllPools() throws PoolException { 
        for (Pool p : loadedPools) { 
            p.stop(true); 
        } 
    } 
 
    /** 
     * Stop a single pool 
     */ 
    public static void stopPool(String name) throws PoolException { 
        Pool p = findExistingPool(name); 
 
        if (p != null) { 
            p.stop(true); 
        } 
    } 
 
    /** 
     * Load a single new Pool, or alter settings in an existing Pool using a 
     * Properties object, which should contain all the details required to 
     * configure primrose. If createNewPool is true, then a brand new pool is 
     * made. Any pool running under an exsting name is shut down. if false then an 
     * existing pool is located, and its properties updated, and then restarted 
     * without killing existing connection SQL jobs running from the pool 
     */ 
    public static String loadPool(Properties config, boolean createNewPool) 
        throws GeneralException { 
        Pool pool = null; 
        String poolName = config.getProperty(Constants.POOL_NAME); 
 
        if (poolName == null) { 
            throw new GeneralException( 
                "Cannot load pool without a poolName property"); 
        } 
 
        pool = findExistingPool(poolName.trim()); 
 
        // If we are creating a new Pool, see if we have an existing pool under that 
        // name, and stop it. 
        // Then instantiate a new Pool object 
        // or if we are altering an existing pool, then find it, update the settings 
        // and restart it 
        if (createNewPool) { 
            if (pool != null) { 
                pool.stop(false); 
                loadedPools.remove(pool); 
            } 
 
            pool = new Pool(); 
        } else { 
            if (pool == null) { 
                throw new GeneralException("Cannot locate pool under name '" + 
                    poolName + "'"); 
            } 
        } 
 
        // Set the pool properties 
        // Are we loading our pool using a pool config provider class 
        // or are we loading using individual settings (ie from a primrose.config 
        // text file) 
        String configClassName = (String) config.get(Constants.EXTERNAL_CONFIG_PROVIDER); 
 
        // Check for encrypted passwords 
        DebugLogger.log( 
            "Seeing if we need to decrpyt the passwords"); 
        decryptPassword(config);         
         
        // 
        // Load pool settings using a user provided class 
        // 
        if (configClassName != null) { 
            // Instantiate their class that provides the config settings 
            Object o = null; 
 
            try { 
                o = Class.forName(configClassName).newInstance(); 
            } catch (Exception e) { 
                throw new GeneralException("Error loading " + 
                    Constants.EXTERNAL_CONFIG_PROVIDER + " class", e.getCause()); 
            } 
 
            ExternalPoolConfigProvider configProvider = (ExternalPoolConfigProvider) o; 
 
            // set our pool name 
            pool.setPoolName(poolName); 
 
            // For all available config items, get the values from their class 
            for (String itemName : Constants.POOL_CONFIG_ITEM_NAMES) { 
                String value = configProvider.getConfigItem(poolName, itemName); 
 
                // Ignore nulls ... use default 
                if ((value != null) && !value.equals("null") && 
                        (value.length() > 0)) { 
                    String camelItemName = (itemName.charAt(0) + "").toUpperCase() + 
                        itemName.substring(1, itemName.length()); 
 
                    // Throws GeneralException if errors 
                    Util.callClassMethod(pool.getClass(), pool, 
                        "set" + camelItemName, new Object[] { value }); 
                } 
            } 
 
            //	 
            // Load in the traditional manner (config text file) 
            // 
        } else { 
            for (Enumeration<?> e = config.propertyNames(); 
                    e.hasMoreElements();) { 
                String name = ((String) e.nextElement()).trim(); 
                String value = config.getProperty(name); 
 
                // If value is null - do not attempt to set the property 
                if (value == null) { 
                    continue; 
                } 
 
                value = value.trim(); 
 
                String camelName = (name.charAt(0) + "").toUpperCase() + 
                    name.substring(1, name.length()); 
 
                // Throws GeneralException if errors 
                Util.callClassMethod(pool.getClass(), pool, "set" + camelName, 
                    new Object[] { value }); 
            } 
        } 
 
        // Start the pool 
        if (createNewPool) { 
            pool.start(); 
            loadedPools.add(pool); 
        } else { 
            pool.restart(false); 
        } 
 
        return poolName; 
    } 
 
    /** 
     * Get a reference to an existing pool 
     */ 
    public static Pool findExistingPool(String poolName) { 
        for (Pool pool : loadedPools) { 
            if (pool.getPoolName().equals(poolName)) { 
                return pool; 
            } 
        } 
 
        return null; 
    } 
 
    /** 
     * Have any pools been loaded ? 
     */ 
    public static boolean havePoolsBeenLoaded() { 
        return loadedPools.size() > 0; 
    } 
 
    public static PoolLoader getInstance() { 
        return singleton; 
    } 
 
    // JVM shutdown hook 
    static class PoolStopper extends Thread { 
        @Override 
        public void run() { 
            try { 
                PoolLoader.stopAllPools(); 
            } catch (Throwable t) { 
                t.printStackTrace(); 
            } 
        } 
    } 
 
} 
