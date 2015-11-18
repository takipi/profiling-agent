package com.takipi.samples.servprof.inst;

import java.util.regex.Pattern;

public class InstrumentationFilter {
	private final Pattern classPattern;
	private final Pattern methodPattern;

	public InstrumentationFilter(String classPattern, String methodPattern) {
		this.classPattern = Pattern.compile(classPattern);
		this.methodPattern = Pattern.compile(methodPattern);
	}

	public boolean shouldInstrumentClass(String className) {
		return classPattern.matcher(className).matches();

	}

	public boolean shouldInstrumentMethod(String methodName) {
		return methodPattern.matcher(methodName).matches();
	}
}
