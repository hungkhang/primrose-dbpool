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
 
import java.io.*; 
 
 
public class GeneratePoolCode { 
    String configFileName; 
    String outFileName; 
    FileOutputStream outFile; 
    boolean genInterface; 
 
    public GeneratePoolCode(String configFileName, String outFileName, 
        String classOrInterface) throws IOException { 
        this.configFileName = configFileName; 
        this.outFileName = outFileName; 
 
        if (classOrInterface.equals("class")) { 
            genInterface = false; 
        } else { 
            genInterface = true; 
        } 
 
        outFile = new FileOutputStream(outFileName); 
    } 
 
    public static void main(String[] args) throws Exception { 
        if (args.length != 3) { 
            System.out.println( 
                "java GeneratePoolCode <config file> <out file> <class|interface>"); 
        } else { 
            GeneratePoolCode gpc = new GeneratePoolCode(args[0], args[1], 
                    args[2]); 
            gpc.doit(); 
        } 
    } 
 
    public void doit() throws IOException { 
        BufferedReader br = new BufferedReader(new InputStreamReader( 
                    new FileInputStream(configFileName))); 
        String line; 
 
        if (!genInterface) { 
            while ((line = br.readLine()) != null) { 
                String[] parts = line.split("="); 
                String name = parts[0]; 
 
                generateClassVars(name); 
            } 
        } 
 
        br.close(); 
 
        br = new BufferedReader(new InputStreamReader( 
                    new FileInputStream(configFileName))); 
 
        while ((line = br.readLine()) != null) { 
            String[] parts = line.split("="); 
            String name = parts[0]; 
 
            generateGetSet(name); 
        } 
 
        br.close(); 
 
        outFile.close(); 
    } 
 
    public void generateClassVars(String name) throws IOException { 
        outFile.write(("\t// The '" + name + "' pool config property\n").getBytes()); 
        outFile.write(("\tprotected String " + name + ";\n\n").getBytes()); 
 
        outFile.flush(); 
    } 
 
    public void generateGetSet(String name) throws IOException { 
        String camelName = (name.charAt(0) + "").toUpperCase() + 
            name.substring(1, name.length()); 
 
        outFile.write(("\t/**\n").getBytes()); 
        outFile.write(("\t*\tGet the '" + name + "' pool config property\n").getBytes()); 
        outFile.write(("\t*/\n").getBytes()); 
 
        if (!genInterface) { 
            outFile.write(("\tpublic String get" + camelName + "() {\n").getBytes()); 
            outFile.write(("\t\treturn " + name + ";\n").getBytes()); 
            outFile.write(("\t}\n\n").getBytes()); 
        } else { 
            outFile.write(("\tpublic String get" + camelName + "();\n\n").getBytes()); 
        } 
 
        outFile.write(("\t/**\n").getBytes()); 
        outFile.write(("\t*\tSet the '" + name + "' pool config property\n").getBytes()); 
        outFile.write(("\t*/\n").getBytes()); 
 
        if (!genInterface) { 
            outFile.write(("\tpublic void set" + camelName + "(String " + name + 
                ") {\n").getBytes()); 
            outFile.write(("\t\tthis." + name + " = " + name + ";\n").getBytes()); 
            outFile.write(("\t}\n\n\n").getBytes()); 
        } else { 
            outFile.write(("\tpublic void set" + camelName + "(String " + name + 
                ");\n\n\n").getBytes()); 
        } 
 
        outFile.flush(); 
    } 
} 
