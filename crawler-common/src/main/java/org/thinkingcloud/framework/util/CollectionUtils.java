package org.thinkingcloud.framework.util;

import java.util.Collection;
import java.util.Map;

public class CollectionUtils {

	public static <E> boolean isEmpty(Collection<E> collection) {
		if (collection == null || collection.isEmpty())
			return true;
		return false;
	}

	public static <K, V> boolean isEmpty(Map<K, V> map) {
		if (map == null || map.isEmpty())
			return true;
		return false;
	}
}
