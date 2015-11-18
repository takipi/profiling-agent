package com.takipi.samples.servprof.state;

public class MetricsData {
	//this are the singletons which will be used by the instrumented code
	public static final String START_TIMES_TLS_NAME = "startTimes";
	public static final String STATS_COLLECTOR_NAME = "statsCollector";
	public static final String CAPTURE_STATE_NAME = "captureState";
	
	public static final ThreadLocal<Long> startTimes;
	public static final MetricsCollector statsCollector;
	
	public static boolean captureState()
	{
		//a stub method to decide whether or not dynamic data is collected for metrics
		return true;
	}

	static {
		startTimes = new ThreadLocal<>();
		statsCollector = new MetricsCollector();
	}
}
