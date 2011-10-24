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
 
import uk.org.primrose.Logger; 
import uk.org.primrose.Util; 
 
import java.io.*; 
 
import java.sql.*; 
 
import java.util.*; 
 
 
public class PoolData extends PoolConfigImpl { 
    // Connection status codes 
     
    /** Connection status is unknown */ 
    public static final int UNKNOWN_STATUS_CODE = -1; 
     
    /** Connection is currently in use */ 
    public static final int CONNECTION_ACTIVE = 1; 
     
    /** Connection is currently not in use */ 
    public static final int CONNECTION_INACTIVE = 2; 
     
    /** Connection has been released */ 
    public static final int CONNECTION_DUMPED = 3; 
     
    /** Connection is in the process of being released (normally due to timeout) */ 
    public static final int CONNECTION_CLOSING = 4; 
 
    // Connection behviours 
    public static final int ON_CLOSE_SHOULD_DIE = 14; 
    public static final int ON_CLOSE_SHOULD_REUSE = 15; 
 
    // Global id for a connection 
    protected long gid = 0L; 
    // A list of connections (and status's etc) 
    Vector<ConnectionHolder> connections = null; 
 
    // Whether the pool can give out connections 
    boolean poolAccessLocked = false; 
 
    // Global lock object 
    protected PoolLock lock = null; 
 
    // The number of waiting threads waiting for a connection 
    protected int numberOfWaitingThreads = 0; 
    protected int totalConnectionsHandedOut = 0; 
 
    // A monitor on the pool 
    PoolMonitor monitor = null; 
 
    // The pool's logger 
    protected Logger logger = null; 
 
    // The pool to failover to (if specified) 
    Pool failoverPoolObj = null; 
 
    // A thread to monitor if this DB comes back (and cutback) 
    FailoverCutBack failoverCutBackObj = null; 
 
    // Is the pool shutdown ? 
    boolean bPoolHasBeenShutdown = false; 
 
    protected void setUpLogger() { 
        logger = new Logger(); 
 
        if ((log != null) && (log.length() > 0)) { 
            try { 
                logger.setEmailDetails(emailEvents, adminEmail, 
                    smtpMailExchangeServer, smtpMailExchangeServerPort, poolName); 
                logger.setLogLevel(logLevel); 
                logger.setLogWriter(log); 
            } catch (IOException ioe) { 
                System.out.println("Primrose cannot write to log file : " + 
                    log); 
                ioe.printStackTrace(); 
            } 
        } 
    } 
 
    /** 
    *        Get the number of waiting threads 
    */ 
    public int getNumberOfWaitingThreads() { 
        return numberOfWaitingThreads; 
    } 
 
    /** 
    *        Get the total number of connections handed out by the pool 
    */ 
    public int getTotalConnectionsHandedOut() { 
        return totalConnectionsHandedOut; 
    } 
 
    /** 
    *        Get a string representation for the connection status/behaviour 
    */ 
    public static String getStringStatus(int i) { 
        if (i == CONNECTION_ACTIVE) { 
            return "CONNECTION_ACTIVE"; 
        } else if (i == CONNECTION_INACTIVE) { 
            return "CONNECTION_INACTIVE"; 
        } else if (i == CONNECTION_DUMPED) { 
            return "CONNECTION_DUMPED"; 
        } else if (i == CONNECTION_CLOSING) { 
            return "CONNECTION_CLOSING"; 
        } else if (i == ON_CLOSE_SHOULD_DIE) { 
            return "ON_CLOSE_SHOULD_DIE"; 
        } else if (i == ON_CLOSE_SHOULD_REUSE) { 
            return "ON_CLOSE_SHOULD_REUSE"; 
        } else if (i == UNKNOWN_STATUS_CODE) { 
            return "UNKNOWN_STATUS_CODE"; 
        } 
 
        return "UNKNOWN_STATUS_CODE"; 
    } 
 
    /** 
    *        Get the current number of free connections 
    */ 
    public int numberOfFreeConnections() { 
        logger.verbose("START"); 
 
        int n = 0; 
 
        for (ConnectionHolder ch : connections) { 
            if (ch.status == CONNECTION_INACTIVE) { 
                logger.verbose("INACTIVE"); 
                n++; 
            } 
        } 
 
        return n; 
    } 
 
    /** 
    *        Get the current number of active connections 
    */ 
    public int numberOfActiveConnections() { 
        int n = 0; 
 
        for (ConnectionHolder ch : connections) { 
            if (ch.status == CONNECTION_ACTIVE) { 
                n++; 
            } 
        } 
 
        return n; 
    } 
 
