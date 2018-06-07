package me.jetp250.wands.utilities.config;

import java.util.Random;

public class RangedInt {

	private final int lowerBound, upperBound;

	public RangedInt(int both) {
		this(both, both);
	}

	public RangedInt(int lower, int upper) {
		this.lowerBound = lower;
		this.upperBound = upper;
	}

	public RangedInt(String string) {
		String[] split = RangedFloat.SPLIT_PATTERN.split(string);
		if (string.startsWith("..")) {
			this.lowerBound = Integer.MIN_VALUE;
		} else if (split.length != 0) {
			this.lowerBound = parseInt(split[0]);
		} else {
			this.lowerBound = 0;
		}
		if (string.endsWith("..")) {
			this.upperBound = Integer.MAX_VALUE;
		} else if (split.length != 0) {
			this.upperBound = parseInt(split[split.length - 1]);
		} else {
			this.upperBound = this.lowerBound;
		}
	}

	public boolean equal() {
		return lowerBound == upperBound;
	}

	public int getLowerBound() {
		return this.lowerBound;
	}

	public int getUpperBound() {
		return this.upperBound;
	}

	public boolean inRange(int val) {
		return val >= this.lowerBound && val <= this.upperBound;
	}

	public int getRandomValue(Random random) {
		if (equal())
			return lowerBound;
		return random.nextInt(upperBound - lowerBound) + lowerBound;
	}

	@Override
	public String toString() {
		return lowerBound + ".." + upperBound;
	}

	private static int parseInt(String input) {
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

}
