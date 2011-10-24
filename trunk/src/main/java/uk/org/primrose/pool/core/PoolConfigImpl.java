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
 
import uk.org.primrose.pool.core.loadrules.*; 
import uk.org.primrose.pool.core.loadrules.LoadRule; 
 
import java.sql.*; 
 
import java.util.*; 
 
 
/** 
 * 
 * @author sedj 
 * This class represents the Primrose Pool Config 
 * (commonly primrose.config or poolconfig.properties) file. 
 * To add new config parameters, you need to add an entry here, 
 * and also in the Constants.POOL_CONFIG_ITEM_NAMES List<String>. 
 * 
 */ 
public class PoolConfigImpl { 
    // The 'poolName' pool config property 
    protected String poolName = "not_set"; 
 
    // The 'failoverPool' pool config property 
    // If set, the primrose tries to use the other pool 
    protected String failoverPool = null; 
 
    // How many connections should we open when we initialize 
    protected int iNumberOfConnectionsToInitializeWith = 1; 
 
    // Address to send emails to when problems with the pool 
    protected String adminEmail = null; 
 
    // SMPT Mail Exchanger to send emails via when the adminEmail property is set 
    protected String smtpMailExchangeServer = null; 
     
    // SMPT Mail Exchanger Port to send emails via when the adminEmail property is set 
    // Set to port 25 by default 
    protected String smtpMailExchangeServerPort = "25";     
 
    // Pool events to email for 
    protected String emailEvents = null; 
 
    // The 'base' pool config property 
    protected int iBase = 5; 
 
    // The 'log' pool config property 
    protected String log = ""; 
 
    // The 'idleTime' pool config property 
    protected int iIdleTime = 120000; 
 
    // The 'logLevel' pool config property 
    protected String logLevel = "info,warn,error,crisis"; 
 
    // The 'driverClass' pool config property 
    protected String driverClass = ""; 
 
    // The 'driverURL' pool config property 
    protected String driverURL = ""; 
 
    // The 'user' pool config property 
    protected String user = ""; 
 
    // The 'password' pool config property 
    protected String password = ""; 
 
    // The 'killActiveConnectionsOverAge' pool config property 
    protected int iKillActiveConnectionsOverAge = -1; 
 
    // The 'cycleConnections' pool config property 
    protected int iCycleConnections = -1; 
 
    // The 'queueConnectionRequests' pool config property 
    protected boolean bQueueConnectionRequests = true; 
 
    // The 'runPooledMode' pool config property 
    protected boolean bRunPooledMode = true; 
 
    // The 'connectionAutoCommit' pool config property 
    protected boolean bConnectionAutoCommit = true; 
 
    // The 'connectionTransactionIsolation' pool config property 
    protected int iConnectionTransactionIsolation = -1; 
 
    // The 'checkSQL' pool config property 
    protected String checkSQL = null; 
 
    // The 'encryptionFileKey' pool config property 
    protected String encryptionFileKey = null; 
 
    // The 'waitForConnectionIfDatabaseIsDown' pool config property 
    protected boolean bWaitForConnectionIfDatabaseIsDown = false; 
 
    // The 'dumpConnectionOnSQLException' property which describes 
    // behaviour for when a SQLException occurs. If true, then the  
    // Pool will dump the connection when a SQLException occurs 
    protected boolean bDumpConnectionOnSQLException = true; 
 
    // A SQL statement to run if failoverPool is set, to see if a DB is down 
    // Used if you wish to email notifications of DB failures (see 'emailEvents' parameter) 
    // or required if 'failoverPool' is used.   
    protected String onExceptionCheckSQL = null; 
 
    // A list of rules to execute when the pool starts 
    protected List<LoadRule> loadRules = new ArrayList<LoadRule>(); 
 
    /** 
    *        Get the 'poolName' pool config property 
    */ 
    public String getPoolName() { 
        return poolName; 
    } 
 
