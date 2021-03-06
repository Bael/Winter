package Core;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;

import com.intersys.globals.NodeReference;

public class FieldSetter {

    public static void SetFieldValue(Persistent obj, Field field, NodeReference node)
    {
    	String fieldName = field.getName();
    	
    	try 
    	{
			if (Persistent.class.isInstance(field.get(obj)))
	    	{
	    		Persistent fieldAsPersistentObject = (Persistent) field.get(obj);
	    		if (fieldAsPersistentObject != null)
	    		{
    				fieldAsPersistentObject.Save();
					node.set(fieldAsPersistentObject.Id, obj.Id, fieldName);
					return;
	    		}
	    		
	    	}
			
			Long Id = obj.Id;
			Object fieldValue = field.get(obj);
			 if (fieldValue instanceof java.lang.String)
			 {
			     node.set(fieldValue.toString(), Id, fieldName);
			 }
			 else if (fieldValue instanceof java.lang.Long)
		    {
		         long longValue = field.getLong(obj);
		         node.set(longValue, Id, fieldName);
		     }
		     else if (fieldValue instanceof java.util.Date)
	         {
	             Date dateValue = (Date) fieldValue;
	             String strValue = dateValue.toGMTString();
	             node.set(strValue, obj.Id, fieldName);
	         }
		}
		catch (IllegalArgumentException | IllegalAccessException e) 
    	{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
     	
    }
    
   
}
