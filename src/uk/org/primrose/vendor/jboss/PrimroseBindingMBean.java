package uk.org.primrose.vendor.jboss; 
 
public interface PrimroseBindingMBean { 
    public void start() throws Exception; 
 
    public void stop() throws Exception; 
 
    public void setPrimroseConfigFile(String primroseConfigFile); 
 
    public String getPrimroseConfigFile(); 
} 