    /** 
    *        Set the 'poolName' pool config property 
    */ 
    public void setPoolName(String poolName) { 
        this.poolName = poolName; 
    } 
 
    /** 
    *        Get the 'base' pool config property 
    */ 
    public String getBase() { 
        return iBase + ""; 
    } 
 
    /** 
    *        Set the 'base' pool config property 
    */ 
    public void setBase(String base) { 
        this.iBase = Integer.parseInt(base); 
    } 
 
    /** 
    *        Get the 'base' pool config property 
    */ 
    public String getEmailEvents() { 
        return emailEvents; 
    } 
 
    /** 
    *        Set the 'base' pool config property 
    */ 
    public void setEmailEvents(String emailEvents) { 
        this.loadRules.add(new EmailEvents()); 
        this.emailEvents = emailEvents; 
    } 
 
    /** 
     *        Get the 'smtpMailExchangeServerPort' pool config property 
     */ 
     public String getSmtpMailExchangeServerPort() { 
         return this.smtpMailExchangeServerPort; 
     } 
 
     /** 
     *        Set the 'smtpMailExchangeServerPort' pool config property 
     */ 
     public void setSmtpMailExchangeServerPort(String smtpMailExchangeServerPort) { 
         this.smtpMailExchangeServerPort = smtpMailExchangeServerPort; 
     }     
 
    /** 
    *        Get the 'smtpMailExchangeServer' pool config property 
    */ 
    public String getSmtpMailExchangeServer() { 
        return this.smtpMailExchangeServer; 
    } 
 
    /** 
    *        Set the 'smtpMailExchangeServer' pool config property 
    */ 
    public void setSmtpMailExchangeServer(String smtpMailExchangeServer) { 
        this.smtpMailExchangeServer = smtpMailExchangeServer; 
    } 
 
    /** 
    *        Get the 'adminEmail' pool config property 
    */ 
    public String getAdminEmail() { 
        return this.adminEmail; 
    } 
 
    /** 
    *        Set the 'adminEmail' pool config property 
    */ 
    public void setAdminEmail(String adminEmail) { 
        this.adminEmail = adminEmail; 
    } 
 
    /** 
    *        Get the 'numberOfConnectionsToInitializeWith' pool config property 
    */ 
    public String getNumberOfConnectionsToInitializeWith() { 
        return iNumberOfConnectionsToInitializeWith + ""; 
    } 
 
    /** 
    *        Set the 'numberOfConnectionsToInitializeWith' pool config property 
    */ 
    public void setNumberOfConnectionsToInitializeWith( 
        String numberOfConnectionsToInitializeWith) { 
        this.iNumberOfConnectionsToInitializeWith = Integer.parseInt(numberOfConnectionsToInitializeWith); 
    } 
 
    /** 
    *        Get the 'log' pool config property 
    */ 
    public String getLog() { 
        return log; 
    } 
 
    /** 
    *        Set the 'log' pool config property 
    */ 
    public void setLog(String log) { 
        this.log = log; 
    } 
 
    /** 
    *        Get the 'idleTime' pool config property 
    */ 
    public String getIdleTime() { 
        return iIdleTime + ""; 
    } 
 
    /** 
    *        Set the 'idleTime' pool config property 
    */ 
    public void setIdleTime(String idleTime) { 
        this.iIdleTime = Integer.parseInt(idleTime); 
    } 
 
    /** 
    *        Get the 'logLevel' pool config property 
    */ 
    public String getLogLevel() { 
        return logLevel; 
    } 
 
    /** 
    *        Set the 'logLevel' pool config property 
    */ 
    public void setLogLevel(String logLevel) { 
        this.logLevel = logLevel; 
    } 
 
    /** 
    *        Get the 'driverClass' pool config property 
    */ 
    public String getDriverClass() { 
        return driverClass; 
    } 
 
    /** 
    *        Set the 'driverClass' pool config property 
    */ 
    public void setDriverClass(String driverClass) { 
        this.driverClass = driverClass; 
    } 
 
