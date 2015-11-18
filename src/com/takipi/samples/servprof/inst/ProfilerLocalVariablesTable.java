package com.takipi.samples.servprof.inst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.takipi.samples.servprof.state.MethodKey;

public class ProfilerLocalVariablesTable {

	// a simple container to hold the variable names for each cut point method
	
	// map an instrumented method to its list of variables
	private static Map<MethodKey, List<String>> map;

	public static String[] getMethodVariableNames(MethodKey methodKey) {
		List<String> vars = map.get(methodKey);

		if (vars == null) {
			return new String[0];
		}

		return vars.toArray(new String[0]);
	}

	public static void addVariable(MethodKey methodKey, int index, String name) {
		List<String> vars = map.get(methodKey);

		//new cut point method? => add to map
		if (vars == null) {
			vars = new ArrayList<>();
			map.put(methodKey, vars);
		}

		//if we haven't added this variable, let's do so
		if (vars.size() <= index) {
			vars.add(name);
		}
	}

	static {
		
		//as classes can be loaded concurrently, use a thread safe structure
		map = new ConcurrentHashMap<>();
	}
}
