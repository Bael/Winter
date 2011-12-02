package tester;

import Core.Index;
import Core.Persistent;

public class SomeClass extends Persistent {
	
	@Index(IndexName="stringIndexingFiledIndex")
	public String StringIndexingField;
	
	@Index(IndexName="findFieldIndex")
	public String findField;
	
	public String StringNonIndexingField;

}