    /** 
    *        Get the 'driverURL' pool config property 
    */ 
    public String getDriverURL() { 
        return driverURL; 
    } 
 
    /** 
    *        Set the 'driverURL' pool config property 
    */ 
    public void setDriverURL(String driverURL) { 
        this.driverURL = driverURL; 
    } 
 
    /** 
    *        Get the 'user' pool config property 
    */ 
    public String getUser() { 
        return user; 
    } 
 
    /** 
    *        Set the 'user' pool config property 
    */ 
    public void setUser(String user) { 
        this.user = user; 
    } 
 
    /** 
    *        Get the 'password' pool config property 
    */ 
    public String getPassword() { 
        return password; 
    } 
 
    /** 
    *        Set the 'password' pool config property 
    */ 
    public void setPassword(String password) { 
        this.password = password; 
    } 
 
    /** 
    *        Get the 'killActiveConnectionsOverAge' pool config property 
    */ 
    public String getKillActiveConnectionsOverAge() { 
        return iKillActiveConnectionsOverAge + ""; 
    } 
 
    /** 
    *        Set the 'killActiveConnectionsOverAge' pool config property 
    */ 
    public void setKillActiveConnectionsOverAge( 
        String killActiveConnectionsOverAge) { 
        iKillActiveConnectionsOverAge = Integer.parseInt(killActiveConnectionsOverAge); 
    } 
 
    /** 
    *        Get the 'cycleConnections' pool config property 
    */ 
    public String getCycleConnections() { 
        return iCycleConnections + ""; 
    } 
 
    /** 
    *        Set the 'cycleConnections' pool config property 
    */ 
    public void setCycleConnections(String cycleConnections) { 
        iCycleConnections = Integer.parseInt(cycleConnections); 
    } 
 
    /** 
    *        Get the 'queueConnectionRequests' pool config property 
    */ 
    public String getQueueConnectionRequests() { 
        return bQueueConnectionRequests + ""; 
    } 
 
    /** 
    *        Set the 'queueConnectionRequests' pool config property 
    */ 
    public void setQueueConnectionRequests(String queueConnectionRequests) { 
        this.bQueueConnectionRequests = Boolean.valueOf(queueConnectionRequests) 
                                               .booleanValue(); 
    } 
 
    /** 
    *        Get the 'runPooledMode' pool config property 
    */ 
    public String getRunPooledMode() { 
        return bRunPooledMode + ""; 
    } 
 
    /** 
    *        Set the 'runPooledMode' pool config property 
    */ 
    public void setRunPooledMode(String runPooledMode) { 
        this.bRunPooledMode = Boolean.valueOf(runPooledMode).booleanValue(); 
    } 
 
    /** 
    *        Get the 'connectionAutoCommit' pool config property 
    */ 
    public String getConnectionAutoCommit() { 
        return bConnectionAutoCommit + ""; 
    } 
 
    /** 
    *        Set the 'connectionAutoCommit' pool config property 
    */ 
    public void setConnectionAutoCommit(String connectionAutoCommit) { 
        this.bConnectionAutoCommit = Boolean.valueOf(connectionAutoCommit) 
                                            .booleanValue(); 
    } 
 
    /** 
    *        Get the 'connectionTransactionIsolation' pool config property 
    */ 
    public String getConnectionTransactionIsolation() { 
        return iConnectionTransactionIsolation + ""; 
    } 
 
