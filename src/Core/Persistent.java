package core;
import java.util.Date;
public class Persistent {
	
    public String GetStorageGlobalName()
    {
    	String name = this.getClass().toString();
    	name =  name.concat("D");
    	return name;
    }
 
    public String GetIndexGlobalName()
    {
    	return this.getClass().toString().concat("I");
    }
    
    public static long GetNextId()
    {
        return 1;
    }
    
    public long Id;
    
    public String Name ;
   
    public Date CreatedOn;
    
    public String toString()
    {
        return "Name:"+this.Name+ ", Id:"+this.Id+", CreatedOn:"+CreatedOn;
    }
  
}
