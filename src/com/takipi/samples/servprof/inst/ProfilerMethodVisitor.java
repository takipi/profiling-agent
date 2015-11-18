package com.takipi.samples.servprof.inst;

import com.takipi.samples.servprof.asm.MethodVisitor;
import com.takipi.samples.servprof.asm.Opcodes;
import com.takipi.samples.servprof.asm.Type;
import com.takipi.samples.servprof.state.MethodKey;
import com.takipi.samples.servprof.state.MetricsCollector;
import com.takipi.samples.servprof.state.MetricsData;

public class ProfilerMethodVisitor extends StartEndMethodVisitor {
	public ProfilerMethodVisitor(MethodVisitor mv, int access, MethodKey methodKey, String methodDesc) {
		super(mv, access, methodKey, methodDesc);
	}

	@Override
	protected void insertPrologue() {
		//////////////////////////////////////////////////////////
		//
		// State.startTimes.set(System.currentTimeMillis());
		//
		//////////////////////////////////////////////////////////

		// Put the TLS object on the stack, getting ready to invoke .set(...) on
		// it in order to store
		// the start timestamp.
		//
		// Stack: [ThreadLocal<Long>]
		//
		super.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(MetricsData.class),
				MetricsData.START_TIMES_TLS_NAME, Type.getDescriptor(ThreadLocal.class));

		// Put the current timestamp (long) on the stack.
		//
		// Stack: [ThreadLocal<Long>, long]
		//
		super.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(System.class), "currentTimeMillis", "()J",
				false);

		// Box the timestamp (long -> Long) by using Long.valueOf(...).
		//
		// Stack: [ThreadLocal<Long>, Long]
		//
		super.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Long.class), "valueOf", "(J)Ljava/lang/Long;",
				false);

		// Invoke .set(...) on the "start times" TLS with the boxed timestamp as
		// an argument.
		//
		// Stack: []
		//
		super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(ThreadLocal.class), "set",
				"(Ljava/lang/Object;)V", false);

	}

	@Override
	protected void insertEpilogue() {
		////////////////////////////////////////////////////////////////////////////////////////////
		//
		// State.totalTimes.adjust(className, System.currentTimeMillis() -
		//////////////////////////////////////////////////////////////////////////////////////////// State.startTimes.get());
		//
		////////////////////////////////////////////////////////////////////////////////////////////

		// Put the "total times" map on the stack.
		//
		// Stack: [LongHashMap<String>]
		//
		mv.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(MetricsData.class), MetricsData.STATS_COLLECTOR_NAME,
				Type.getDescriptor(MetricsCollector.class));

		// Put the map's key on the stack (the owner class of the method we're
		// running in).
		// This is the first argument in the call to .adjust(...)
		//
		// Stack: [LongHashMap<String>, String]
		//
		mv.visitLdcInsn(methodKey.toString());

		// We move on to calculating the second argument to .adjust(...).
		// Put the current timestamp (long) on the stack -- this is the "end"
		// time.
		//
		// Stack: [LongHashMap<String>, String, long]
		//
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(System.class), "currentTimeMillis", "()J", false);

		// Put the "start times" TLS on the stack, getting ready to fetch this
		// thread's previous start-time
		// value (the time the method started running).
		//
		// Stack: [LongHashMap<String>, String, long, ThreadLocal<Long>]
		//
		mv.visitFieldInsn(Opcodes.GETSTATIC, Type.getInternalName(MetricsData.class), MetricsData.START_TIMES_TLS_NAME,
				Type.getDescriptor(ThreadLocal.class));

		// Retrieve the start timestamp from the TLS object. This leaves the
		// boxed timestamp on the
		// stack. We know for sure it is not null since we explicitly stored a
		// value at the beginning of
		// the method.
		//
		// Stack: [LongHashMap<String>, String, long, Object]
		//
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(ThreadLocal.class), "get",
				"()Ljava/lang/Object;", false);

		// Since ThreadLocal is generic, the value we just pulled sits on the
		// stack as a java.lang.Object.
		// We need to cast it into a boxed Long.
		//
		// Stack: [LongHashMap<String>, String, long, Long]
		//
		mv.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(Long.class));

		// Unbox the start-time value (Long -> long) by using Long.longValue().
		//
		// Stack: [LongHashMap<String>, String, long, long]
		//
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Long.class), "longValue", "()J", false);

		// Subtract the start timestamp that is currently at the top of the
		// stack, from current timestamp,
		// which is just underneath it in the stack. This leaves us with the
		// running time in millis, at the
		// top of the stack.
		//
		// Stack: [LongHashMap<String>, String, long]
		//
		mv.visitInsn(Opcodes.LSUB);

		// load the method's argument as Object[]
		//
		// Stack: [LongHashMap<String>, String, long, Object[]]
		//
		loadArgsIntoObjectArray();

		// Accumulate the running time into the "total times" map by invoking
		// its .adjust(...) method, with
		// the class name as the key and the running time as the value
		// difference.
		//

		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(MetricsCollector.class), "adjust",
				"(Ljava/lang/Object;JLjava/lang/Object;)V", false);

		// Stack: []
		//
	}
}
