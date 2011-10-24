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
 
import uk.org.primrose.Constants; 
import uk.org.primrose.Logger; 
 
import java.sql.SQLException; 
 
import java.util.ArrayList; 
import java.util.List; 
 
 
public class PoolMonitor extends Thread { 
    Pool pool; 
    boolean run = true; 
    int sleepTime = 10000; 
    Logger logger = null; 
 
    public PoolMonitor(Pool pool, Logger logger) { 
        this.pool = pool; 
        this.logger = logger; 
    } 
 
    public void shutdown() { 
        run = false; 
    } 
 
    public void run() { 
        while (run) { 
            try { 
                long now = System.currentTimeMillis(); 
 
                for (ConnectionHolder ch : pool.connections) { 
                    if (ch.status == Pool.CONNECTION_INACTIVE) { 
                        if (pool.iIdleTime != -1) { 
                            long idle = now - ch.lastUsedTimestamp; 
 
                            if ((ch.lastUsedTimestamp != 0L) && 
                                    (idle > pool.iIdleTime)) { 
                                logger.info("[PoolMonitor@" + 
                                        pool.poolName + "] PoolConnection@" + 
                                        ch.conn.hashCode() + 
                                        " has exceeded max idle time (" + 
                                        pool.iIdleTime + 
                                        " ms) with idle time of (" + idle + 
                                " ms) - dropping connection"); 
 
                                try { 
                                    ch.conn.closePhysicalAsynch(); 
                                } catch (SQLException sqle) { 
                                    logger.printStackTrace(sqle); 
                                } 
                            } 
                        } 
                    } else if (ch.status == Pool.CONNECTION_ACTIVE) { 
                        if (pool.iKillActiveConnectionsOverAge != -1) { 
                            long active = now - ch.connOpenedDate; 
 
                            if ((ch.connOpenedDate != 0L) && 
                                    (active > pool.iKillActiveConnectionsOverAge)) { 
                                StringBuffer stack = new StringBuffer(); 
                                StackTraceElement[] els = ch.callStack; 
 
                                for (StackTraceElement el : els) { 
                                    stack.append("\t"); 
                                    stack.append(el.toString()); 
                                    stack.append("\n"); 
                                } 
 
                                String message = ("[PoolMonitor@" + 
                                        pool.poolName + "] PoolConnection@" + 
                                        ch.conn.hashCode() + 
                                        " has exceeded max execution time (" + 
                                        pool.iKillActiveConnectionsOverAge + 
                                        " ms) with active time of (" + active + 
                                        " ms) - killing off connection\n" + 
                                        stack.toString()); 
                                logger.email(Constants.KILL_EVENT, message); 
                                logger.info(message); 
 
                                try { 
                                    ch.conn.closePhysicalAsynch(); 
                                } catch (SQLException sqle) { 
                                    logger.printStackTrace(sqle); 
                                } 
                            } 
                        } 
                    } 
                } 
            } catch (Exception e) { 
                logger.printStackTrace(e); 
            } 
 
            if (run) { 
                try { 
                    Thread.sleep(sleepTime); 
                } catch (InterruptedException ie) { 
                } 
            } 
        } 
 
        logger.verbose("[PoolMonitor@" + pool.poolName + "] Exiting."); 
    } 
} 
