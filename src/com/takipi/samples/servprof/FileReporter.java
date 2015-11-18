package com.takipi.samples.servprof;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

public class FileReporter extends BaseReporter {
	protected final PrintWriter pw;

	public FileReporter(PrintWriter pw, String name, long reportIntervalMillis, boolean printToConsole) {
		super(name, reportIntervalMillis, printToConsole);
		this.pw = pw;
	}

	@Override
	protected void reportMetrics(Map<Object, Long> metrics) {

		pw.println("Takipi servlet profiler state for " + new Date().toString() + ":");
		pw.println("  Total classes profiled: " + metrics.size());

		super.reportMetrics(metrics);

		pw.println("End.");
		pw.flush();

	}

	@Override
	protected void reportMetric(String name, long value) {
		String output = parseMetric(name, value);
		pw.println(output);
	}
}