    /** 
    *        Set the 'connectionTransactionIsolation' pool config property 
    */ 
    public void setConnectionTransactionIsolation( 
        String connectionTransactionIsolation) { 
        //bUserSpecifiedTransIsoLevel = true; 
        // Did the user specify they wanted a specific transaction isolation level 
        // If not, then we won't bother checking it each time we hand a connection 
        // out, as some drivers call the db to find out the level  
        //protected boolean bUserSpecifiedTransIsoLevel = false; 
        if (connectionTransactionIsolation.equalsIgnoreCase("TRANSACTION_NONE")) { 
            iConnectionTransactionIsolation = Connection.TRANSACTION_NONE; 
        } else if (connectionTransactionIsolation.equalsIgnoreCase( 
                    "TRANSACTION_READ_COMMITTED")) { 
            iConnectionTransactionIsolation = Connection.TRANSACTION_READ_COMMITTED; 
        } else if (connectionTransactionIsolation.equalsIgnoreCase( 
                    "TRANSACTION_READ_UNCOMMITTED")) { 
            iConnectionTransactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED; 
        } else if (connectionTransactionIsolation.equalsIgnoreCase( 
                    "TRANSACTION_REPEATABLE_READ")) { 
            iConnectionTransactionIsolation = Connection.TRANSACTION_REPEATABLE_READ; 
        } else if (connectionTransactionIsolation.equalsIgnoreCase( 
                    "TRANSACTION_SERIALIZABLE")) { 
            iConnectionTransactionIsolation = Connection.TRANSACTION_SERIALIZABLE; 
        } else { 
            iConnectionTransactionIsolation = -1; 
        } 
    } 
 
    /** 
    *        Get the 'checkSQL' pool config property 
    */ 
    public String getCheckSQL() { 
        return checkSQL; 
    } 
 
    /** 
    *        Set the 'checkSQL' pool config property 
    */ 
    public void setCheckSQL(String checkSQL) { 
        this.checkSQL = checkSQL; 
    } 
 
    /** 
    *        Get the 'encryptionFileKey' pool config property 
    */ 
    public String getEncryptionFileKey() { 
        return encryptionFileKey; 
    } 
 
    /** 
    *        Set the 'encryptionFileKey' pool config property 
    */ 
    public void setEncryptionFileKey(String encryptionFileKey) { 
        this.encryptionFileKey = encryptionFileKey; 
    } 
 
    /** 
    *        Get the 'waitForConnectionIfDatabaseIsDown' pool config property 
    */ 
    public String getWaitForConnectionIfDatabaseIsDown() { 
        return bWaitForConnectionIfDatabaseIsDown + ""; 
    } 
 
    /** 
    *        Set the 'waitForConnectionIfDatabaseIsDown' pool config property 
    */ 
    public void setWaitForConnectionIfDatabaseIsDown( 
        String waitForConnectionIfDatabaseIsDown) { 
        this.loadRules.add(new WaitForConnectionIfDatabaseIsDown()); 
        this.bWaitForConnectionIfDatabaseIsDown = Boolean.valueOf(waitForConnectionIfDatabaseIsDown) 
                                                         .booleanValue(); 
    } 
 
    /** 
    *        Get the 'dumpConnectionOnSQLException' pool config property 
    */ 
    public String getDumpConnectionOnSQLException() { 
        return bDumpConnectionOnSQLException + ""; 
    } 
 
    /** 
    *        Set the 'dumpConnectionOnSQLException' pool config property 
    */ 
    public void setDumpConnectionOnSQLException( 
        String dumpConnectionOnSQLException) { 
        this.bDumpConnectionOnSQLException = Boolean.valueOf(dumpConnectionOnSQLException) 
                                                    .booleanValue(); 
    } 
 
    /** 
    *        Get the 'failoverPool' pool config property 
    */ 
    public String getFailoverPool() { 
        return failoverPool; 
    } 
 
    /** 
    *        Set the 'failoverPool' pool config property 
    */ 
    public void setFailoverPool(String failoverPool) { 
        this.failoverPool = failoverPool; 
        this.loadRules.add(new FailoverPool()); 
    } 
 
    /** 
     *        Get the 'onExceptionCheckSQL' pool config property 
     */ 
    public String getOnExceptionCheckSQL() { 
        return onExceptionCheckSQL; 
    } 
 
    /** 
     * Set the 'onExceptionCheckSQL' pool config property 
     */ 
    public void setOnExceptionCheckSQL(String onExceptionCheckSQL) { 
        this.onExceptionCheckSQL = onExceptionCheckSQL; 
    } 
} 
