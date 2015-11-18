package com.takipi.samples.servprof.state;

public interface MetricMultiplexer {

	//multiplex a metric into a collection of metrics based on dynamic state
	public Object[] multiplex(Object name, Object state);
}
