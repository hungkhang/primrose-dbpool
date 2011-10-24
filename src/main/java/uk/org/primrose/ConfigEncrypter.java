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
 
import java.io.BufferedReader; 
import java.io.ByteArrayInputStream; 
import java.io.ByteArrayOutputStream; 
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.io.FileReader; 
import java.io.InputStreamReader; 
import java.io.ObjectInputStream; 
import java.io.ObjectOutputStream; 
import java.io.PrintWriter; 
import java.security.Key; 
import java.security.SecureRandom; 
import java.util.List; 
import java.util.StringTokenizer; 
 
import javax.crypto.Cipher; 
import javax.crypto.KeyGenerator; 
 
import sun.misc.BASE64Decoder; 
import sun.misc.BASE64Encoder; 
import uk.org.primrose.vendor.standalone.PrimroseLoader; 
 
 
public class ConfigEncrypter { 
    static String keyfile = null; 
    static BufferedReader br = new BufferedReader(new InputStreamReader( 
                System.in)); 
 
    public static void main(String[] args) throws Exception { 
    	List<String> loadedPoolNames = PrimroseLoader.load("primrose3.config", true); 
    	 
    	System.exit(0); 
    	 
    	boolean runStartMenu = true; 
    	if (args.length > 0) { 
    		if (args[0].equalsIgnoreCase("makeKey")) { 
    			makeKey(args[1]); 
    			runStartMenu = false; 
    		} else if (args[0].equalsIgnoreCase("encryptConfig")) {  
    			encrpytConfig(args[1], args[2]); 
    			runStartMenu = false; 
    		} 
    	} 
        if (runStartMenu) start(); 
    } 
 
    public static void start() throws Exception { 
        printMenu(); 
 
        String line = br.readLine(); 
 
        if (line.trim().equals("q")) { 
            br.close(); 
            System.exit(0); 
        } 
 
        while (!(line.trim().equals("1") || line.trim().equals("2"))) { 
            printMenu(); 
            line = br.readLine(); 
 
            if (line.trim().equals("q")) { 
                br.close(); 
                System.exit(0); 
            } 
        } 
 
        if (line.trim().equals("1")) { 
            makeKey(); 
        } else if (line.trim().equals("2")) { 
            encrpytConfig(); 
        } 
    } 
     
    public static void makeKey(String keyFile) throws Exception { 
 
        // Security.addProvider( new com.sun.crypto.provider.SunJCE() ); 
        KeyGenerator generator = KeyGenerator.getInstance("DES", "SunJCE"); 
 
        // generate a new random key 
        generator.init(56, new SecureRandom()); 
 
        Key key = generator.generateKey(); 
 
        ByteArrayOutputStream keyStore = new ByteArrayOutputStream(); 
        ObjectOutputStream keyObjectStream = new ObjectOutputStream(keyStore); 
        keyObjectStream.writeObject(key); 
 
        byte[] keyBytes = keyStore.toByteArray(); 
 
        FileOutputStream fos = new FileOutputStream(keyFile); 
        fos.write(keyBytes); 
        fos.flush(); 
        fos.close(); 
 
        System.out.print("\nSuccessfully created encryption key : " + keyFile); 
 
    	 
    } 
 
    public static void makeKey() throws Exception { 
        System.out.print( 
            "\nEnter the full directory path (eg /usr/local/keys) \nto where you would like the encryption key to be stored :: "); 
 
        String line = br.readLine(); 
 
        while (!(new File(line).exists() && new File(line).isDirectory())) { 
            System.out.println("\nFile does not exist ..."); 
            System.out.print( 
                "Enter the full directory path (eg /usr/local/keys) \nto where you would like the encryption key to be stored :: "); 
            line = br.readLine(); 
        } 
 
        String dir = line; 
 
        System.out.print( 
            "\nEnter the filename (eg mykey.key) of the encryption key to be stored :: "); 
 
        String filename = dir + File.separator + br.readLine(); 
        keyfile = filename; 
 
        // Security.addProvider( new com.sun.crypto.provider.SunJCE() ); 
        KeyGenerator generator = KeyGenerator.getInstance("DES", "SunJCE"); 
 
        // generate a new random key 
        generator.init(56, new SecureRandom()); 
 
        Key key = generator.generateKey(); 
 
        ByteArrayOutputStream keyStore = new ByteArrayOutputStream(); 
        ObjectOutputStream keyObjectStream = new ObjectOutputStream(keyStore); 
        keyObjectStream.writeObject(key); 
 
        byte[] keyBytes = keyStore.toByteArray(); 
 
        FileOutputStream fos = new FileOutputStream(filename); 
        fos.write(keyBytes); 
        fos.flush(); 
        fos.close(); 
 
        System.out.print("\nSuccessfully created encryption key : " + filename); 
 
        start(); 
    } 
     
    public static void encrpytConfig(String configFile, String keyFile) throws Exception { 
    	encryptFile(configFile, keyFile); 
    } 
 
