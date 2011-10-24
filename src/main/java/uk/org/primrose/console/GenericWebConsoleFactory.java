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
package uk.org.primrose.console; 
 
import uk.org.primrose.*; 
 
import java.io.*; 
 
 
public class GenericWebConsoleFactory { 
    private static WebConsole wc = null; 
    private int port = -1; 
    private String logFile = null; 
    private Logger logger = null; 
    private String logLevel = ""; 
    private String username = null; 
    private String password = null; 
 
    public void setLogLevel(String logLevel) { 
        this.logLevel = logLevel; 
    } 
 
    public String getLogLevel() { 
        return logger.getLogLevel(); 
    } 
 
    public void setPort(String port) throws IOException { 
        this.port = Integer.parseInt(port); 
        start(); 
    } 
 
    public void setLogFile(String logFile) throws IOException { 
        this.logFile = logFile; 
        this.logger = new Logger(); 
        this.logger.setLogWriter(logFile); 
        this.logger.setLogLevel(this.logLevel); 
        start(); 
    } 
 
    public String getLogFile() { 
        return logFile; 
    } 
 
    public String getPort() { 
        return port + ""; 
    } 
 
    public void start() throws IOException { 
        if ((port > 0) && (logger != null)) { 
            wc = new WebConsole(username, password, port, logger); 
            wc.start(); 
        } 
    } 
 
    public void stop() throws IOException { 
        WebConsole.shutdown(); 
    } 
 
    public void setUsername(String username) { 
        this.username = username; 
 
        if (wc != null) { 
            wc.setUsername(username); 
        } 
    } 
 
    public void setPassword(String password) { 
        this.password = password; 
 
        if (wc != null) { 
            wc.setPassword(password); 
        } 
    } 
 
    public String getPassword() { 
        if (wc != null) { 
            return wc.getPassword(); 
        } 
 
        return null; 
    } 
 
    public String getUsername() { 
        if (wc != null) { 
            return wc.getUsername(); 
        } 
 
        return null; 
    } 
} 
