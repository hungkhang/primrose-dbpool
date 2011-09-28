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
package uk.org.primrose; 
 
import java.io.*; 
 
import java.util.*; 
 
 
/** 
*        Debug Logging class for the pools (straight to stdout). 
*/ 
public class DebugLogger { 
    private static boolean enabled = false; 
 
    static { 
        try { 
        	String javaVersion = System.getProperty("java.version"); 
        	if (javaVersion != null && javaVersion.startsWith("1.4")) { 
                if ((System.getProperty("PRIMROSE_DEBUG") != null) && 
                        System.getProperty("PRIMROSE_DEBUG").trim() 
                                  .equalsIgnoreCase("TRUE")) { 
                    DebugLogger.setEnabled(true); 
                }        		 
        	} else { 
	            if ((System.getenv("PRIMROSE_DEBUG") != null) && 
	                    System.getenv("PRIMROSE_DEBUG").trim() 
	                              .equalsIgnoreCase("TRUE")) { 
	                DebugLogger.setEnabled(true); 
	            } 
        	} 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
 
    public static boolean getEnabled() { 
        return enabled; 
    } 
 
    public static void setEnabled(boolean bEnabled) { 
        enabled = bEnabled; 
    } 
 
    /** 
    *        Print a stack trace of the exception to stdout 
    */ 
    public static void printStackTrace(Throwable t) { 
        if (!enabled) { 
            return; 
        } 
 
        t.printStackTrace(System.out); 
    } 
 
    /** 
    *        Print to stdout a debug message 
    */ 
    public static void log(String message) { 
        if (!enabled) { 
            return; 
        } 
 
        Calendar now = Calendar.getInstance(); 
        String nowString = now.get(Calendar.DAY_OF_MONTH) + "/" + 
            (now.get(Calendar.MONTH) + 1) + "/" + now.get(Calendar.YEAR) + " " + 
            now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + 
            ":" + now.get(Calendar.SECOND); 
 
        message = nowString + " : PRIMROSE_DBG : " + message; 
        System.out.println(message); 
    } 
} 
