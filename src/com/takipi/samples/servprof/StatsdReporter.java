package com.takipi.samples.servprof;

import com.takipi.timgroup.statsd.NonBlockingStatsDClient;
import com.takipi.timgroup.statsd.StatsDClient;

public class StatsdReporter extends BaseReporter {
	static final StatsDClient statsd = new NonBlockingStatsDClient("takipi.agent", "localhost", 8125);

	public StatsdReporter(String name, long reportIntervalMillis, boolean printToConsole) {
		super(name, reportIntervalMillis, printToConsole);
	}

	@Override
	protected void reportMetric(String metricName, long value) {
		//fire the metric into the StatsD server
		statsd.recordExecutionTime(metricName, value);
	}
}
