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
		int r = 0;
		if(o.c.equals(Integer.class)){
			// compare obj to integer
			r = ((Integer) o.value).compareTo((Integer) this.value);
		}		
		if(o.c.equals(Long.class)){
			// compare obj to long
			r = ((Long) o.value).compareTo((Long) this.value);
		}		
		if(o.c.equals(Double.class)){
			// compare obj to double
			r = ((Double) o.value).compareTo((Double) this.value);
		}		
		if(o.c.equals(Float.class)){
			// compare obj to double
			r = ((Float) o.value).compareTo((Float) this.value);
		}		
		if(o.c.equals(String.class)){
			// compare obj to double
			r = ((String) o.value).compareTo((String) this.value);
		}		
		if(r == 0){
			return this.key.compareTo(o.key);
		} else {
			return r;
		}		
	}	
	
}


