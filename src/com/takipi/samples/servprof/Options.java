package com.takipi.samples.servprof;

import java.util.HashMap;
import java.util.Map;

public class Options {
	private static final String OUTPUT_FILE_FORMAT_KEY = "o";
	private static final String REPORT_INTERVAL_KEY = "r";
	private static final String CLASS_PATTERN_KEY = "c";
	private static final String METHOD_PATTERN_KEY = "m";
	private static final String VM_PROCESS_ID_KEY = "p";

	private final String outputFileName;
	private final String classPattern;
	private final String methodPattern;
	private final long reportIntervalMillis;
	private final String vmProcessId;

	public static Options parse(String optionsStr) {
		Map<String, String> optionMap = toMap(optionsStr);

		String outputFileFormat = readString(optionMap, OUTPUT_FILE_FORMAT_KEY, "%S");
		String outputFileName = String.format(outputFileFormat, Long.toString(System.currentTimeMillis()));

		String classPattern = readString(optionMap, CLASS_PATTERN_KEY, "");
		String methodPattern = readString(optionMap, METHOD_PATTERN_KEY, "");
		long reportIntervalMillis = readLong(optionMap, REPORT_INTERVAL_KEY, 1000);
		String vmProcessId = readString(optionMap, VM_PROCESS_ID_KEY, "");

		return new Options(outputFileName, classPattern, methodPattern, reportIntervalMillis, vmProcessId);
	}

	private static long readLong(Map<String, String> map, String key, long defaultValue) {
		if (map == null) {
			return defaultValue;
		}

		Object value = map.get(key);

		if (value == null) {
			return defaultValue;
		}

		return Long.parseLong((String) value);
	}

	private static String readString(Map<String, String> map, String key, String defaultValue) {
		if (map == null) {
			return defaultValue;
		}

		Object value = map.get(key);

		if (value == null) {
			return defaultValue;
		}

		return (String) value;
	}

	private Options(String outputFilePrefix, String classPattern, String methodPattern, long reportIntervalMillis,
			String vmProcessId) {
		this.outputFileName = outputFilePrefix;
		this.classPattern = classPattern;
		this.methodPattern = methodPattern;
		this.reportIntervalMillis = reportIntervalMillis;
		this.vmProcessId = vmProcessId;
	}

	public String outputFileName() {
		return outputFileName;
	}

	public String getClassPattern() {
		return classPattern;
	}

	public String getMethodPattern() {
		return methodPattern;
	}

	public long getReportIntervalMillis() {
		return reportIntervalMillis;
	}

	private static Map<String, String> toMap(String optionsStr) {
		Map<String, String> optionMap = new HashMap<>();

		if (optionsStr == null) {
			return optionMap;
		}

		String[] pairs = optionsStr.split(",");

		for (String pair : pairs) {
			int splitIndex = pair.indexOf("=");

			String key = pair.substring(0, splitIndex);
			String value = pair.substring(splitIndex + 1);

			optionMap.put(key, value);
		}

		return optionMap;
	}

	public void print() {
		System.out.println("> Output file name: " + outputFileName);
		System.out.println("> Class Pattern:    " + classPattern);
		System.out.println("> Method Pattern:   " + methodPattern);
		System.out.println("> Report interval:  " + reportIntervalMillis + " ms");
	}

	public String getVmProcessId() {
		return vmProcessId;
	}
}
