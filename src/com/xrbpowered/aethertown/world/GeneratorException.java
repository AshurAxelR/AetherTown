package com.xrbpowered.aethertown.world;

public class GeneratorException extends RuntimeException {

	public GeneratorException(String message) {
		super(message);
	}

	public GeneratorException(String format, Object... args) {
		super(String.format(format, args));
	}

	public GeneratorException(Throwable cause) {
		super(cause);
	}

}