    /** 
    *         Sets the default level for connection's isolation transaction level. 
    *        This can only be set on startup - not during runtime. 
    */ 
    protected void setInternalConnectionTransactionIsolation( 
        String sConnectionTransactionIsolation) { 
        // If not specified in the config file, then use the driver's default 
        if (sConnectionTransactionIsolation == null) { 
            try { 
                Connection c = Util.getConnection(logger, driverClass, 
                        driverURL, user, password); 
 
                // if the connection is null, then it means the db is down or not reachable 
                // so don't go any further. 
                // If this is the case, the when the db comes back up 
                // the transaction isolation level will -1, and the next 'get' on the Pool 
                // will cause this code to be run again to default the level if needs be. 
                if (c == null) { 
                    logger.warn("[Pool@" + poolName + 
                        "] setConnectionTransactionIsolation() : DB is down/not reachable, and because the pool config variable 'connectionTransactionLevel' is not set, it will be set when the db comes back up and the next call for a connection is made."); 
                    iConnectionTransactionIsolation = -1; 
 
                    return; 
                } 
 
                iConnectionTransactionIsolation = c.getTransactionIsolation(); 
                c.close(); 
            } catch (Throwable t) { 
                logger.printStackTrace(t); 
            } 
        } else if (sConnectionTransactionIsolation.equalsIgnoreCase( 
                    "TRANSACTION_NONE")) { 
            iConnectionTransactionIsolation = Connection.TRANSACTION_NONE; 
        } else if (sConnectionTransactionIsolation.equalsIgnoreCase( 
                    "TRANSACTION_READ_COMMITTED")) { 
            iConnectionTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED; 
        } else if (sConnectionTransactionIsolation.equalsIgnoreCase( 
                    "TRANSACTION_READ_UNCOMMITTED")) { 
            iConnectionTransactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED; 
        } else if (sConnectionTransactionIsolation.equalsIgnoreCase( 
                    "TRANSACTION_REPEATABLE_READ")) { 
            iConnectionTransactionIsolation = Connection.TRANSACTION_REPEATABLE_READ; 
        } else if (sConnectionTransactionIsolation.equalsIgnoreCase( 
                    "TRANSACTION_SERIALIZABLE")) { 
            iConnectionTransactionIsolation = Connection.TRANSACTION_SERIALIZABLE; 
 
            // If not recognized, then use the driver's default 
        } else { 
            logger.warn("[Pool@" + poolName + 
                "] setConnectionTransactionIsolation() : Do not recognize transaction level of '" + 
                sConnectionTransactionIsolation + "', using driver default"); 
 
            try { 
                Connection c = Util.getConnection(logger, driverClass, 
                        driverURL, user, password); 
                iConnectionTransactionIsolation = c.getTransactionIsolation(); 
                c.close(); 
            } catch (Throwable t) { 
                logger.printStackTrace(t); 
            } 
        } 
 
        logger.verbose("[Pool@" + poolName + 
            "] setConnectionTransactionIsolation() : Set connection transaction level to '" + 
            getInternalConnectionTransactionIsolation() + "'"); 
    } 
 
    /** 
    *         Gets the default level for connection's isolation transaction level. 
    */ 
    protected String getInternalConnectionTransactionIsolation() { 
        return getInternalConnectionTransactionIsolation(iConnectionTransactionIsolation); 
    } 
 
    /** 
    *         Gets the default level for connection's isolation transaction level. 
    */ 
    protected String getInternalConnectionTransactionIsolation(int level) { 
        String sLevel = "DRIVER_DEFAULT"; 
 
        switch (level) { 
        case Connection.TRANSACTION_NONE: 
            sLevel = "TRANSACTION_NONE"; 
 
            break; 
 
        case Connection.TRANSACTION_READ_COMMITTED: 
            sLevel = "TRANSACTION_READ_COMMITTED"; 
 
            break; 
 
        case Connection.TRANSACTION_READ_UNCOMMITTED: 
            sLevel = "TRANSACTION_READ_UNCOMMITTED"; 
 
            break; 
 
        case Connection.TRANSACTION_REPEATABLE_READ: 
            sLevel = "TRANSACTION_REPEATABLE_READ"; 
 
            break; 
 
        case Connection.TRANSACTION_SERIALIZABLE: 
            sLevel = "TRANSACTION_SERIALIZABLE"; 
 
            break; 
        } 
 
        return sLevel; 
    } 
 
    /* 
     * A trivial synch object used by Pool.java 
     */ 
    class PoolLock { 
    } 
 
    /** 
     * 
     * @author sedj 
     * On failover (if defined), then start a thread to monitor the broken 
     * DB to see if it comes back. If it does, the thread stops, and informs the 
     * Pool to cut back from the failover pool to the original pool 
     */ 
    class FailoverCutBack extends Thread { 
        Pool pool = null; 
        Logger logger = null; 
        boolean bKeepRunning = true; 
 
        public FailoverCutBack(Pool pool, Logger logger) { 
            this.pool = pool; 
            this.logger = logger; 
        } 
 
        public void run() { 
            while (bKeepRunning) { 
                try { 
                    Thread.sleep(10000); 
                } catch (InterruptedException ie) { 
                } 
 
                Connection c = null; 
                Statement s = null; 
                ResultSet rs = null; 
 
                try { 
                    c = Util.getConnection(logger, driverClass, driverURL, 
                            user, password); 
                    s = c.createStatement(); 
                    rs = s.executeQuery(onExceptionCheckSQL); 
                    logger.info("[FailoverCutBack@" + pool.getPoolName() + 
                        "] FailoverCutBack has determined that the original DB is back up ... informing pool to cut back"); 
                    pool.cutbackFromFailoverPool(); 
                    bKeepRunning = false; 
                } catch (Throwable t) { 
                } finally { 
                    try { 
                        if (rs != null) { 
                            rs.close(); 
                        } 
                    } catch (SQLException sqle2) { 
                    } 
 
                    try { 
                        if (s != null) { 
                            s.close(); 
                        } 
                    } catch (SQLException sqle2) { 
                    } 
 
                    try { 
                        if (c != null) { 
                            c.close(); 
                        } 
                    } catch (SQLException sqle2) { 
                    } 
                } 
            } 
        } 
 
        public void stopIt() { 
            bKeepRunning = false; 
        } 
    } 
} 
