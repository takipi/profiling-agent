package com.takipi.samples.servprof;

import java.io.FileNotFoundException;
import java.lang.instrument.Instrumentation;

import com.sun.tools.attach.VirtualMachine;
import com.takipi.samples.servprof.inst.InstrumentationFilter;

public class Agent {

	private static Transformer internalPremain(String agentArgs, Instrumentation inst) throws FileNotFoundException {
		System.out.println("Takipi sample profiling agent loaded.");

		// parse the options that were passed into the java agent
		Options options = Options.parse(agentArgs);

		// print options to console
		options.print();

		// create a cut point filter based on Regex supplied through options
		InstrumentationFilter instFilter = new InstrumentationFilter(options.getClassPattern(),
				options.getMethodPattern());

		// initialize the bytecode instrumentation transformer
		Transformer transformer = new Transformer(options, instFilter);

		// start our reporter thread
		//BaseReporter reporter = ReporterFactory.createFileReporter("takipi-profile-reporter", options.outputFileName(),
		//		options.getReportIntervalMillis());

		 BaseReporter reporter =
		 ReporterFactory.createStatsdReporter("takipi-profile-reporter",
		 options.getReportIntervalMillis());

		reporter.start();

		// connect our transformer into the bytecode transformation chain
		inst.addTransformer(transformer, true);

		return transformer;
	}

	// this method is invoked when an agent is attached to a JVM at launch
	public static void premain(String agentArgs, Instrumentation inst) {
		try {
			internalPremain(agentArgs, inst);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// this method is used as a launcher to attach an agent to a live jvm
	public static void main(String[] args) {
		// target jar name
		String jarName = args[0];

		// jar arguments
		String agentArgs = args[1];

		// parse the options that were passed into the java agent
		Options options = Options.parse(agentArgs);

		// print options to console
		options.print();

		try {

			// attach to the target JVM
			VirtualMachine vm = VirtualMachine.attach(options.getVmProcessId());

			// load our library and detach
			vm.loadAgent(jarName, agentArgs);
			vm.detach();
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	// this method is invoked when an agent is attached to a live JVM
	public static void agentmain(String agentArgs, Instrumentation inst) throws FileNotFoundException {
		System.out.println("Takipi sample profiling remote attached.");

		// initialize a transformer
		Transformer transformer = internalPremain(agentArgs, inst);

		try {
			// gather the list of classes that have already been loaded into the
			// JVM
			Class<?> loadedClasses[] = inst.getAllLoadedClasses();

			for (Class<?> loadedClass : loadedClasses) {
				String internalName = loadedClass.getName();

				// if the class matches our filter, initiate a re-transform
				// sequence
				if (transformer.getInstFilter().shouldInstrumentClass(internalName)) {
					System.out.println("Retransforming loaded class " + internalName);
					inst.retransformClasses(loadedClass);
				}

			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

}
