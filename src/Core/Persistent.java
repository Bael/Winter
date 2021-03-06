package Core;
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
    	return DataWorker.GetIndexGlobalName(this.getClass());
    }
    
    
    public long Id = 0;
    
    public String Name = "";
   
    public Date CreatedOn = new Date();
    
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
    
    
    public Persistent OpenId(Long id)
    {
    	this.Id = id;
    	return this.LoadData();
    	
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
