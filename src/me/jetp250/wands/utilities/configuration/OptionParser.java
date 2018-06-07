package me.jetp250.wands.utilities.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class OptionParser {

	private OptionParser() {
	}

	private static final Pattern INT_PATTERN = Pattern.compile("-?\\d+");
	private static final Pattern FLOAT_PATTERN = Pattern.compile("-?\\d+.\\d+");

	public static Map<String, Object> parse(String[] arguments) {
		Map<String, Object> map = new HashMap<>(arguments.length);
		for (String string : arguments) {
			int index = string.indexOf('=');
			if (index == -1) {
				System.err.println("Invalid parameter: " + string);
				continue;
			}
			String key = string.substring(0, index);
			String str = string.substring(index + 1);
			Object value = str;
			if (isInt(str)) {
				value = Integer.parseInt(str);
			} else if (isFloat(str)) {
				value = Float.parseFloat(str);
			} else if (isBoolean(str)) {
				value = Boolean.parseBoolean(str);
			}
			map.put(key, value);
		}
		return map;
	}

	private static boolean isBoolean(String input) {
		String lower = input.toLowerCase();
		return lower.equals("true") || lower.equals("false");
	}

	private static boolean isInt(String input) {
		return INT_PATTERN.matcher(input).matches();
	}

	private static boolean isFloat(String input) {
		return FLOAT_PATTERN.matcher(input).matches();
	}

}