    public static void encrpytConfig() throws Exception { 
        String bla = "/usr/local/keys/mykey.key"; 
 
        if (keyfile != null) { 
            bla = keyfile; 
        } 
 
        System.out.print("\nEnter the full path and filename (eg " + bla + 
            ") \nto where the encryption key is stored :: "); 
 
        String line = br.readLine(); 
 
        while (!new File(line).exists()) { 
            System.out.println("\nFile does not exist ..."); 
            System.out.print("\nEnter the full path and filename (eg " + bla + 
                ") \nto where the encryption key is stored :: "); 
            line = br.readLine(); 
        } 
 
        keyfile = line; 
 
        System.out.print( 
            "\nEnter the full path and filename (eg /usr/tomcat/conf/primrose.config) \nto where the primrose config is  :: "); 
        line = br.readLine(); 
 
        while (!new File(line).exists()) { 
            System.out.println("\nFile does not exist ..."); 
            System.out.print( 
                "\nEnter the full path and filename (eg /usr/tomcat/conf/primrose.config) \nto where the primrose config is :: "); 
            line = br.readLine(); 
        } 
 
        encryptFile(line, keyfile); 
    } 
 
    public static void encryptFile(String file, String keyFile) 
        throws Exception { 
        System.out.println("Encrypting passwords in file : '" + file + "'"); 
 
        BufferedReader br = new BufferedReader(new FileReader(file)); 
        PrintWriter pw = new PrintWriter(new FileOutputStream(file + ".tmp")); 
        String line = ""; 
        String poolName = "admin tool"; 
 
        while ((line = br.readLine()) != null) { 
            if (!line.trim().startsWith("#") && !line.trim().equals("")) { 
                StringTokenizer st = new StringTokenizer(line, "="); 
                st.countTokens(); 
 
                String key = st.nextToken(); 
                String value = ""; 
 
                while (st.hasMoreTokens()) { 
                    value += (st.nextToken() + "="); 
                } 
 
                if (value.length() != 0) { 
                    value = value.substring(0, value.length() - 1); 
                } 
 
                if (key.equals("poolName")) { 
                    poolName = value; 
                } 
 
                if (key.equals("password")) { 
                    if ((value == null) || value.equals("")) { 
                        pw.println(line); 
                    } else { 
                        System.out.println("\n---- " + poolName + " ----"); 
                        pw.println(key + "=" + 
                            getEncryptedString(value, keyFile)); 
                        System.out.println("Encrypting from '" + value + 
                            "' to '" + getEncryptedString(value, keyFile) + 
                            "'"); 
                    } 
                } else { 
                    pw.println(line); 
                } 
            } else { 
                pw.println(line); 
            } 
        } 
 
        br.close(); 
        pw.flush(); 
        pw.close(); 
 
        File f = new File(file); 
        f.delete(); 
        f = new File(file + ".tmp"); 
        f.renameTo(new File(file)); 
        new File(file + ".tmp").delete(); 
 
        System.out.println("\nDone !"); 
    } 
 
    public static String getEncryptedString(String input, String keyFile) 
        throws Exception { 
        // Security.addProvider( new com.sun.crypto.provider.SunJCE() ); 
        Key key = null; 
 
        File f = new File(keyFile); 
        FileInputStream fis = new FileInputStream(f); 
        byte[] keyBytes = new byte[(int) f.length()]; 
        fis.read(keyBytes); 
        fis.close(); 
 
        ByteArrayInputStream keyArrayStream = new ByteArrayInputStream(keyBytes); 
        ObjectInputStream keyObjectStream = new ObjectInputStream(keyArrayStream); 
        key = (Key) keyObjectStream.readObject(); 
 
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); 
        cipher.init(Cipher.ENCRYPT_MODE, key); 
 
        byte[] inputBytes = input.getBytes(); 
 
        byte[] outputBytes = cipher.doFinal(inputBytes); 
 
        BASE64Encoder encoder = new BASE64Encoder(); 
        String base64 = encoder.encode(outputBytes); 
 
        return base64; 
    } 
 
    public static String getDecryptedString(String base64Input, String keyFile) 
        throws Exception { 
        BASE64Decoder encoder = new BASE64Decoder(); 
        byte[] inputBytes = encoder.decodeBuffer(base64Input); 
 
        // Security.addProvider( new com.sun.crypto.provider.SunJCE() ); 
        Key key = null; 
 
        File f = new File(keyFile); 
        FileInputStream fis = new FileInputStream(f); 
        byte[] keyBytes = new byte[(int) f.length()]; 
        fis.read(keyBytes); 
        fis.close(); 
 
        ByteArrayInputStream keyArrayStream = new ByteArrayInputStream(keyBytes); 
        ObjectInputStream keyObjectStream = new ObjectInputStream(keyArrayStream); 
        key = (Key) keyObjectStream.readObject(); 
 
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); 
        cipher.init(Cipher.DECRYPT_MODE, key); 
 
        byte[] outputBytes = cipher.doFinal(inputBytes); 
 
        return new String(outputBytes); 
    } 
 
    public static void printMenu() { 
        System.out.println("\n\n\n\n\n------- MENU -------"); 
        System.out.println("1) Create an encryption key for your pools."); 
        System.out.println("2) Encrypt your primrose config files."); 
        System.out.println("\nAlternatively you can run the program non - interactively :"); 
        System.out.println("\n\tjava uk.org.primrose.ConfigEncrypter makeKey <key filename>\n\tjava uk.org.primrose.ConfigEncrypter encrpytConfig <primrose config filename> <key filename>"); 
        System.out.print( 
            "\nChoose an option (enter '1' or '2' or 'q' to quit) :: "); 
    } 
} 
