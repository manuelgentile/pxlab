package de.pxlab.pxl;

import java.util.HashMap;

/**
 * A static HashMap which stores objects that must be preserved across display
 * list presentations. All methods of this class are derived from the HashMap
 * class.
 * 
 * @author H. Irtel
 * @version 0.1.0
 * @see java.util.HashMap
 */
public class RuntimeRegistry {
	private static HashMap registry = new HashMap(100);

	public static Object put(Object key, Object value) {
		return registry.put(key, value);
	}

	public static boolean containsKey(Object key) {
		return registry.containsKey(key);
	}

	public static Object get(Object key) {
		return registry.get(key);
	}

	public static Object remove(Object key) {
		return registry.get(key);
	}

	public static void clear() {
		// System.out.println("RuntimeRegistry.clear()");
		registry.clear();
	}

	public static Object createKey(String type, int subtype, int id) {
		return type + "_" + String.valueOf(subtype) + "_" + String.valueOf(id);
	}
}
