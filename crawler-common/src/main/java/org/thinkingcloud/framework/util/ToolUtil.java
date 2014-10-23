package org.thinkingcloud.framework.util;

import java.util.HashMap;
import java.util.Map;

public class ToolUtil {

	public static final Map<String, Object> toArgMap(Object... args) {
		if (args == null) {
			return null;
		}
		if (args.length % 2 != 0) {
			throw new RuntimeException("expected pairs of argName argValue");
		}
		HashMap<String, Object> res = new HashMap<String, Object>();
		for (int i = 0; i < args.length; i += 2) {
			if (args[i + 1] != null) {
				res.put(String.valueOf(args[i]), args[i + 1]);
			}
		}
		return res;
	}
}
