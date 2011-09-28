package uk.org.primrose; 
 
import java.lang.Exception; 
 
public class SyslogException extends Exception 
	{ 
	SyslogException() 
		{ 
		super(); 
		} 
	 
	SyslogException( String msg ) 
		{ 
		super( msg ); 
		} 
	} 
