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
import uk.org.primrose.DebugLogger; 
import uk.org.primrose.Logger; 
import uk.org.primrose.Util; 
import uk.org.primrose.pool.*; 
import uk.org.primrose.pool.core.loadrules.*; 
 
import java.sql.*; 
 
import java.util.*; 
 
 
public class Pool extends PoolData { 
    /** 
    *        CTOR - init some stuff 
    */ 
    public Pool() { 
        lock = new PoolLock(); 
        DebugLogger.log("[Pool:" + this + " ... Creating new lock object : " + 
            lock); 
    } 
 
    /** 
    *        Get a pooled connection. 
    *        If the db is down, or all connections in the pool are busy, 
    *        then wait until we can find a connection ... 
    *         unless 1) queueConnectionRequests is false, 
    *        or     2) waitForConnectionIfDatabaseIsDown is false 
    *         whereupon we error, and the client will see a SQLException from 
    *        the data source object linked to this pool 
     
    */ 
    public final Connection getConnection() throws PoolException { 
        long lid = ++gid; 
 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + ",id=" + lid + 
                "] getConnection() start"); 
        } 
 
        // If we are not running in pooled more, then 
        // just give them a dedicated connection 
        if (!bRunPooledMode) { 
            return getNonPooledConnection(lid); 
        } 
 
        // If all connections are busy, then this throws PoolIsFulLException 
        // if the pool is configured so that it does not queue connection requests 
        Connection c = null; 
 
        // If we want to wait for a connection if the db is down 
        // then hide the CannotConnectException 
        // Else try and get a connection, and throw CannotConnectException (from the internalGetConnection() method) 
        // if we cannot get one ... 
        try { 
            c = internalGetConnection(lid); 
        } catch (CannotConnectException cce) { 
            if (DebugLogger.getEnabled()) { 
                DebugLogger.log("[Pool@" + poolName + ",id=" + lid + 
                    "] getConnection() Got exception(" + 
                    cce.getClass().getName() + ") getting connection ..."); 
            } 
 
            // Only throw an exception if we don't want to wait 
            // and there is no failoverPool 
            if (!bWaitForConnectionIfDatabaseIsDown && (failoverPool == null)) { 
                throw cce; 
            } 
 
            if (failoverPool != null) { 
                notifyExceptionEvent(); 
            } 
        } 
 
        // Got a connection - return it 
        if (c != null) { 
            if (DebugLogger.getEnabled()) { 
                DebugLogger.log("[Pool@" + poolName + ",id=" + lid + 
                    "] getConnection() got conn OK - returning"); 
            } 
 
            return c; 
        } 
 
        // If we get here, then it means that either the db is down 
        // and we want to wait until it is up, 
        // or the pool is full, and we want to wait till its not 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + ",id=" + lid + 
                "] no connection available - waitingThreads(" + 
                (numberOfWaitingThreads + 1) + ")"); 
        } 
 
        numberOfWaitingThreads++; 
 
        return getConnectionWait(lid); 
    } 
 
    /** 
    *        Can't find a connection - loop until we can ... 
    *         unless 1) queueConnectionRequests is false, 
    *        or     2) waitForConnectionIfDatabaseIsDown is false 
    *         whereupon we error, and the client will see a SQLException from 
    *        the data source object linked to this pool 
    */ 
    private final Connection getConnectionWait(long id) 
        throws PoolException { 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                "] getConnectionWait() start"); 
        } 
 
        try { 
            Thread.sleep(250); 
        } catch (InterruptedException e) { 
        } 
 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                "] After 250ms sleep, try to get connection ..."); 
        } 
 
        Connection c = null; 
 
        try { 
            c = internalGetConnection(id); 
        } catch (CannotConnectException cce) { 
            if (DebugLogger.getEnabled()) { 
                DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                    "] getConnectionWait() Got exception(" + 
                    cce.getClass().getName() + ") getting connection ..."); 
            } 
 
            // Only throw an exception if we don't want to wait 
            // and there is no failoverPool 
            if (!bWaitForConnectionIfDatabaseIsDown && 
                    (failoverPoolObj != null)) { 
                throw cce; 
            } 
 
            if ((failoverPoolObj == null) && (failoverPool != null)) { 
                notifyExceptionEvent(); 
            } 
        } 
 
        // Still cannot find a connection ... loop again 
        if (c == null) { 
            int numberOfActiveConnections = numberOfActiveConnections(); 
            logger.verbose("[Pool@" + poolName + ",id=" + id + 
                "] getConnectionWait() Could not find a connection (" + 
                numberOfActiveConnections + 
                " active) - sleeping, and trying again"); 
 
            return getConnectionWait(id); 
        } 
 
        // yay got one ... decrement the number of waiting threads and return the connection 
        numberOfWaitingThreads--; 
 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                "] getConnectionWait() got connection now, returning; numberOfWaitingThreads : " + 
                numberOfWaitingThreads); 
        } 
 
        return c; 
    } 
 
    /** 
     *         Initialize a ConnectionHolder for when handing out a new pooled connection 
     */ 
    private final void initializeConnection(ConnectionHolder ch, long id) { 
        ch.numberOfOpens++; 
 
        try { 
            throw new Exception(); 
        } catch (Exception e) { 
            ch.callStack = e.getStackTrace(); 
        } 
 
        ch.connOpenedDate = System.currentTimeMillis(); 
        ch.status = CONNECTION_ACTIVE; 
        ch.id = id; 
        ch.resultsetObjects = new Stack<PoolResultSet>(); 
        ch.statementObjects = new Stack<PoolStatement>(); 
    } 
 
    /** 
    *        Internal get Connection method 
    *         Throws PoolHasNoFreeConnections IFF there are no free connections and queueConnectionRequests == false. 
    *        Throws CannotConnectException if the pool is not full, but we cannot get a connection, or 
    *        an inactive connection is invalid, and we cannot get a new connection to replace it 
    */ 
    private final Connection internalGetConnection(long id) 
        throws PoolException { 
        logger.verbose("[Pool@" + poolName + ",id=" + id + 
            "] internalGetConnection() called ..."); 
 
        if (poolAccessLocked) { 
            logger.warn("[Pool@" + poolName + ",id=" + id + 
                "] getConnection() Pool access is locked ..."); 
            throw new PoolException("Access to the pool@" + poolName + 
                " is locked"); 
        } 
 
        // Have we failed over ? 
        if (failoverPoolObj != null) { 
            return failoverPoolObj.internalGetConnection(id); 
        } 
 
        ConnectionHolder retCH = null; 
 
        // We want to synchonize access to the core connection list 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                "] intGetConn() About to synch on lock"); 
        } 
 
        synchronized (lock) { 
            long now = System.currentTimeMillis(); 
 
            // Loop the connections, and try to find an inactive connection 
            if (DebugLogger.getEnabled()) { 
                DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                    "] intGetConn() About to loop connections, size : " + 
                    connections.size()); 
            } 
 
            for (ConnectionHolder ch : connections) { 
                if (DebugLogger.getEnabled()) { 
                    DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                        "] intGetConn() Connection : " + ch.toString()); 
                } 
 
                if (ch.status == CONNECTION_INACTIVE) { 
                    // Initialize the holder and connection 
                    initializeConnection(ch, id); 
                    retCH = ch; 
 
                    // Cycle connections past their configured number of calls 
                    // Dump it and go back for another 
                    if (iCycleConnections > -1) { 
                        if (ch.numberOfOpens == iCycleConnections) { 
                            logger.info("[Pool@" + poolName + ",id=" + id + 
                                "] getConnection() Dumping " + ch.conn + 
                                " because it has executed its max number of calls (" + 
                                iCycleConnections + ")"); 
 
                            try { 
                                ch.conn.closePhysical(); 
                            } catch (SQLException sqle) { 
                                logger.printStackTrace(sqle); 
                            } 
 
                            connections.remove(ch); 
 
                            return internalGetConnection(id); 
                        } 
                    } 
 
                    // Reset connection defaults 
                    // If this errors, then the connection will be closed, 
                    // and removed from the global connections list 
                    setConnectionDefaults(ch, id); 
 
                    logger.verbose("[Pool@" + poolName + ",id=" + id + 
                        "] getConnection() using id " + ch.conn.hashCode() + 
                        ", " + numberOfActiveConnections() + 
                        " in use, caller : " + getCallerString()); 
 
                    // found one ... break 
                    totalConnectionsHandedOut++; 
 
                    break; 
                } 
            } 
 
            // No connections in pool, or all active ... try and load a new one 
            if (retCH == null) { 
                int numberOfLoadedConnections = connections.size(); 
 
                // Load a connection if necessary, and if we can (ie active is less than base) 
                // Pool is not busy, and there is room to load a new connection 
                if (numberOfLoadedConnections < iBase) { 
                    logger.verbose("[Pool@" + poolName + ",id=" + id + 
                        "] getConnection() - No loaded connections in pool ... loaded(" + 
                        numberOfLoadedConnections + ") of base(" + iBase + ")"); 
 
                    logger.verbose("[Pool@" + poolName + ",id=" + id + 
                        "] getConnection() loading new connection ..."); 
 
                    // Load a new connection 
                    ConnectionHolder ch = loadConnection(false, id); 
                    // Initialize the holder and connection 
                    initializeConnection(ch, id); 
                    // Add it into the pool (marked as ACTIVE) 
                    connections.addElement(ch); 
                    // Increment the number of connections handed out 
                    totalConnectionsHandedOut++; 
                    retCH = ch; 
                } else if (numberOfLoadedConnections == iBase) { 
                    logger.verbose("[Pool@" + poolName + ",id=" + id + 
                        "] getConnection() - All loaded conn's in use... loaded(" + 
                        numberOfLoadedConnections + ") of base(" + iBase + ")"); 
 
                    StringBuffer stack = new StringBuffer(); 
 
                    try { 
                        throw new Exception(); 
                    } catch (Exception e) { 
                        StackTraceElement[] els = e.getStackTrace(); 
 
                        for (StackTraceElement el : els) { 
                            stack.append("\t"); 
                            stack.append(el.toString()); 
                            stack.append("\n"); 
                        } 
                    } 
 
                    logger.email(Constants.NO_FREE_CONNECTIONS_EVENT, 
                        "Pool has no free connections - in use(" + 
                        numberOfLoadedConnections + "), base(" + iBase + 
                        ") : " + new java.util.Date() + "\n" + 
                        stack.toString()); 
 
                    // They don't want to wait for a connection, but 
                    // wish to see an error populated through to the client 
                    // if there are no connections available 
                    if (!bQueueConnectionRequests) { 
                        throw new PoolHasNoFreeConnections(); 
                    } 
                } 
 
                // Loaded conn OK ... recurse to pick up 
                //return internalGetConnection(); 
            } 
 
            logger.verbose("[Pool@" + poolName + ",id=" + id + 
                "] getConnection() took " + (System.currentTimeMillis() - now) + 
                "ms"); 
        } // end synch block 
 
        if (retCH == null) { 
            return null; 
        } 
 
        // Check if the connection is OK 
        // If it is not, dump it, and load another, and then recurse back 
        // If it looks like the db is down, then depending on the value 
        // of waitForConnectionIfDatabaseIsDown, then either throw an exception 
        // or recurse 
        boolean conOK = checkIfConnectionIsValid(retCH.conn, id); 
 
        if (!conOK) { 
            logger.verbose("[Pool@" + poolName + ",id=" + id + 
                "] getConnection() Dumping " + retCH.conn + 
                " because it failed validity checks."); 
 
            try { 
                retCH.conn.closePhysical(); 
            } catch (SQLException sqle) { 
                logger.printStackTrace(sqle); 
            } 
 
            connections.remove(retCH); 
 
            this.notifyExceptionEvent(); 
 
            try { 
                Thread.sleep(250); 
            } catch (InterruptedException ie) { 
            } 
 
            return internalGetConnection(id); 
        } 
 
        return retCH.conn; 
    } 
 
    /** 
     * Get who called the getConnection() method 
     * @return String 
     */ 
    private String getCallerString() { 
        try { 
            throw new Exception(); 
        } catch (Exception e) { 
            int maxBacktraceCount = 5; 
            StackTraceElement[] ste = e.getStackTrace(); 
            StringBuffer caller = new StringBuffer(100); 
            boolean start = false; 
 
            for (int i = 0; i < ste.length; i++) { 
                if (ste[i].getClassName().endsWith("PrimroseDataSource")) { 
                    start = true; 
 
                    continue; 
                } 
 
                if (start && (maxBacktraceCount > 0)) { 
                    maxBacktraceCount--; 
 
                    if (maxBacktraceCount == 0) { 
                        caller.append(ste[i].getFileName() + "[method:" + 
                            ste[i].getMethodName() + ",line:" + 
                            ste[i].getLineNumber() + "]"); 
                    } else { 
                        caller.append(ste[i].getFileName() + "[method:" + 
                            ste[i].getMethodName() + ",line:" + 
                            ste[i].getLineNumber() + "], "); 
                    } 
                } 
            } 
 
            return caller.toString(); 
        } 
    } 
 
    /** 
    *        Apply default connection properties to the connection 
    *        Called from fill() when a brand new connection is added to the pool, 
    *        and from put() when a connection is returned to the pool 
    *        If the check methods fail (SQLException), the underlying connection will be closed down, 
    *        and removed from the pool list 
    */ 
    protected final void setConnectionDefaults(ConnectionHolder ch, long id) 
        throws PoolException { 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                "] setConnectionDefaults() start"); 
        } 
 
        Connection c = ch.conn; 
 
        // set the default auto commit value 
        try { 
            if (c.getAutoCommit() != bConnectionAutoCommit) { 
                // don't log changes to driver defaults if the connection 
                // is brand new 
                if (ch.numberOfOpens > 0) { 
                    logger.warn("[Pool@" + poolName + ",id=" + id + 
                        "] setConnectionDefaults() : Checking autocommit value : Looks like someone has changed it from the default, and has not set it back. Default should be '" + 
                        bConnectionAutoCommit + 
                        "', but the connection value is '" + c.getAutoCommit() + 
                        "'"); 
                } 
 
                c.setAutoCommit(bConnectionAutoCommit); 
            } 
        } catch (SQLException sqle) { 
            logger.printStackTrace(sqle); 
            ch.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
            ch.conn.close(); 
            connections.remove(ch); 
            logger.warn("[Pool@" + poolName + ",id=" + id + 
                "] setConnectionDefaults() : Error checking auto commit. Connection will be dumped."); 
            notifyExceptionEvent(); 
            throw new CannotConnectException( 
                "Checking auto commit value errored : " + sqle.toString(), sqle); 
        } 
 
        // set the default transaction isolation level 
        // if the user has specified they want it in primrose.config. 
        // Else, just leave it as the default 
        if (iConnectionTransactionIsolation != -1) { 
            try { 
                // if it is -1, then set the default 
                //if (iConnectionTransactionIsolation == -1) { 
                //	setInternalConnectionTransactionIsolation(null); 
                //} 
                // if the connections setting does not equal the pool's, 
                // then set it (and log that somone changed it, but did not set it back 
                if (c.getTransactionIsolation() != iConnectionTransactionIsolation) { 
                    // don't log changes to driver defaults if the connection 
                    // is brand new 
                    if (ch.numberOfOpens > 0) { 
                        logger.warn("[Pool@" + poolName + ",id=" + id + 
                            "] setConnectionDefaults() : Checking transaction isolation level : Looks like someone has changed it from the default, and has not set it back. Default should be '" + 
                            getInternalConnectionTransactionIsolation() + 
                            "', but the connection value is '" + 
                            getInternalConnectionTransactionIsolation( 
                                c.getTransactionIsolation()) + "'"); 
                    } 
 
                    c.setTransactionIsolation(iConnectionTransactionIsolation); 
                } 
            } catch (SQLException sqle) { 
                logger.printStackTrace(sqle); 
                ch.closeBehaviour = Pool.ON_CLOSE_SHOULD_DIE; 
                ch.conn.close(); 
                connections.remove(ch); 
                logger.warn("[Pool@" + poolName + ",id=" + id + 
                    "] setConnectionDefaults() : Error checking transaction isolation level. Connection will be dumped."); 
                notifyExceptionEvent(); 
                throw new CannotConnectException( 
                    "Checking transaction isolation level value errored : " + 
                    sqle.toString(), sqle); 
            } 
        } 
 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                "] setConnectionDefaults() leave"); 
        } 
    } 
 
    /** 
    *        Check if a connection is valid. This calls isClosed() (once we go to Java 6, we can call isValid()) 
    *        If user has configured "checkSQL" option, we run that also. 
    *        If either checks fail, the connection is dumped. 
    */ 
    private final boolean checkIfConnectionIsValid(Connection c, long id) { 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                "] checkIfConnectionIsValid() start"); 
        } 
 
        long now = System.currentTimeMillis(); 
 
        boolean checkret = false; 
 
        try { 
            checkret = c.isClosed(); 
            checkret = true; 
        } catch (SQLException e) { 
            logger.error("[Pool@" + poolName + ",id=" + id + 
                "] checkIfConnectionIsValid() : Error calling isClosed() on connection " + 
                c + " : " + e); 
        } 
 
        // If its false (ie isClosed() returned false, then return false 
        if (!checkret) { 
            return false; 
        } 
 
        // Only run a check if they want it 
        if ((checkSQL != null) && (checkSQL.length() > 0)) { 
            logger.verbose("[Pool@" + poolName + ",id=" + id + 
                "] checkIfConnectionIsValid() : running checkSQL : " + 
                checkSQL); 
            checkret = ((PoolConnection) c).runCheckSQL(checkSQL); 
        } 
 
        logger.verbose("[Pool@" + poolName + ",id=" + id + 
            "] checkIfConnectionIsValid(" + checkret + ") : took " + 
            (System.currentTimeMillis() - now) + " ms"); 
 
        return checkret; 
    } 
 
    /** 
     * Get a non pooled connection - ie just a dedicated connection 
     * that someone can use once and once only. 
     * So when the client calls close(), the connection dies off 
     * @return a Connection object 
     */ 
    private final Connection getNonPooledConnection(long id) 
        throws PoolException { 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                "] getNonPooledConnection() start"); 
        } 
 
        ConnectionHolder ch = loadConnection(false, id); 
        initializeConnection(ch, id); 
        // override the close behaviour because its a non-pooled connection 
        ch.closeBehaviour = ON_CLOSE_SHOULD_DIE; 
 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                "] getNonPooledConnection() end : " + ch.conn); 
        } 
 
        return ch.conn; 
    } 
 
    /** 
     * Get a ConnectionHolder object, with the connection in it. 
     * 
     * @param addToList - should the ConnectionHolder be added to the list 
     * of pooled connections 
     * @return a ConnectionHolder object which contains the connection 
     * @throws PoolException 
     */ 
    private final ConnectionHolder loadConnection(boolean addToList, long id) 
        throws PoolException { 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + ",id=" + id + 
                "] loadConnection() start"); 
        } 
 
        Connection raw = Util.getConnection(logger, driverClass, driverURL, 
                user, password); 
        ConnectionHolder ch = new ConnectionHolder(); 
        PoolConnection pc = new PoolConnection(raw, ch); 
        ch.conn = pc; 
        ch.closeBehaviour = ON_CLOSE_SHOULD_REUSE; 
        ch.status = CONNECTION_INACTIVE; 
        ch.lock = lock; 
        ch.poolName = poolName; 
        ch.bDumpConnectionOnSQLException = this.bDumpConnectionOnSQLException; 
        ch.myPool = this; 
        ch.logger = this.logger; 
 
        setConnectionDefaults(ch, id); 
 
        if (addToList) { 
            connections.addElement(ch); 
        } 
 
        return ch; 
    } 
 
    /** 
    *        Close down all connection objects, and remove them from the connection list 
    *        If force is true, then close the connection down immediately, else 
    *         leave them to finish their job, and then close them. This means that pools can 
    *        be stopped/started with no impact on live connections (ie safely). 
    */ 
    public final void stop(boolean force) throws PoolException { 
        if (bPoolHasBeenShutdown) { 
            logger.warn("[Pool@" + poolName + 
                "] stop() Pool has already been shutdown ... not doing it twice."); 
 
            return; 
        } 
 
        logger.email(Constants.STOP_EVENT, 
            "Pool stopping at : " + new java.util.Date()); 
 
        logger.info("[Pool@" + poolName + "] stop() Stopping pool with force=" + 
            force + " ..."); 
        // Lock access to the pool 
        poolAccessLocked = true; 
 
        // Kill the cutback failover thread if it exists 
        if (failoverCutBackObj != null) { 
            failoverCutBackObj.stopIt(); 
        } 
 
        // Close down each connection 
        for (ConnectionHolder ch : connections) { 
            // If we are forcing a close, just dump it 
            // Else, only dump if not busy 
            // If they are busy, and we are not forcing close, then mark the connection 
            // as not returning to the pool once its free (ie dump after work is done) 
            try { 
                if (force) { 
                    ch.conn.closePhysical(); 
                } else { 
                    if (ch.status == CONNECTION_INACTIVE) { 
                        ch.conn.closePhysical(); 
                    } else { 
                        ch.closeBehaviour = ON_CLOSE_SHOULD_DIE; 
                    } 
                } 
            } catch (SQLException e) { 
                logger.printStackTrace(e); 
            } 
        } 
 
        connections.removeAllElements(); 
 
        logger.info("[Pool@" + poolName + 
            "] stop() Shutting down pool monitor."); 
 
        if (monitor != null) { 
            monitor.shutdown(); 
        } 
 
        logger.info("[Pool@" + poolName + "] stop() Stop Complete."); 
        logger.close(); 
        bPoolHasBeenShutdown = true; 
    } 
 
    /** 
    *        Start the pool, filling it with the configured base of connections 
    */ 
    public final void start() throws PoolException { 
        // set up the logger 
        setUpLogger(); 
        gid = 0L; 
        bPoolHasBeenShutdown = false; 
 
        logger.info("[Pool@" + poolName + "] STARTING " + poolName + " ..."); 
 
        logger.verbose("[Pool@" + poolName + "] Checking " + poolName + 
            " parameters ..."); 
 
        for (LoadRule rule : loadRules) { 
            rule.runCheck(this, this.logger); 
        } 
 
        logger.email(Constants.START_EVENT, 
                "Pool starting at : " + new java.util.Date()); 
 
        // print some verbose logging 
        Util.printGetMethodValues("[Pool@" + poolName + "] config item : ", 
            logger, PoolConfigImpl.class, this); 
 
        // Lock access to the pool 
        poolAccessLocked = true; 
 
        // Make a new list 
        connections = new Vector<ConnectionHolder>(); 
        numberOfWaitingThreads = 0; 
        totalConnectionsHandedOut = 0; 
 
        if (iNumberOfConnectionsToInitializeWith > iBase) { 
            logger.warn("[Pool@" + poolName + 
                "] start() The number of connections to initialise with is greater than the number of base connections ... adjusting init number to base : " + 
                iBase); 
            iNumberOfConnectionsToInitializeWith = iBase; 
        } 
 
        logger.verbose("[Pool@" + poolName + "] start() Loading " + 
            iNumberOfConnectionsToInitializeWith + " Connection(s) on init"); 
 
        for (int i = 0; i<  iNumberOfConnectionsToInitializeWith; i++) { 
            try { 
                Connection pc = loadConnection(true, i).conn; 
 
                if (i == 0) { 
                    DatabaseMetaData conMD = pc.getMetaData(); 
                    logger.info("[Pool@" + poolName + 
                        "] start() Primrose version " + Constants.VERSION + 
                        ", release date " + Constants.RELEASE_DATE); 
                    logger.info("[Pool@" + poolName + 
                        "] start() JDBC Driver Name: " + conMD.getDriverName()); 
                    logger.info("[Pool@" + poolName + 
                        "] start() JDBC Driver Version: " + 
                        conMD.getDriverVersion()); 
                } 
            } catch (Throwable t) { 
                logger.warn("[Pool@" + poolName + 
                    "] start() Could not connect to db - is this OK ?"); 
                logger.printStackTrace(t); 
            } 
        } 
 
        logger.info("[Pool@" + poolName + 
            "] start() Starting new pool monitor."); 
        monitor = new PoolMonitor(this, logger); 
        monitor.start(); 
 
        logger.info("[Pool@" + poolName + "] start() Load complete."); 
 
        // Unlock access to the pool - make it available 
        poolAccessLocked = false; 
    } 
 
    public final Vector<ConnectionHolder> getPoolConnections() { 
        return connections; 
    } 
 
    /** 
    *        Restart the pool, calling stop(), then start() 
    */ 
    public final void restart(boolean forceStop) throws PoolException { 
        logger.info("[Pool@" + poolName + "] restart() Restarting pool ..."); 
        stop(forceStop); 
        start(); 
        logger.info("[Pool@" + poolName + "] restart() Restart Complete ..."); 
    } 
 
    public Logger getLogger() { 
        return logger; 
    } 
 
    /** 
     *         If a SQLException occurs and the parameter 'dumpConnectionOnSQLException' is true (default) 
     *        then this method is called. 
     *        If the config requires emails to be sent on SQLExceptions, then send it. 
     *        If we require notification of a possible DB crash, then see if we have, using the 
     *         'onExceptionCheckSQL' parameter SQL. 
     *         If the config requires failover, then attempt that (if the db has crashed). 
     */ 
    protected void notifyExceptionEvent() { 
        if (emailEvents != null) { 
            boolean notifyException = emailEvents.toUpperCase() 
                                                 .indexOf(Constants.EXCEPTION_EVENT.toUpperCase()) > -1; 
            boolean notifyCrash = emailEvents.toUpperCase() 
                                             .indexOf(Constants.DBCRASH_EVENT.toUpperCase()) > -1; 
 
            if (notifyException) { 
                logger.email(Constants.EXCEPTION_EVENT, 
                    "SQLException has occured in pool " + poolName); 
            } 
 
            if (notifyCrash) { 
                boolean bHasCrashed = hasDbCrashed(); 
 
                if (notifyCrash && bHasCrashed) { 
                    logger.email(Constants.DBCRASH_EVENT, 
                        "Database seems to have crashed ! Driver URL : " + 
                        driverURL); 
                } 
 
                // If we don't want failover, or we already have failed over, then ignore 
                if ((failoverPool != null) && (failoverPoolObj == null)) { 
                    attemptFailover(); 
                } 
            } 
        } 
 
        // If we want failover, and we habe not already failed over, then 
        // attempt a failover 
        if ((failoverPool != null) && (failoverPoolObj == null)) { 
            boolean bHasCrashed = hasDbCrashed(); 
 
            if (bHasCrashed) { 
                attemptFailover(); 
            } 
        } 
    } 
 
    /* 
     * Work out if the db has crashed by running 
     */ 
    private boolean hasDbCrashed() { 
        boolean bHasCrashed = false; 
 
        logger.verbose("[Pool@" + poolName + 
            "] About to see if DB has crashed (get new connection & run onExceptionCheckSQL)..."); 
 
        try { 
            if (DebugLogger.getEnabled()) { 
                DebugLogger.log("[Pool@" + poolName + 
                    "] hasDbCrashed() Loading connection ..."); 
            } 
 
            //Throws exception if fails ( == yes, failover) 
            ConnectionHolder ch = loadConnection(false, -7777); 
            Connection c = ch.conn; 
 
            // false == fail check, true == passed check 
            // so bHasCrashed = !checkval 
            if (DebugLogger.getEnabled()) { 
                DebugLogger.log("[Pool@" + poolName + 
                    "] hasDbCrashed() Running onExceptionCheckSQL statement ..."); 
            } 
 
            bHasCrashed = ((PoolConnection) c).runCheckSQL(onExceptionCheckSQL); 
 
            if (DebugLogger.getEnabled()) { 
                DebugLogger.log("[Pool@" + poolName + 
                    "] hasDbCrashed() runCheckSQL returned " + bHasCrashed); 
            } 
 
            bHasCrashed = !bHasCrashed; 
 
            try { 
                if (c != null) { 
                    c.close(); 
                } 
            } catch (SQLException sqle) { 
            } 
        } catch (PoolException pe) { 
            logger.printStackTrace(pe); 
            bHasCrashed = true; 
        } 
 
        if (DebugLogger.getEnabled()) { 
            DebugLogger.log("[Pool@" + poolName + "] hasDbCrashed ? : " + 
                bHasCrashed); 
        } 
 
        return bHasCrashed; 
    } 
 
    /* 
     * If failoverPool is set, and we have failed over, and then the DB is back up 
     * then cutback to this pool. 
     */ 
    public void cutbackFromFailoverPool() { 
        logger.info("[Pool@" + poolName + 
            "] Cutting back to this pool from failoverPool " + failoverPool); 
        logger.email(Constants.CUTBACK_EVENT, 
            "Cutting back to this pool from failoverPool " + failoverPool); 
 
        try { 
            stop(false); 
            start(); 
            failoverPoolObj = null; 
            logger.info("[Pool@" + poolName + 
                "] Cutback to original pool succeeded"); 
            logger.email(Constants.CUTBACK_EVENT, 
                "Cutback to original pool succeeded"); 
        } catch (PoolException pe) { 
            logger.error("Cutback failed ..."); 
            logger.printStackTrace(pe); 
            logger.email(Constants.CUTBACK_EVENT, "Cutback failed : " + pe); 
        } 
    } 
 
    /** 
     *         If a SQLException occurs and the parameter 'dumpConnectionOnSQLException' is true (default) 
     *         and the parameter 'failoverPool' is set, then see if we should failover. 
     */ 
    private void attemptFailover() { 
        logger.info("[Pool@" + poolName + "] Now attemping failover to pool '" + 
            failoverPool + "'"); 
        logger.email(Constants.FAILOVER_EVENT, 
            "Now attemping failover to pool '" + failoverPool + "'"); 
 
        try { 
            logger.info("[Pool@" + poolName + "] Finding failoverPool(" + 
                failoverPool + ")"); 
            // Restart and change the pool name 
            failoverPoolObj = PoolLoader.findExistingPool(failoverPool); 
 
            if (failoverPoolObj == null) { 
                throw new PoolException("Cannot find failoverPool(" + 
                    failoverPool + ") !"); 
            } 
 
            logger.info("[Pool@" + poolName + "] Stopping this pool ..."); 
            stop(true); 
 
            poolAccessLocked = false; 
            logger.info("[Pool@" + poolName + 
                "] Testing that can get connection from failoverPool ..."); 
 
            Connection c = getConnection(); 
 
            try { 
                if (c != null) { 
                    c.close(); 
                } 
            } catch (SQLException sqle) { 
            } 
 
            logger.info("[Pool@" + poolName + 
                "] Starting failover cutback monitor ..."); 
            failoverCutBackObj = new FailoverCutBack(this, logger); 
            failoverCutBackObj.start(); 
 
            logger.info("[Pool@" + poolName + 
                "] Failover complete. Routing all requests to " + poolName + 
                " to " + failoverPool); 
            logger.email(Constants.FAILOVER_EVENT, 
                "Failover complete. Routing all requests to " + poolName + 
                " to " + failoverPool); 
        } catch (PoolException pe) { 
            logger.error("[Pool@" + poolName + "] Failover failed : " + 
                pe.toString()); 
            logger.email(Constants.FAILOVER_EVENT, 
                "Failover failed : " + pe.toString()); 
            logger.printStackTrace(pe); 
        } 
    } 
} 
