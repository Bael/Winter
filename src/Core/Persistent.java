package core;
import java.util.Date;
public class Persistent {
	
    public String GetStorageGlobalName()
    {
    	String name = this.getClass().getSimpleName();
    	name =  name.concat("D");
    	return name;
    }
 
    public String GetIndexGlobalName()
    {
    	return this.getClass().getSimpleName().concat("I");
    }
    
    
    public long Id = 0;
    
    public String Name ;
   
    public Date CreatedOn;
    
    public String toString()
    {
        return "Name:"+this.Name+ ", Id:"+this.Id+", CreatedOn:"+CreatedOn;
    }
    
    public void Save()
    {
    	try {
			DataWorker.Instance().SaveObject(this);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    public Persistent LoadData()
    {
    	Persistent p = null;
    	try {
			p = DataWorker.Instance().LoadObjectById(this.Id, this);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return p;
    }
  
}
