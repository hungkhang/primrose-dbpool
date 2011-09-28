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
 
import uk.org.primrose.Logger; 
 
import java.net.*; 
 
import java.util.*; 
 
 
public class HttpRequest { 
    protected String szMethod; 
    protected String szResource; 
    protected String szResponseMimeType; 
    protected String szVersion; 
    HashMap<String, String> headers = new HashMap<String, String>(); 
    HashMap<String, String> parameters = new HashMap<String, String>(); 
    Logger logger = null; 
 
    public HttpRequest(Logger logger) { 
        this.logger = logger; 
    } 
 
    /** 
    *        Get the 'szMethod ' 
    */ 
    public String getMethod() { 
        return szMethod; 
    } 
 
    /** 
    *        Set the 'szMethod ' 
    */ 
    public void setMethod(String szMethod) { 
        this.szMethod = szMethod; 
    } 
 
    /** 
    *        Get the 'szResource ' 
    */ 
    public String getResource() { 
        return szResource; 
    } 
 
    /** 
    *        Set the 'szResource ' 
    */ 
    public void setResource(String szResource) { 
        this.szResource = szResource; 
    } 
 
    /** 
    *        Get the 'szResponseMimeType ' 
    */ 
    public String getResponseMimeType() { 
        return szResponseMimeType; 
    } 
 
    /** 
    *        Set the 'szResponseMimeType ' 
    */ 
    public void setResponseMimeType(String szResponseMimeType) { 
        this.szResponseMimeType = szResponseMimeType; 
    } 
 
    /** 
    *        Get the 'szVersion ' 
    */ 
    public String getVersion() { 
        return szVersion; 
    } 
 
    /** 
    *        Set the 'szVersion ' 
    */ 
    public void setVersion(String szVersion) { 
        this.szVersion = szVersion; 
    } 
 
    /** 
    *        Get the 'headers ' 
    */ 
    public HashMap<String, String> getHeaders() { 
        return headers; 
    } 
 
    /** 
    *        Get the 'parameters ' 
    */ 
    public HashMap<String, String> getParameters() { 
        return parameters; 
    } 
 
    public void printDetails() { 
        logger.info("szMethod=" + szMethod); 
        logger.info("szResource=" + szResource); 
        logger.info("szResponseMimeType=" + szResponseMimeType); 
        logger.info("szVersion=" + szVersion); 
 
        logger.info("\n\nHeaders : "); 
 
        Iterator<String> headersIter = headers.keySet().iterator(); 
 
        while (headersIter.hasNext()) { 
            String key = headersIter.next(); 
            logger.info(key + "=" + headers.get(key)); 
        } 
 
        logger.info("\n\nParameters : "); 
 
        Iterator<String> parametersIter = parameters.keySet().iterator(); 
 
        while (parametersIter.hasNext()) { 
            String key = parametersIter.next(); 
            logger.info(key + "=" + parameters.get(key)); 
        } 
    } 
 
    @SuppressWarnings("deprecation") 
    boolean parseRequest(String szRequestData) { 
        if ((szRequestData != null) && (szRequestData.length() == 0)) { 
            return false; 
        } 
 
        // Create a tokenizer from the request 
        String[] t = szRequestData.split("\n"); 
        String szTok; 
        boolean bFirstLine = true; 
 
        // Loop the request, extacting the necessary data 
        for (int i = 0; i < t.length; i++) { 
            szTok = t[i]; 
 
            //CReportError::Info("LINE : %s", szTok); 
            // first line : 
            // extract the method, resource and http version data 
            if (bFirstLine) { 
                bFirstLine = false; 
 
                int j = 0; 
                String[] t2 = szTok.split(" "); 
 
                // populate the HTTP method 
                String szTok2 = t2[j++]; 
 
                if (szTok2 != null) { 
                    szMethod = szTok2; 
                } 
 
                // populate the resource requested 
                szTok2 = t2[j++]; 
 
                if (szTok2 != null) { 
                    szResource = szTok2; 
 
                    // set the MIME type too 
                    if (szResource.indexOf(".htm") != -1) { 
                        szResponseMimeType = ("text/html"); 
                    } else if (szResource.indexOf(".svg") != -1) { 
                        szResponseMimeType = ("image/svg+xml"); 
                    } else if (szResource.indexOf(".png") != -1) { 
                        szResponseMimeType = ("image/png"); 
                    } else if (szResource.indexOf(".gif") != -1) { 
                        szResponseMimeType = ("image/gif"); 
                    } else if ((szResource.indexOf(".jpeg") != -1) || 
                            (szResource.indexOf(".jpg") != -1)) { 
                        szResponseMimeType = ("image/jpeg"); 
                    } else if (szResource.indexOf(".txt") != -1) { 
                        szResponseMimeType = ("text/plain"); 
                    } else if (szResource.indexOf(".log") != -1) { 
                        szResponseMimeType = ("text/plain"); 
                    } else { 
                        szResponseMimeType = ("text/html"); 
                    } 
 
                    // populate the request attributes for a GET 
                    if (szMethod.indexOf("GET") != -1) { 
                        String[] t3 = szResource.split("\\?"); 
 
                        if (t3.length > 1) { 
                            String szToken = t3[1]; 
                            String[] t4 = szToken.split("&"); 
 
                            // breaking each pair up 
                            String szToken2; 
 
                            for (int l = 0; l < t4.length; l++) { 
                                szToken2 = t4[l]; 
 
                                // and finally breaking the pairs into their 
                                // names and values 
                                String[] t5 = szToken2.split("="); 
 
                                if (t5.length == 2) { 
                                    parameters.put(URLDecoder.decode( 
                                            t5[0].trim()), 
                                        URLDecoder.decode(t5[1].trim())); 
                                } 
                            } 
                        } 
                    } 
                } 
 
                // populate the HTTP version 
                szTok2 = t2[j++]; 
 
                if (szTok2 != null) { 
                    szVersion = szTok2; 
                } 
 
                continue; 
            } 
 
            // non-first line : 
            // extract the header details 
            String[] t2 = szTok.split(":"); 
            String szHeaderName = t2[0]; 
            String szHeaderValue = t2[1]; 
 
            if ((szHeaderName == null) || (szHeaderValue == null)) { 
                continue; 
            } 
 
            headers.put(szHeaderName.trim(), szHeaderValue.trim()); 
        } 
 
        return true; 
    } 
} 
