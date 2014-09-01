package com.lynda.javatraining.filetree;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MyTreeMap<K, V> extends TreeMap<K, V> {

	private static final long serialVersionUID = 1848500171020873297L;

	@Override
	public String toString() {
		Iterator<Entry<K, V>> i = super.entrySet().iterator();
		if (!i.hasNext())
			return "";

		StringBuilder sb = new StringBuilder();
		for (;;) {
			Entry<K, V> e = i.next();
			K key = e.getKey();
			V value = e.getValue();
			sb.append(key == this ? "(this Map)" : key);
			sb.append(": ");
			sb.append(value == this ? "(this Map)" : value);
			if (!i.hasNext())
				return sb.toString();
			sb.append(System.lineSeparator());
		}
	}
}
