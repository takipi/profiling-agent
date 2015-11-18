package com.takipi.samples.servprof;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.takipi.samples.servprof.state.LocalVarMultiplexer;
import com.takipi.samples.servprof.state.MetricsData;

public class ReporterFactory {
	public static BaseReporter createStatsdReporter(String name, long reportIntervalMillis) {

		MetricsData.statsCollector.setMultiplexer(new LocalVarMultiplexer("descr"));

		StatsdReporter reporter = new StatsdReporter(name, reportIntervalMillis, false);

		return reporter;

	}

	public static BaseReporter createFileReporter(String name, String outputFileName, long reportIntervalMillis)
			throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(outputFileName);
		BaseReporter reporter = new FileReporter(pw, name, reportIntervalMillis, true);

		return reporter;

	}
}
