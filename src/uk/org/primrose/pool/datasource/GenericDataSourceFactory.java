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
package uk.org.primrose.pool.datasource; 
 
import uk.org.primrose.GeneralException; 
import uk.org.primrose.Util; 
import uk.org.primrose.pool.core.*; 
 
import java.io.*; 
 
import java.util.*; 
 
import javax.naming.Context; 
import javax.naming.InitialContext; 
import javax.naming.NoInitialContextException; 
 
 
public class GenericDataSourceFactory { 
    /** 
    *        Generate a PrimroseDataSource object - which provides access to pooled connections, 
    *        from config file containing one or more defined pools 
    */ 
    public PrimroseDataSource loadPool(String poolName, String configFile) 
        throws GeneralException, IOException { 
        if ((poolName == null) || (configFile == null)) { 
            throw new GeneralException( 
                "You must specify a non-null 'poolName' and a 'configFile' as parameters"); 
        } 
 
        Properties poolProps = Util.generatePropertiesForPoolName(configFile, 
                poolName); 
 
        return loadPool(poolName, poolProps); 
    } 
 
    /** 
    *        Generate a PrimroseDataSource object - which provides access to pooled connections, 
    *        from a Properties object containing pool config data 
    */ 
    public PrimroseDataSource loadPool(String poolName, Properties poolProps) 
        throws GeneralException, IOException { 
        if ((poolName == null) || (poolProps == null)) { 
            throw new GeneralException( 
                "You must specify a non-null 'poolName' pool Properties object as parameters"); 
        } 
 
        if (poolProps != null) { 
            PoolLoader.loadPool(poolProps, true /* create new */); 
        } 
 
        PrimroseDataSource pds = new PrimroseDataSource(); 
        pds.setPoolName(poolName); 
 
        return pds; 
    } 
 
    public Context findOrBindJNDIEnvContext() throws GeneralException { 
        Context envCtx = null; 
        Context initCtx = null; 
 
        try { 
            initCtx = new InitialContext(); 
 
            try { 
                // Try to find the env subcontext 
                envCtx = (Context) initCtx.lookup("java:comp/env"); 
            } catch (NoInitialContextException e) { 
                // If they've loaded Jetty 5 with no JNDI support, use our own JNDI context 
                // and create the relevant sub contexts 
                System.setProperty("java.naming.factory.url.pkgs", 
                    "uk.org.primrose.jndi"); 
                System.setProperty("java.naming.factory.initial", 
                    "uk.org.primrose.jndi.PrimroseInitialContextFactory"); 
 
                initCtx = new InitialContext(); 
 
                Context ctx = (Context) initCtx.lookup("java:comp"); 
 
                try { 
                    envCtx = (Context) ctx.lookup("env"); 
                } catch (Exception e2) { 
                    // no env context - forget it and create it below in a while 
                } 
            } catch (Exception e) { 
                // We have a JNDI context (primrose's or jetty's) 
                // but no env subcontext - forget it and create it below in a while 
            } 
        } catch (Exception e) { 
            throw new GeneralException("Error looking up JNDI context java:comp or java:comp/env", 
                e); 
        } 
 
        if (envCtx == null) { 
            try { 
                Context ctx = (Context) initCtx.lookup("java:comp"); 
                envCtx = ctx.createSubcontext("env"); 
            } catch (Exception e2) { 
                throw new GeneralException("Error creating JNDI subcontext 'env' under java:comp", 
                    e2); 
            } 
        } 
 
        return envCtx; 
    } 
 
    /** 
     * Load a set of pools from a config file, and bind them to a JNDI context under java:comp/env 
     * If there is no JNDI context, then Primrose's own JNDI implementation is used. 
     * 
     * @param primroseConfigFile 
     * @throws GeneralException 
     */ 
    public void loadPools(String primroseConfigFile) throws GeneralException { 
        String currentPoolName = null; 
 
        try { 
            Context envCtx = findOrBindJNDIEnvContext(); 
            List<String> loadedPoolNames = PoolLoader.loadPool(primroseConfigFile, 
                    true); 
 
            for (String poolName : loadedPoolNames) { 
                currentPoolName = poolName; 
 
                PrimroseDataSource pds = new PrimroseDataSource(); 
                pds.setPoolName(poolName); 
                envCtx.rebind(poolName, pds); 
            } 
        } catch (Exception e) { 
            throw new GeneralException("Error loading pool '" + 
                currentPoolName + "'", e); 
        } 
    } 
} 
