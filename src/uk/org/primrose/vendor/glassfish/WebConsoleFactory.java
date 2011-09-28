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
package uk.org.primrose.vendor.glassfish; 
 
import uk.org.primrose.console.GenericWebConsoleFactory; 
 
import java.util.Hashtable; 
 
import javax.naming.Context; 
import javax.naming.Name; 
import javax.naming.Reference; 
import javax.naming.spi.ObjectFactory; 
 
 
public class WebConsoleFactory extends GenericWebConsoleFactory 
    implements ObjectFactory { 
    /** 
     * Initialize a pool as defined in a config file. The config file may contain 
     * multiple pools, but only the one specified is loaded because Glassfish does 
     * the JNDI binding itself (one datasource at a time) 
     */ 
    public Object getObjectInstance(Object refObj, Name nm, Context ctx, 
        Hashtable<?, ?> env) throws Exception { 
        Reference ref = (Reference) refObj; 
 
        if ((ref.get("port") == null) || (ref.get("logFile") == null)) { 
            throw new Exception( 
                "You must specify at least a 'port' and a 'logFile' in the <Resource> tag"); 
        } 
 
        String port = (String) ref.get("port").getContent(); 
        String logFile = (String) ref.get("logFile").getContent(); 
 
        Object tmp = ref.get("username"); 
        String username = null; 
 
        if (tmp != null) { 
            username = (String) ref.get("username").getContent(); 
        } 
 
        tmp = ref.get("password"); 
 
        String password = null; 
 
        if (tmp != null) { 
            password = (String) ref.get("password").getContent(); 
        } 
 
        tmp = ref.get("logLevel"); 
 
        String logLevel = "verbose,info,warn,error,crisis"; 
 
        if (tmp != null) { 
            logLevel = (String) ref.get("logLevel").getContent(); 
        } 
 
        this.setUsername(username); 
        this.setPassword(password); 
        this.setLogLevel(logLevel); 
        this.setPort(port); 
        this.setLogFile(logFile); // this triggers start (don't ask) 
 
        return this; 
    } 
} 
