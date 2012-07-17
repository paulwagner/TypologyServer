/**
 * Sort by Value Collection.
 * Give key, value and type of value to the constructor.
 * You can then sort a set of these entries with java.util.Collections
 * 
 * @author Paul Wagner
 */
package de.typology.tools;

public class SBVEntry implements Comparable<SBVEntry> {
	
	public String key;
	public Object value;
	
	@SuppressWarnings("rawtypes")
	private Class c;
	
	@SuppressWarnings("rawtypes")
	public SBVEntry(String key, Object value, Class c){
		this.key = key;
		this.value = value;
		this.c = c;
	}

	@Override
	public int compareTo(SBVEntry o) {
		if(o.c.equals(Integer.class)){
			// compare obj to integer
			return ((Integer) o.value).compareTo((Integer) this.value);
		}		
		if(o.c.equals(Long.class)){
			// compare obj to long
			return ((Long) this.value).compareTo((Long) o.value);
		}		
		if(o.c.equals(Double.class)){
			// compare obj to double
			return ((Double) this.value).compareTo((Double) o.value);
		}		
		if(o.c.equals(Float.class)){
			// compare obj to double
			return ((Float) this.value).compareTo((Float) o.value);
		}		
		return 0;
	}	
	
}


