package tester;

public class IndexTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SomeClass c = new SomeClass();
		c.StringIndexingField = "testIndex";
		c.StringNonIndexingField = "testNonIndex";
		c.Save();

	}

}
