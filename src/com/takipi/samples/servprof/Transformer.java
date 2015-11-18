package com.takipi.samples.servprof;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.takipi.samples.servprof.asm.ClassReader;
import com.takipi.samples.servprof.asm.ClassVisitor;
import com.takipi.samples.servprof.asm.ClassWriter;
import com.takipi.samples.servprof.inst.InstrumentationFilter;
import com.takipi.samples.servprof.inst.ProfilerClassVisitor;
import com.takipi.samples.servprof.inst.ThreadNameSetterClassVisitor;

public class Transformer implements ClassFileTransformer {
	private final InstrumentationFilter instFilter;
	private final Options options;

	public Transformer(Options options, InstrumentationFilter instFilter) {
		this.instFilter = instFilter;
		this.options = options;
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		// if the class being loaded / redefined does not match our target cut
		// points return null
		// this instructs the JVM to continue loading that class as is
		if (!instFilter.shouldInstrumentClass(className)) {
			return null;
		}

		// initiate a reader which will scan the loaded byte code
		ClassReader cr = new ClassReader(classfileBuffer);

		// create a writer which will receive data from the reader and write
		// that into a new bytecode raw byte[] buffer
		// we let the ASM library take care of doing calculations of frame and
		// variable depths which are a part of the
		// bytecode structure
		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

		// and this is where we come in - our visitor will be staged between the
		// class reader and the writer
		// ever instruction read by the class reader will be passed on to us
		// before it goes into the writer
		// this gives us the ability to manipulate, or "instrument" the class's
		// code with our own

		ClassVisitor tnv = new ThreadNameSetterClassVisitor(cw, instFilter, className);
		
		ClassVisitor cv = new ProfilerClassVisitor(tnv, instFilter, className);

		// initiate a Visitor pattern between the reader and writer, passing our
		// visitor through the chain
		cr.accept(cv, 0);

		// the writer has now completed generating the new class, convert to raw
		// bytecode
		return cw.toByteArray();
	}

	public Options getOptions() {
		return options;
	}

	public InstrumentationFilter getInstFilter() {
		return instFilter;
	}
}
