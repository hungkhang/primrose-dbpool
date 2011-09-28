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
package uk.org.primrose.vendor.standalone; 
 
import uk.org.primrose.GeneralException; 
import uk.org.primrose.pool.PoolException; 
import uk.org.primrose.pool.core.PoolLoader; 
import uk.org.primrose.pool.datasource.PrimroseDataSource; 
 
import java.io.IOException; 
 
import java.util.List; 
import java.util.Properties; 
 
import javax.naming.Context; 
import javax.naming.InitialContext; 
import javax.naming.NamingException; 
 
 
public class PrimroseLoader { 
    public static void main(String[] args) throws Exception, Throwable { 
        if (args.length != 1) { 
            System.out.println( 
                "java uk.org.primrose.vendor.standalone.PrimroseLoader <config file>"); 
            System.exit(0); 
        } 
 
        load(args[0], true); 
    } 
 
    /** 
     * Stop the pool given by the name 
     * 
     * @throws PoolException 
     */ 
    public static void stopPool(String name) throws PoolException { 
        PoolLoader.stopPool(name); 
    } 
 
    /** 
     * Stop the ALL pools that are loaded in the JVM. 
     * 
     * @throws PoolException 
     */ 
    public static void stopAllPools() throws PoolException { 
        PoolLoader.stopAllPools(); 
    } 
 
    /** 
     * Load a pool(s), or admin data from a config file. If admin data, then 
     * createNewPools is ignored. If createNewPools is true, then an existing pool 
     * is located, altered accordingly and restarted. If createNewPools is false, 
     * then existing pools for the poolName(s) passed are stopped, and replaced. 
     */ 
    public static List<String> load(String configFile, boolean createNewPools) 
        throws GeneralException, IOException { 
        setJNDIEnv(); 
        List<String> loadedPoolNames = PoolLoader.loadPool(configFile, 
                createNewPools); 
 
        for (String poolName : loadedPoolNames) { 
            bindPoolToJNDI(poolName); 
        } 
 
        return loadedPoolNames; 
    } 
 
    /** 
     * Load a pool, or admin data from a Properties object. One pool, or admin 
     * data per load operation. If admin data, then createNewPools is ignored. If 
     * createNewPools is true, then an existing pool is located, altered 
     * accordingly and restarted. If createNewPools is false, then existing pools 
     * for the poolName(s) passed are stopped, and replaced. 
     */ 
    public static void load(Properties config, boolean createNewPools) 
        throws GeneralException { 
        setJNDIEnv(); 
 
        String poolName = PoolLoader.loadPool(config, createNewPools); 
        bindPoolToJNDI(poolName); 
    } 
 
    private static void bindPoolToJNDI(String poolName) 
        throws GeneralException { 
        try { 
            Context initCtx = new InitialContext(); 
            Context ctx = (Context) initCtx.lookup("java:comp"); 
            Context envCtx = null; 
 
            try { 
                envCtx = (Context) ctx.lookup("env"); 
            } catch (NamingException e) { 
                envCtx = ctx.createSubcontext("env"); 
            } 
 
            PrimroseDataSource pds = new PrimroseDataSource(); 
            pds.setPoolName(poolName); 
            envCtx.rebind(poolName, pds); 
        } catch (Exception e) { 
            throw new GeneralException( 
                "Error creating JNDI subcontext 'env', while loading pool '" + 
                poolName + "'", e); 
        } 
    } 
 
    private static void setJNDIEnv() { 
        System.setProperty("java.naming.factory.url.pkgs", 
            "uk.org.primrose.jndi"); 
        System.setProperty("java.naming.factory.initial", 
            "uk.org.primrose.jndi.PrimroseInitialContextFactory"); 
    } 
} 
