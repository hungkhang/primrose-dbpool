package uk.org.primrose.pool.core; 
 
public class KeyValuePair { 
    private String line; 
    private String key; 
    private String value; 
 
    public KeyValuePair(String line) { 
        this.line = line; 
    } 
 
    public String getKey() { 
        return key; 
    } 
 
    public String getValue() { 
        return value; 
    } 
 
    public void splitLine() { 
        String[] parts = line.split("="); 
        key = parts[0]; 
        value = ""; 
 
        if (parts.length > 2) { 
            // handle cases where line is like "name=value&soemthing=value2" - like 
            // on driver URLs 
            // or checkSQL statements 
            for (int i = 1; i < parts.length; i++) { 
                if (i == (parts.length - 1)) { 
                    value += parts[i]; 
                } else { 
                    value += (parts[i] + "="); 
                } 
            } 
        } else { 
            if (parts.length > 1) { 
                value = parts[1]; 
            } 
        } 
 
        if (line.endsWith("=") && value.length() > 0) { 
            value += "="; 
        } 
 
        if (key != null) { 
            key = key.trim(); 
        } 
 
        if (value != null) { 
            value = value.trim(); 
        } 
    } 
} 
