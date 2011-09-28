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
package uk.org.primrose.pool.core.loadrules; 
 
import uk.org.primrose.Constants; 
import uk.org.primrose.Logger; 
import uk.org.primrose.pool.core.Pool; 
 
import java.net.*; 
 
 
/** 
 * 
 * @author sedj 
 *        If the 'emailEvents' parameter is set, the 'smtpMailExchangeServer' parameter 
 *        must also be set, aswell as the 'adminEmail'. 
 *        Also checks if can connect to the server on port 25 (SMPT port). 
 */ 
public class EmailEvents implements LoadRule { 
    public void runCheck(Pool pool, Logger logger) throws LoadRuleException { 
        logger.info("[Pool@" + pool.getPoolName() + 
            "] Checking 'emailEvents' rule  ..."); 
 
        if ((pool.getSmtpMailExchangeServer() == null) || 
                (pool.getSmtpMailExchangeServer().length() == 0)) { 
            throw new LoadRuleException( 
                "Pool config parameter 'smtpMailExchangeServer' must be specified if you wish to use the 'emailEvents' parameter."); 
        } 
 
        if ((pool.getAdminEmail() == null) || 
                (pool.getAdminEmail().length() == 0)) { 
            throw new LoadRuleException( 
                "Pool config parameter 'adminEmail' must be specified if you wish to use the 'emailEvents' parameter."); 
        } 
 
        try { 
            Integer.parseInt(pool.getSmtpMailExchangeServerPort()); 
        } catch (Exception e) { 
            throw new LoadRuleException("'smtpMailExchangeServerPort' parameter is set, the value '" +pool.getSmtpMailExchangeServerPort() +"' is not an integer", 
                e); 
        } 
 
        try { 
            Socket s = new Socket(pool.getSmtpMailExchangeServer(), Integer.parseInt(pool.getSmtpMailExchangeServerPort())); 
            s.close(); 
        } catch (Exception e) { 
            throw new LoadRuleException("'smtpMailExchangeServer' parameter is set, but the pool cannot connect to the server on port " +pool.getSmtpMailExchangeServerPort(), 
                e); 
        } 
 
        String events = pool.getEmailEvents(); 
        String[] eventParts = events.split(","); 
 
        for (String event : eventParts) { 
            if (event.toUpperCase().equals(Constants.DBCRASH_EVENT)) { 
                if ((pool.getOnExceptionCheckSQL() == null) || 
                        (pool.getOnExceptionCheckSQL().length() == 0)) { 
                    throw new LoadRuleException( 
                        "If you wish to be emailed about the DBCRASH event, then you must set the 'onExceptionCheckSQL', eg 'onExceptionCheckSQL=select 1 from dual'"); 
                } 
 
                break; 
            } 
        } 
    } 
} 
