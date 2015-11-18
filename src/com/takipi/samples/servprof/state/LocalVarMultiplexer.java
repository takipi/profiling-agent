package com.takipi.samples.servprof.state;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.takipi.samples.servprof.inst.ProfilerLocalVariablesTable;

public class LocalVarMultiplexer implements MetricMultiplexer {

	private final String varName;

	public LocalVarMultiplexer(String varName) {
		this.varName = varName;
	}

	@Override
	public Object[] multiplex(Object metric, Object state) {

		//create a method key based of the metric name
		MethodKey methodKey = MethodKey.fromString(metric.toString());
		
		// get the local variable table for the target method
		String[] varNames = ProfilerLocalVariablesTable.getMethodVariableNames(methodKey);

		// get the index of the target var name from the table
		int index = Arrays.asList(varNames).indexOf(varName);

		if (index == -1)
		{
			return new Object[] {metric};
		}
		
		// the state is local var array
		Object[] varValues = (Object[]) state;

		// grab the value of the target variable
		Object value = varValues[index];

		// return the sub metric
		return new Object[] {metric, metric + "." + value};
	}
}
