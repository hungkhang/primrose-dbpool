package uk.org.primrose.vendor.jboss; 
 
import uk.org.primrose.GeneralException; 
import uk.org.primrose.pool.core.Pool; 
import uk.org.primrose.pool.core.PoolLoader; 
import uk.org.primrose.pool.datasource.*; 
 
import java.io.Serializable; 
 
import java.util.List; 
 
import javax.naming.*; 
 
 
public class PrimroseBinding implements PrimroseBindingMBean, Serializable { 
    private static final long serialVersionUID = -7189423090231527848L; 
    private String primroseConfigFile; 
 
    public void start() throws Exception { 
        try { 
            List<String> loadedPoolNames = PoolLoader.loadPool(primroseConfigFile, 
                    true); 
 
            for (String poolName : loadedPoolNames) { 
                PrimroseDataSource pds = new PrimroseDataSource(); 
                pds.setPoolName(poolName); 
 
                Context initCtx = new InitialContext(); 
                initCtx.rebind(poolName, pds); 
            } 
        } catch (GeneralException ge) { 
            ge.printStackTrace(); 
            throw new Exception("Cannot start primrose : " + ge); 
        } 
    } 
 
    public void stop() throws Exception { 
        try { 
            List<Pool> pools = PoolLoader.getLoadedPools(); 
 
            for (Pool pool : pools) { 
                pool.stop(false); 
            } 
        } catch (GeneralException ge) { 
            ge.printStackTrace(); 
            throw new Exception("Cannot start primrose : " + ge); 
        } 
    } 
 
    public void setPrimroseConfigFile(String primroseConfigFile) { 
        this.primroseConfigFile = primroseConfigFile; 
    } 
 
    public String getPrimroseConfigFile() { 
        return this.primroseConfigFile; 
    } 
} 
