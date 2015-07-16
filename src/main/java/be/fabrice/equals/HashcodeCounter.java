package be.fabrice.equals;

import java.util.HashMap;
import java.util.Map;

public class HashcodeCounter {
	static private Map<Class<?>, Integer> count = new HashMap<Class<?>, Integer>();
	
	static public int get(Class<?> clazz){
		Integer i = count.get(clazz);
		return i==null?0:i.intValue();
	}
	
	static public void tic(Class<?> clazz){
		Integer i = count.get(clazz);
		int c = i==null?0:i.intValue();
		c++;
		count.put(clazz,c);
	}
	
	static public void reinit(){
		count = new HashMap<Class<?>, Integer>();
	}
	
	static public void reinit(Class<?> clazz){
		count.put(clazz,0);
	}
}
