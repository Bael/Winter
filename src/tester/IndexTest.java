package tester;

import Core.DataFinder;

public class IndexTest {

	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException {
		fill();
		
		DataFinder finder = 
				new DataFinder(SomeClass.class).
					Where("StringIndexingField", "f3").
					Where("findField", "33");
		
		while (true)
		{
			SomeClass s = (SomeClass) finder.Next();
			if (s == null)
				break;
			System.out.println("founded " + s.StringNonIndexingField);
		}
	}
	
	private static void fill()
	{
		SomeClass c = null;
		
		for (int i=0;i<5;i++)
		{
			c = new SomeClass();
			c.StringIndexingField = "f" + i;
			c.findField = "33";
			c.StringNonIndexingField = "fp 1" + i + " Obj";
			c.Save();
		}
	}

}
