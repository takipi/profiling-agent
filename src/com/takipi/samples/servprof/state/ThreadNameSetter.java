package com.takipi.samples.servprof.state;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.takipi.samples.servprof.inst.ProfilerLocalVariablesTable;

public class ThreadNameSetter {

	private static final ThreadLocal<String> originalThreadName;
	private static final Map<Class<?>, Boolean> simpleNames;
	private static Method objectToString;

	private static String formatValue(Object value) throws NoSuchMethodException, SecurityException {
		if (value == null) {
			return "null";
		}

		// if the Object's class doesn't override toString() => print the
		// class's simple name
		// for this we use reflection to check for an override
		Boolean useSimpleName = simpleNames.get(value.getClass());

		//we don't have a cached answer
		if (useSimpleName == null) {
			Method toStringMethod = value.getClass().getMethod("toString", new Class[0]);
			useSimpleName = objectToString.equals(toStringMethod);
			simpleNames.put(value.getClass(), useSimpleName);
		}

		if (useSimpleName) {
			return value.getClass().getSimpleName();
		}

		return value.toString();
	}

	public static void restoreThreadName() {
		// reset to original
		Thread.currentThread().setName(originalThreadName.get());
	}

	public static void setThreadName(String methodKey, Object[] args) throws NoSuchMethodException, SecurityException {

		// back up the current thread name into a thread local variable
		originalThreadName.set(Thread.currentThread().getName());

		// get the method's variable names, convert the string identifier that
		// was embedded into the bytecode back to comparable objects
		String[] varNames = ProfilerLocalVariablesTable.getMethodVariableNames(MethodKey.fromString(methodKey));

		StringBuilder statefulName = new StringBuilder();

		// append the original thread name
		statefulName.append(Thread.currentThread().getName());

		// when did we get hold of the thread
		statefulName.append(" Start time: ");
		statefulName.append(new Date());

		// add the dynamic state
		statefulName.append(" args: ");

		int index = 0;

		for (String varName : varNames) {
			statefulName.append(varName);
			statefulName.append(" = ");
			statefulName.append(formatValue(args[index]));

			if (index < args.length - 1) {
				statefulName.append(", ");
			}

			index++;
		}

		// set the stateful thread name
		Thread.currentThread().setName(statefulName.toString());
	}

	static {
		originalThreadName = new ThreadLocal<>();
		simpleNames = new ConcurrentHashMap<>();

		try {
			// grab the base toString method from Object
			// we'll use it later for parsing of object values
			objectToString = Object.class.getMethod("toString", new Class[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
