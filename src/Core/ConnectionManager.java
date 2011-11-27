package Core;
	import java.io.*;
	import com.intersys.globals.*;
	import java.io.PrintWriter;
	import java.util.logging.Level;
	import java.util.logging.Logger;
	public class ConnectionManager {
	
	  	private static ConnectionManager manager;
	    public static ConnectionManager Instance()
	    {
	        if (ConnectionManager.manager == null)
	        {
	            ConnectionManager.manager = new ConnectionManager();            
	        }
	        
	        return ConnectionManager.manager;
	    }
	    
	    private Connection _connection;

	    /**
	     * @return the _connection
	     */
	    
	    
	    public static String writeToFile(String filename, String str)
	    {
	        PrintWriter writer = null;
	        try {
	            writer = new PrintWriter(
	             new OutputStreamWriter(
	             new FileOutputStream("c:\\temp\\javaLog.txt"), "windows-1251"));
	            writer.write(str);
	            writer.close();
	            }
	        catch (Exception ex) {} 
	        return str;
	    }


	    public Connection getConnection() 
	    {
	        if (_connection == null)
	        {
	            try
	            {
	                System.out.println("Step1");
	                _connection =  ConnectionContext.getConnection(); // ConnectionContext.getConnection();
	                System.out.println("Step2");
	                
	                if (!_connection.isConnected())
	                {
	                    System.out.println("Step2_1");
	                    
	                    _connection.connect("USER","_SYSTEM","DATA");
	                }
	                System.out.println("Step3");
	            }
	            catch (GlobalsException ex)
	            {
	               writeToFile("", ex.getMessage());
	                System.out.println(ex.toString());
	                throw ex;
	            }
	            catch (Exception ex)
	            {
	                writeToFile("", ex.getMessage());
	            }
	        }
	        return _connection;
	    }
	    
	    public void CloseConnection()
	    {
	        getConnection().close();
	    }

}
