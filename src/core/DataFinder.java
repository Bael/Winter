package Core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.intersys.globals.NodeReference;

public class DataFinder {
	
	Class searchClass = null;
	
	String indexGlobal = "";
	
	ArrayList<Long> ids = null;
	
	int currentPosition = 0;
	
	HashMap<String, String> conditions = new HashMap<>();
	
	public DataFinder(Class searchClass) throws IllegalArgumentException {
		if (searchClass.getGenericSuperclass() != Persistent.class)
			throw new IllegalArgumentException();
		this.searchClass = searchClass;
		
		this.indexGlobal = DataWorker.GetIndexGlobalName(searchClass);
	}
	
	public Persistent Next() throws InstantiationException, IllegalAccessException
	{
		if (ids == null)
			initSearch();
		
		if (currentPosition >= ids.size())
			return null;
		
		Persistent obj = (Persistent) (searchClass.newInstance());
		obj.Id = ids.get(currentPosition);
		obj.LoadData();
		
		currentPosition++;
		return obj;
	}
	
	public DataFinder Where(String field, Object value) throws NoSuchFieldException, SecurityException
	{
		Field searchField = searchClass.getField(field);
		Index annotation = searchField.getAnnotation(Index.class);
		if (annotation == null)
			throw new IllegalArgumentException(field);
		
		conditions.put(annotation.IndexName(), DataWorker.ConvertValueForIndexing(value));
		
		return this;
	}
	
	private void initSearch()
	{
		for (Map.Entry<String, String> entry: conditions.entrySet())
		{
			ArrayList<Long> conditionIds = generateIdsForCondition(entry.getKey(), entry.getValue());
			intersect(conditionIds);
		}
	}
	
	private ArrayList<Long> generateIdsForCondition(String indexName, String value)
	{
		ArrayList<Long> results = new ArrayList<>();
		
		NodeReference node = DataWorker.GetNodeReference(indexGlobal);
		
		String key = "";
		while (true)
		{
			key = node.nextSubscript(indexName, value, key);
			if (key.equals(""))
				break;
			
			Long parsedKey = Long.parseLong(key);
			if (ids != null && !ids.contains(parsedKey))
				continue;
				
			results.add(parsedKey);
		}
		return results;
	}
	
	private void intersect(ArrayList<Long> filterList)
	{
		if (ids == null)
		{
			ids = filterList;
			return;
		}
		
		for (int i = ids.size()-1; i>=0; i--)
		{
			if (!filterList.contains(ids.get(i)))
			{
				ids.remove(i);
			}
		}
	}

}
