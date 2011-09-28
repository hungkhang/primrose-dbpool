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
import uk.org.primrose.pool.core.PoolLoader; 
 
import java.util.Hashtable; 
 
import javax.naming.Context; 
import javax.naming.Name; 
import javax.naming.Reference; 
import javax.naming.spi.ObjectFactory; 
 
 
/** 
 * @author sedj <code> 
 *         public static Connection getPrimroseConnection() throws Exception { 
 *                 InitialContext ctx = new InitialContext(); 
 *                 PoolLoader pl = (PoolLoader)ctx.lookup("java:comp/env/AllPrimrosePools"); 
 *                 Pool myPool = pl.findExistingPool("myPoolName"); 
 *                 if (myPool != null) { 
 *                         return myPool.getConnection(); 
 *                 } else { 
 *                         // Can't find pool ... handle error 
 *                         throw new Exception("Cannot find pool"); 
 *                 } 
 *         } 
 *         </code> 
 */ 
public class NoJndiLoaderFactory implements ObjectFactory { 
    public Object getObjectInstance(Object refObj, Name nm, Context ctx, 
        Hashtable<?, ?> env) throws Exception { 
        Reference ref = (Reference) refObj; 
 
        if (ref.get("primroseConfigFile") == null) { 
            throw new Exception( 
                "You must specify a 'primroseConfigFile' in the <Resource> tag"); 
        } 
 
        String primroseConfigFile = (String) ref.get("primroseConfigFile") 
                                                .getContent(); 
 
        if (!PoolLoader.havePoolsBeenLoaded()) { 
            try { 
                PoolLoader.loadPool(primroseConfigFile, true); 
            } catch (GeneralException ge) { 
                // GeneralException extends Throwable, not Exception ... so it needs 
                // to be wrapped. 
                throw new Exception(ge); 
            } 
        } 
 
        return PoolLoader.getInstance(); 
    } 
} 
