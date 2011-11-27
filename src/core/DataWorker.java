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
public class DataWorker {
	/*
	 * To change this template, choose Tools | Templates
	 * and open the template in the editor.
	 */
	    private HashMap<String, Field> fieldsMap;
	    private String SchemaClassName;
	    
	    private static DataWorker dataWorker;
	    public static DataWorker Instance()
	    {
	        if (dataWorker == null)
	        {
	            dataWorker = new DataWorker();
	        }
	        return dataWorker;
	    }
	    
	    public void UpdateIndicesOnCreate(Persistent obj)
	    {
	    	
	    }
	    
	    public void UpdateIndicesOnUpdate(Persistent obj)
	    {
	    	
	    }
	    
	    public void UpdateIndicesOnDelete(Persistent obj)
	    {
	    	
	    }
	    
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
	            //System.out.print("\"" + subscr + "\"=" + /*(node.getLong()) + */ " ");
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
	            System.out.print("\"" + subscr + "\"=" + /*(node.getLong()) + */ " ");
	            subscr = node.nextSubscript();
	            node.setSubscript(node.getSubscriptCount(), subscr);
	            
	        };

	   
	    }
	    
	    public Persistent SaveObject(Persistent obj) throws IllegalAccessException
	    {
	        
	        String globalName = obj.GetStorageGlobalName(); //obj.STORAGEGLOBALNAME;
	        NodeReference node = null;
	        try
	        {
	        //ConnectionManager.writeToFile("", "step0");
	            Connection conn = ConnectionManager.Instance().getConnection();
	        //ConnectionManager.writeToFile("", "step2");
	            node = conn.createNodeReference(globalName);
	        }
	        catch (Exception ex)
	        {
	            ConnectionManager.writeToFile("", ex.toString());
	        }
	        
	        node.increment(1);
	        
	        obj.Id = node.getLong();
	        Class info = obj.getClass();
	        Field[] fields = info.getFields();
	        Field field;
	        
	        for (int i = 0; i < fields.length; i++)
	        { 
	            field = fields[i];
	            
	            Object value = field.get(obj);
	            if (value instanceof java.lang.String)
	            {
	                node.set(value.toString(), obj.Id, fields[i].getName());
	            }
	            else
	            {
	                if (value instanceof  java.lang.Long)
	                {
	                    long longValue = field.getLong(obj);
	                            
	                    node.set(longValue, obj.Id, fields[i].getName());
	                }
	                else  
	                {
	                    if (value instanceof java.util.Date)
	                    {
	                        Date dateValue = (Date)  value;
	                                //() new Date(value.toString());
	                        String strValue = dateValue.toGMTString();
	                        node.set(strValue, obj.Id, fields[i].getName());
	                        
	                    }
	                            
	                    
	                }
	                        
	            }
	                    
	                    
	      
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
	      
	         
	        NodeReference node = ConnectionManager.Instance().getConnection().createNodeReference(obj.GetStorageGlobalName());
	        
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
	         } while (subscript.length() > 0);
	         
	       
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

	        
	        ///System.out.println("Step7");
	        ///System.out.println("Access all the fields");
	        for (int i = 0; i < fields.length; i++)
	        { 
	            fieldsMap.put(fields[i].getName(), fields[i]);
	        }
	    }
	    

	    
	

}
