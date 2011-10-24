package uk.org.primrose.vendor.jboss; 
 
import java.io.IOException; 
 
 
public interface PrimroseWebConsoleBindingMBean { 
    public void start() throws IOException; 
 
    public void stop() throws IOException; 
 
    public void setPort(String port) throws IOException; 
 
    public String getPort(); 
 
    public void setLogFile(String logFile) throws IOException; 
 
    public String getLogFile(); 
 
    public void setLogLevel(String logLevel); 
 
    public String getLogLevel(); 
 
    public void setUsername(String username); 
 
    public void setPassword(String password); 
 
    public String getUsername(); 
 
    public String getPassword(); 
} 
