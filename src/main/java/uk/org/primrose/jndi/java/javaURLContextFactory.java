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
package uk.org.primrose.jndi.java; 
 
import uk.org.primrose.jndi.PrimroseInitialContextFactory; 
 
import java.util.Hashtable; 
 
import javax.naming.Context; 
import javax.naming.Name; 
import javax.naming.NamingException; 
import javax.naming.spi.InitialContextFactory; 
import javax.naming.spi.ObjectFactory; 
 
 
/** 
 * Context factory for the "java:" namespace.<br> 
 * jndi.properties must have java.naming.factory.url.pkgs=uk.org.rosehip.core<br> 
 * OR -Djava.naming.factory.url.pkgs=uk.org.rosehip.core on command line.<br> 
 * Implementing this allows us to bind contexts such as 
 * "java:comp/env/poolName"<br> 
 * 
 * @see javax.naming.spi.NamingManager#getURLContext(java.lang.String, 
 *      java.util.Hashtable).<br> 
 */ 
public class javaURLContextFactory implements ObjectFactory, 
    InitialContextFactory { 
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, 
        Hashtable<?, ?> environment) throws NamingException { 
        return new PrimroseInitialContextFactory().getInitialContext(environment); 
    } 
 
    public Context getInitialContext(Hashtable<?, ?> environment) 
        throws NamingException { 
        return new PrimroseInitialContextFactory().getInitialContext(environment); 
    } 
} 
