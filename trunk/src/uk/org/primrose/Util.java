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
package uk.org.primrose; 
 
import uk.org.primrose.pool.CannotConnectException; 
import uk.org.primrose.pool.core.KeyValuePair; 
import java.io.BufferedReader; 
import java.io.ByteArrayOutputStream; 
import java.io.FileInputStream; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import java.io.PrintStream; 
 
import java.lang.reflect.Method; 
 
import java.sql.Connection; 
import java.sql.DriverManager; 
 
import java.util.Properties; 
 
 
public class Util { 
    /** 
     * Is the given value a valid pool config parameter ? 
     */ 
    public static boolean isConfigParameterValid(String name) { 
        if (name == null) { 
            return false; 
        } 
 
        for (String s : Constants.POOL_CONFIG_ITEM_NAMES) { 
            if (name.equals(s)) { 
                return true; 
            } 
        } 
 
        return false; 
    } 
 
    /** 
     * Using reflection, invoke a method on a given object Returns whether it 
     * found the method to run or not. 
     */ 
    public static boolean callClassMethod(Class<?> targetClass, 
        Object targetObj, String methodNameToRun, Object[] args) 
        throws GeneralException { 
        if (targetObj == null) { 
            throw new GeneralException("Target Object is null !"); 
        } 
 
        Method[] publicMethods = targetClass.getMethods(); 
        boolean foundMethodToRun = false; 
 
        int argsLength = 0; 
 
        if (args != null) { 
            argsLength = args.length; 
        } 
 
        for (Method element : publicMethods) { 
            String methodName = element.getName(); 
            int numOfArgs = element.getParameterTypes().length; 
 
            if (methodNameToRun.equalsIgnoreCase(methodName) && 
                    (numOfArgs == argsLength)) { 
                try { 
                    element.invoke(targetObj, args); 
                    foundMethodToRun = true; 
 
                    break; 
                } catch (Exception e) { 
                    throw new GeneralException("Error invoking " + 
                        targetClass.getName() + "." + methodNameToRun, e); 
                } 
            } 
        } 
 
        return foundMethodToRun; 
    } 
 
    /** 
     * Using reflection, print out the values of all 'get' methods for an object 
     */ 
    public static void printGetMethodValues(String prefixData, Logger logger, 
        Class<?> targetClass, Object targetObj) { 
        if (targetObj == null) { 
            return; 
        } 
 
        Method[] publicMethods = targetClass.getMethods(); 
 
        for (Method element : publicMethods) { 
            String methodName = element.getName(); 
            int numOfArgs = element.getParameterTypes().length; 
 
            if (methodName.startsWith("get") && (numOfArgs == 0)) { 
                try { 
                    // Don't print user or password info in the logs 
                    if ((methodName.toLowerCase() 
                                       .indexOf(Constants.PASSWORD.toLowerCase()) == -1) && 
                            (methodName.toLowerCase() 
                                           .indexOf(Constants.USER.toLowerCase()) == -1)) { 
                        String value = (String) element.invoke(targetObj, 
                                new Object[] {  }); 
                        methodName = methodName.substring(3, methodName.length()); 
 
                        String camelName = (methodName.charAt(0) + "").toLowerCase() + 
                            methodName.substring(1, methodName.length()); 
 
                        logger.info(prefixData + camelName + "='" + value + 
                            "'"); 
                    } 
                } catch (Exception e) { 
                    // don't care 
                } 
            } 
        } 
    } 
 
    /** 
     * Get connection to the database 
     */ 
    public static Connection getConnection(Logger logger, String driver, 
        String url, String username, String password) 
        throws CannotConnectException { 
        try { 
            DebugLogger.log("About to load driver into JVM : " + driver); 
            Class.forName(driver); 
            DebugLogger.log("About to get connect from db using url(" + driver + 
                "), user(" + username + "), password(" + password + ")"); 
 
            Connection c = DriverManager.getConnection(url, username, password); 
 
            if (c == null) { 
                throw new CannotConnectException( 
                    "When getting a new connection from the db, no error was thrown, but the connection was null"); 
            } 
 
            return c; 
        } catch (Exception e) { 
            String message = "Cannot connect to db using driver (" + driver + 
                "), url(" + url + "), username(" + username + ")"; 
            logger.error(message); 
            logger.printStackTrace(e); 
 
            ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
            PrintStream ps = new PrintStream(baos); 
            e.printStackTrace(ps); 
 
            byte[] stack = baos.toByteArray(); 
 
            try { 
                baos.close(); 
            } catch (IOException e1) { 
                e1.printStackTrace(); 
            } 
 
            ps.close(); 
            logger.email(Constants.EXCEPTION_EVENT, 
                message + "\r\n" + new String(stack)); 
            throw new CannotConnectException(message, e); 
        } 
    } 
 
    /** 
     * Load multiple pools from a primrose config file 
     */ 
    public static Properties generatePropertiesForPoolName(String configFile, 
        String poolNameToFind) throws IOException { 
        Properties p = null; 
        BufferedReader br = new BufferedReader(new InputStreamReader( 
                    new FileInputStream(configFile))); 
        String line; 
 
        while ((line = br.readLine()) != null) { 
            line = line.trim(); 
 
            // Ignore comments and whitespace 
            if (line.startsWith("#") || (line.length() == 0)) { 
                continue; 
            } 
 
            KeyValuePair keyValuePair = new KeyValuePair(line); 
            keyValuePair.splitLine(); 
            String key = keyValuePair.getKey(); 
            String value = keyValuePair.getValue(); 
            String lastKey = null; 
 
 
            // handle multi line driverURL's 
            if (!Util.isConfigParameterValid(key) && (lastKey != null) && 
                    lastKey.equalsIgnoreCase(Constants.DRIVER_URL)) { 
                if (p != null) { 
                    p.setProperty(lastKey, p.getProperty(lastKey) + line); 
                } 
 
                continue; // continue on so we don't reset the true lastKey (ie lines of 
                          // multi-line driver are more than 1 ...) 
            } 
 
            if (key.equals(Constants.POOL_NAME)) { 
                if (p != null) { 
                    // more than one pool defined - break out 
                    // and return data 
                    break; 
                } 
 
                if (value.equals(poolNameToFind)) { 
                    p = new Properties(); 
                } 
            } 
 
            if (p != null) { 
                p.setProperty(key, value); 
            } 
 
            lastKey = key; 
 
        } 
 
        br.close(); 
 
        return p; 
    } 
} 
