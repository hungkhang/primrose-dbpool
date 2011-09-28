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
package uk.org.primrose.pool.core; 
 
 
/** 
*        Class allows you to provide your own config for a certain pool 
*        at startup - rather than maintaining a text file with required pool 
*        settings. 
*         For example, you may wish to store all pool settings in a central 
*        repository, accessed by LDAP. 
*        By implementing this class, you may do so. 
* 
*        For each setting used by primrose, the pool when loading, will call 
*         the getConfigItem() method - passing the pool name its loading, and the 
*        name of the item required. 
* 
*        For an example, see the uk.org.primrose.test.ExamplePoolConfigProvider class. 
* 
*/ 
import uk.org.primrose.GeneralException; 
 
 
public interface ExternalPoolConfigProvider { 
    /** 
    *        Given a pool name, and an item name, return the value 
    *        of the item. 
    */ 
    public String getConfigItem(String poolName, String itemName) 
        throws GeneralException; 
} 
