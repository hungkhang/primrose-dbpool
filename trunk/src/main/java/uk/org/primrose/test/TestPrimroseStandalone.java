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
package uk.org.primrose.test; 
 
import uk.org.primrose.vendor.standalone.PrimroseLoader; 
import uk.org.primrose.vendor.standalone.PrimroseWebConsoleLoader; 
 
import java.sql.Connection; 
import java.sql.ResultSet; 
import java.sql.Statement; 
 
import java.util.List; 
 
import javax.naming.Context; 
import javax.naming.InitialContext; 
 
import javax.sql.DataSource; 
 
 
/** 
 * This class demonstrates how you may load primrose in a standalone Java 
 * application. It first loads a web console, to provide an interface for 
 * viewing pool activity and stopping/restarting pools on the fly. Then it loads 
 * some pools from a defined pool coniguration file. Note that in your 
 * application, you do not have to load the web console - it is entirely 
 * optional. 
 */ 
public class TestPrimroseStandalone { 
    public static void main(String[] args) throws Exception, Throwable { 
        if (args.length != 6) { 
            System.out.println( 
                "java uk.org.primrose.test.TestPrimroseStandalone  <config file> <webconsole_username> <webconsole_password> &lt;webconsole_port> &lt;log file name> &lt;log level>"); 
            System.out.println( 
                "EG:\njava uk.org.primrose.test.TestPrimroseStandalone /usr/primrose/conf/primrose.conf admin admin 8090 /var/log/primrose_console.log verbose,info,warn,error,crisis"); 
 
            System.exit(0); 
        } 
 
        // Start the web console 
        PrimroseWebConsoleLoader.load(args[1], args[2], 
            Integer.parseInt(args[3]), args[4], args[5]); 
 
        // Load the pools 
        List<String> loadedPoolNames = PrimroseLoader.load(args[0], true); 
 
        // Get a pool name from the first loaded pool 
        // to run our test with 
        String poolName = loadedPoolNames.get(0); 
 
        new TestPrimroseStandalone().runTest(poolName); 
 
        // Thread.sleep(10000); 
 
        // System.exit(0); 
    } 
 
    public void runTest(String poolName) throws Throwable { 
        int i = 0; 
 
        while (i++ < 20) { 
            new RunThread(poolName).run(); 
        } 
 
        /* 
         * int connectionsToGet = 30; int cycles = 10; int [] sleeps = new int[5]; 
         * sleeps[0] = 100; sleeps[1] = 200; sleeps[2] = 50; sleeps[3] = 10000; 
         * sleeps[4] = 10; for (int k = 0; k  cycles; k++) { for (int i = 0; i  
         * sleeps.length; i++) { int j = 0; while (j++  connectionsToGet) { new 
         * RunThread(poolName).start(); } Thread.sleep(sleeps[i]); 
         * System.out.println("Sleep : " +sleeps[i]); } } 
         */ 
    } 
 
    /** 
     * Perform a test on the newly loaded pool 
     */ 
    class RunThread extends Thread { 
        String poolName = null; 
 
        public RunThread(String poolName) { 
            this.poolName = poolName; 
        } 
 
        @Override 
        public void run() { 
            try { 
                // System.out.println("start ..."); 
                Context ctx = new InitialContext(); 
                DataSource ds = (DataSource) ctx.lookup("java:comp/env/" + 
                        poolName); 
                long now = System.currentTimeMillis(); 
                Connection c = ds.getConnection(); 
                System.out.println("got connection " + c + " in " + 
                    (System.currentTimeMillis() - now) + "ms from pool " + 
                    poolName); 
 
                Statement s = c.createStatement(); 
                ResultSet rs = s.executeQuery("select 1 from test"); 
 
                // Thread.sleep(100); 
                while (rs.next()) { 
                    rs.getString(1); 
                } 
 
                rs.close(); 
                s.close(); 
 
                // c.close();; 
            } catch (Exception e) { 
                e.printStackTrace(); 
            } 
        } 
    } 
} 
