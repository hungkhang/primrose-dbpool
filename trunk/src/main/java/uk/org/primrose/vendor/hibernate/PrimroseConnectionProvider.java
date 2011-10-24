package uk.org.primrose.vendor.hibernate; 
 
import org.hibernate.HibernateException; 
 
import org.hibernate.connection.ConnectionProvider; 
 
import org.hibernate.util.JDBCExceptionReporter; 
 
import uk.org.primrose.GeneralException; 
import uk.org.primrose.pool.PoolException; 
import uk.org.primrose.pool.core.ExternalPoolConfigProvider; 
import uk.org.primrose.pool.core.Pool; 
import uk.org.primrose.pool.core.PoolLoader; 
import uk.org.primrose.vendor.standalone.PrimroseLoader; 
 
import java.sql.Connection; 
import java.sql.SQLException; 
 
import java.util.Enumeration; 
import java.util.HashMap; 
import java.util.Map; 
import java.util.Properties; 
 
 
/** 
 * <p> 
 * Connection provider to provide the bridge between Primrose and Hibernate<br> 
 * Requires the following entry in the Hibernate properties:<br> 
 * 
 * <pre> 
 * hibernate.connection.provider_class = uk.org.primrose.demo.PrimroseConnectionProvider 
 * </pre> 
 * 
 * Further configuration is achieved by prefixing the name of the connection 
 * pool 
 * </p> 
 */ 
public class PrimroseConnectionProvider implements ConnectionProvider, 
    ExternalPoolConfigProvider { 
    /* Singleton instance of the pool configuration provider */ 
    private static Map<String, Properties> poolConfigurations = null; 
    private String poolName; 
 
    public PrimroseConnectionProvider() { 
        if (poolConfigurations == null) { 
            poolConfigurations = new HashMap<String, Properties>(); 
        } 
    } 
 
    /* 
     * (non-Javadoc) 
     * 
     * @see org.hibernate.connection.ConnectionProvider#configure(java.util.Properties) 
     */ 
    public void configure(Properties props) throws HibernateException { 
        // Extract the pool name from the Hibernate properties 
        poolName = (String) props.get("hibernate.primrose.poolName"); 
 
        if (poolName == null) { 
            throw new HibernateException( 
                "Unknown pool name. Please ensure property hibernate.primrose.poolName is set in your configuration."); 
        } 
 
        // Create or update a pool configuration 
        Properties poolConfiguration = null; 
 
        if (poolConfigurations.containsKey(poolName)) { 
            poolConfiguration = poolConfigurations.get(poolName); 
        } else { 
            poolConfiguration = new Properties(); 
 
            for (Enumeration<?> e = props.keys(); e.hasMoreElements();) { 
                String key = (String) e.nextElement(); 
 
                // Only interested in Primrose configuration details 
                if (key.startsWith("hibernate.primrose.")) { 
                    String primroseKey = key.replace("hibernate.primrose.", ""); 
                    poolConfiguration.put(primroseKey, props.get(key)); 
                } 
            } 
        } 
 
        try { 
            // Store the pool configuration 
            poolConfigurations.put(poolName, poolConfiguration); 
            // Load up the pool ready for connections 
            PrimroseLoader.load(poolConfiguration, true); 
        } catch (Throwable e) { 
            throw new HibernateException(e); 
        } 
    } 
 
    /* 
     * (non-Javadoc) 
     * 
     * @see org.hibernate.connection.ConnectionProvider#getConnection() 
     */ 
    public Connection getConnection() throws SQLException { 
        Pool pool = PoolLoader.findExistingPool(poolName); 
 
        if (pool == null) { 
            throw new SQLException((new StringBuilder()).append( 
                    "Cannot find primrose pool under name '").append(poolName) 
                                    .append("'").toString()); 
        } 
 
        try { 
            return pool.getConnection(); 
        } catch (PoolException e) { 
            pool.getLogger().printStackTrace(e); 
            throw new SQLException((new StringBuilder()).append( 
                    "Cannot obtain connection using pool name '") 
                                    .append(poolName).append("'").toString()); 
        } 
    } 
 
    /* 
     * (non-Javadoc) 
     * 
     * @see org.hibernate.connection.ConnectionProvider#closeConnection(java.sql.Connection) 
     */ 
    public void closeConnection(Connection con) throws SQLException { 
        try { 
            con.close(); 
        } catch (SQLException ex) { 
            JDBCExceptionReporter.logExceptions(ex); 
            throw ex; 
        } 
    } 
 
    /* 
     * (non-Javadoc) 
     * 
     * @see org.hibernate.connection.ConnectionProvider#close() 
     */ 
    public void close() { 
    } 
 
    /* 
     * (non-Javadoc) 
     * 
     * @see org.hibernate.connection.ConnectionProvider#supportsAggressiveRelease() 
     */ 
    public boolean supportsAggressiveRelease() { 
        return false; 
    } 
 
    /* 
     * (non-Javadoc) 
     * 
     * @see uk.org.primrose.pool.core.ExternalPoolConfigProvider#getConfigItem(java.lang.String, 
     *      java.lang.String) 
     */ 
    public String getConfigItem(String poolName, String itemName) 
        throws GeneralException { 
        if (poolConfigurations.containsKey(poolName)) { 
            Properties poolConfiguration = poolConfigurations.get(poolName); 
 
            if (poolConfiguration != null) { 
                return poolConfiguration.getProperty(itemName); 
            } 
        } 
 
        return null; 
    } 
} 
