package com.takipi.samples.servprof.state;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class MetricsCollector {
	private final Map<Object, LongAdder> map;
	private MetricMultiplexer multiplexer;

	public MetricsCollector() {
		// metrics can be added dynamically so use a thread safe structure
		//each of the metric values can also be changed concurrently, 
		//so use a LongAdder to sum up the values safely and efficiently
		this.map = new ConcurrentHashMap<>();
	}

	public void adjust(Object metric, long amount) {
		adjust(metric, amount);
	}

	public void adjust(Object metric, long amount, Object data) {

		//if we have a multiplexer = use it
		if (getMultiplexer() != null) {

			// give an optional multiplexer the ability to use dynamic state to
			// divide the metrics into sub categories
			Object[] multiplexedMetrics = getMultiplexer().multiplex(metric, data);

			for (Object multiplexedMetric : multiplexedMetrics) {
				doAdjust(multiplexedMetric, amount);
			}
		}
		else
		{
			doAdjust(metric, amount);
		}
	}

	private void doAdjust(Object metric, long amount) {

		LongAdder curValue = map.get(metric);

		//new metric? => add to map
		if (curValue == null) {
			curValue = new LongAdder();
			map.put(metric, curValue);
		}

		//adjust the metric value
		//more about long adders here - 
		//http://blog.takipi.com/java-8-longadders-the-fastest-way-to-add-numbers-concurrently/
		curValue.add(amount);
	}

	public Map<Object, Long> asMap() {
		Map<Object, Long> result = new HashMap<>();

		for (Map.Entry<Object, LongAdder> entry : map.entrySet()) {
			// use the long adder's sumThenReset to safely get and reset the metric value
			result.put(entry.getKey(), entry.getValue().sumThenReset());
		}

		return result;
	}

	public MetricMultiplexer getMultiplexer() {
		return multiplexer;
	}

	public void setMultiplexer(MetricMultiplexer multiplexer) {
		this.multiplexer = multiplexer;
	}
}
