package core;
import core.ConnectionManager;
import core.Persistent;
import com.intersys.globals.Connection;
import java.lang.reflect.Field; 
import java.lang.reflect.Method; 
import com.intersys.globals.NodeReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import support.LogWriter;
public class DataWorker {
    private HashMap<String, Field> fieldsMap;
    private String SchemaClassName;
    
    private static DataWorker dataWorker;
    private LogWriter log;
    private Connection connection;
    public static DataWorker Instance()
    {
        if (dataWorker == null)
        {
            dataWorker = Init();
        }
        return dataWorker;
    }
    
    
    public static DataWorker Init()
    {
    	DataWorker worker = new DataWorker();
    	worker.log = new LogWriter();
    	worker.log.FileName = "c:\\temp\\javaLog.txt";
    	
    	worker.connection = ConnectionManager.Instance().getConnection();
         
    	return worker;
    }
    public void UpdateIndicesOnCreate(Persistent obj)
    {
    	
    }
    
    public void UpdateIndicesOnUpdate(Persistent oldObj, Persistent obj)
    {
    	
    }
    
    public void UpdateIndicesOnDelete(Persistent obj)
    {
    	
    }
    /*
 
    public ArrayList GetAllByInstance(Persistent _obj)
    {
        ArrayList list = new ArrayList();
        NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(_obj.GetStorageGlobalName());
        node.appendSubscript("");
        String subscr = "";
        subscr = node.nextSubscript();   
         while (!subscr.equals("")) 
         {
            long id = new Long(subscr); 
                     
            Persistent obj = new Persistent();
            try {
                obj = _obj.getClass().newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(DataWorker.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DataWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                System.out.println(id+ " . " + _obj.GetStorageGlobalName());
                
                obj = LoadObjectById(id, obj);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DataWorker.class.getName()).log(Level.SEVERE, null, ex);
            }
            list.add(obj);
            subscr = node.nextSubscript();
            node.setSubscript(node.getSubscriptCount(), subscr);
            
          }

   
        return list;
        
    }
    
    public void Iterate(Persistent obj)
    {
        String globalName = obj.GetStorageGlobalName(); //obj.STORAGEGLOBALNAME;
        NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(globalName);
        node.appendSubscript("");
        String subscr = "";
        subscr = node.nextSubscript();   
        while (!subscr.equals("")) 
        {
            System.out.print("\"" + subscr + "\"=" +  " ");
            subscr = node.nextSubscript();
            node.setSubscript(node.getSubscriptCount(), subscr);
            
        };

   
    }
    */
    
    public void SetNodeSubscriptField(Field field, Persistent obj, NodeReference node)
    {
    	Long Id = obj.Id;
    	String fieldName = field.getName();
    	try {
			Object fieldValue = field.get(obj);
			 if (fieldValue instanceof java.lang.String)
			 {
			     node.set(fieldValue.toString(), Id, fieldName);
			 }
			 else
			 {
			     if (fieldValue instanceof java.lang.Long)
			     {
			         long longValue = field.getLong(obj);
			         node.set(longValue, Id, fieldName);
			     }
			     else  
			     {
			         if (fieldValue instanceof java.util.Date)
			         {
			             Date dateValue = (Date)  fieldValue;
			             String strValue = dateValue.toGMTString();
			             node.set(strValue, obj.Id, fieldName);
			         }
			     }
			 }
		} 
    	catch (IllegalArgumentException | IllegalAccessException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.WriteToFile(e.getMessage(), true);
		}

    
    }
    
    public Persistent SaveObject(Persistent obj) throws IllegalAccessException
    {
        NodeReference node = null;
        try
        {
            String globalName = obj.GetStorageGlobalName();
            node = connection.createNodeReference(globalName);
        }
        catch (Exception ex)
        {
            log.WriteToFile(ex.toString(), true);
            throw ex;
        }
        
        Persistent oldObj = null;
        if (obj.Id == 0)
        {
        	node.increment(1);
            
        	obj.Id = node.getLong();
        }
        else
        {
        	oldObj = LoadObjectById(obj.Id, obj);
        	//node.setSubscriptCount(0);
        	//znode .setSubscriptCount((int)obj.Id);
            //node.appendSubscript(obj.Id);
        }
        
        Class info = obj.getClass();
        Field[] fields = info.getFields();
        Field field;
        
        for (int i = 0; i < fields.length; i++)
        { 
            field = fields[i];
            SetNodeSubscriptField(field, obj, node);
        }
        if (oldObj == null)
        {
        	UpdateIndicesOnCreate(obj);
        }
        else
        {
        	UpdateIndicesOnUpdate(oldObj, obj);
        }
        return obj;
    }
    
    public void DeleteObjectById(Long id, String globalName)
    {
        NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(globalName);
        node.kill(id);
    }
    
    public Persistent LoadObjectById(Long id, Persistent obj) throws IllegalAccessException
    {
        NodeReference node = connection.createNodeReference(obj.GetStorageGlobalName());
        
        InitSchema(obj);
        
        node.setSubscriptCount(0);
        node.appendSubscript(id);
        Field field;
        String subscript = "";
        Object nodeValue = null;
        do {
            subscript = node.nextSubscript(subscript);
            if (subscript.length() > 0) 
            {
               field = fieldsMap.get(subscript);
               nodeValue = node.getObject(subscript);
               if (nodeValue instanceof java.lang.Long)
               {
                   Long nodeLongValue = node.getLong(subscript);
                   field.setLong(obj, nodeLongValue);
               }
               else
               {
                   if(field.getType() == java.util.Date.class)
                   {
                       Date dateValue = new Date(nodeValue.toString());
                        
                        field.set(obj, dateValue);
                   }
                   else
                    {
                   
                        field.set(obj, nodeValue);
                    }
               }
            }
         }while (subscript.length() > 0);
         
       
        return obj;
      
        
    }
    private void InitSchema(Object obj)
    {
        Class classInfo = obj.getClass();
        
        if (fieldsMap == null || SchemaClassName != classInfo.getName())
        {
            fieldsMap = new HashMap<String, Field>();
        }
        SchemaClassName = classInfo.getName();
        Field[] fields = classInfo.getFields();

        for (int i = 0; i < fields.length; i++)
        { 
            fieldsMap.put(fields[i].getName(), fields[i]);
        }
    }
	    

	    
	

}
