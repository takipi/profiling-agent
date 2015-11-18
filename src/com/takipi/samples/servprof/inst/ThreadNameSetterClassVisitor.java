package com.takipi.samples.servprof.inst;

import com.takipi.samples.servprof.asm.ClassVisitor;
import com.takipi.samples.servprof.asm.MethodVisitor;
import com.takipi.samples.servprof.state.MethodKey;

public class ThreadNameSetterClassVisitor extends FilterClassVisitor {
	private final String className;

	public ThreadNameSetterClassVisitor(ClassVisitor cv, InstrumentationFilter instFilter, String className) {
		super(cv, instFilter);

		this.className = className;
	}

	@Override
	protected MethodVisitor createMethodVisitor(MethodVisitor mv, int access, String name, String desc) {
		return new ThreadNameSetterMethodVisitor(mv, access, new MethodKey(className, name), desc);
	}
}
