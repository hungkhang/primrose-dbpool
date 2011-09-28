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
package uk.org.primrose.vendor.standalone; 
 
import uk.org.primrose.Logger; 
import uk.org.primrose.console.WebConsole; 
 
import java.io.IOException; 
 
 
public class PrimroseWebConsoleLoader { 
    public static void main(String[] args) throws Exception, Throwable { 
        if (args.length != 3) { 
            System.out.println( 
                "java uk.org.primrose.vendor.standalone.PrimroseWebConsoleLoader <username> <password> <port> &lt;log file name> &lt;log level>"); 
            System.out.println( 
                "EG:\njava uk.org.primrose.vendor.standalone.PrimroseWebConsoleLoader admin admin 8090 /var/log/primrose_console.log verbose,info,warn,error,crisis"); 
 
            System.exit(0); 
        } 
 
        load(args[0], args[1], Integer.parseInt(args[2]), args[3], args[4]); 
    } 
 
    public static void load(String username, String password, int port, 
        String logFileName, String logLevel) throws IOException { 
        Logger l = new Logger(); 
        l.setLogWriter(logFileName); 
        l.setLogLevel(logLevel); 
 
        WebConsole wc = new WebConsole(username, password, port, l); 
        wc.start(); 
    } 
} 
