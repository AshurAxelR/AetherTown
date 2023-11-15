package com.xrbpowered.aethertown.world;

public class GeneratorException extends RuntimeException {

	private GeneratorException(String message) {
		super(message);
	}

	private GeneratorException(String format, Object... args) {
		super(String.format(format, args));
	}

	public static void raise(String message) {
		throw new GeneratorException(message);
	}

	public static void raise(String format, Object... args) {
		throw new GeneratorException(format, args);
	}

	public static void warning(String message) {
		System.err.println(message);
	}

	public static void warning(String format, Object... args) {
		System.err.printf(format, args);
	}

}
