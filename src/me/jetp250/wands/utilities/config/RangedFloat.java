package me.jetp250.wands.utilities.config;

import java.util.Random;
import java.util.regex.Pattern;

public class RangedFloat {

	static final Pattern SPLIT_PATTERN = Pattern.compile("\\.\\.");

	private final float lowerBound, upperBound;

	public RangedFloat(float both) {
		this(both, both);
	}

	public RangedFloat(float lower, float upper) {
		this.lowerBound = lower;
		this.upperBound = upper;
	}

	public RangedFloat(String string) {
		String[] split = RangedFloat.SPLIT_PATTERN.split(string);
		if (string.startsWith("..")) {
			this.lowerBound = Float.MIN_VALUE;
		} else if (split.length != 0) {
			this.lowerBound = parseFloat(split[0]);
		} else {
			this.lowerBound = 0;
		}
		if (string.endsWith("..")) {
			this.upperBound = Float.MAX_VALUE;
		} else if (split.length != 0) {
			this.upperBound = parseFloat(split[split.length - 1]);
		} else {
			this.upperBound = this.lowerBound;
		}
	}

	public boolean equal() {
		return lowerBound == upperBound;
	}

	public float getLowerBound() {
		return this.lowerBound;
	}

	public float getUpperBound() {
		return this.upperBound;
	}

	public boolean inRange(float val) {
		return val >= this.lowerBound && val <= this.upperBound;
	}

	public float getRandomValue(Random random) {
		return random.nextFloat() * (upperBound - lowerBound) + lowerBound;
	}

	@Override
	public String toString() {
		return lowerBound + ".." + upperBound;
	}

	private static float parseFloat(String input) {
		try {
			return Float.parseFloat(input);
		} catch (NumberFormatException ex) {
			return 0.0F;
		}
	}

}
