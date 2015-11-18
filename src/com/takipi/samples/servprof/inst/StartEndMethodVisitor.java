package com.takipi.samples.servprof.inst;

import com.takipi.samples.servprof.asm.Label;
import com.takipi.samples.servprof.asm.MethodVisitor;
import com.takipi.samples.servprof.asm.Opcodes;
import com.takipi.samples.servprof.asm.Type;
import com.takipi.samples.servprof.state.MethodKey;
import com.takipi.samples.servprof.state.MetricsData;

public abstract class StartEndMethodVisitor extends MethodVisitor {
	protected final int access;
	protected final MethodKey methodKey;
	protected final String methodDesc;

	public StartEndMethodVisitor(MethodVisitor mv, int access, MethodKey methodKey, String desc) {
		super(Opcodes.ASM5, mv);
		this.access = access;
		this.methodKey = methodKey;
		this.methodDesc = desc;
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		super.visitLocalVariable(name, desc, signature, start, end, index);

		//add the local variable to the cut point method variable table
		ProfilerLocalVariablesTable.addVariable(methodKey, index, name);
	}

	@Override
	public void visitCode() {
		super.visitCode();

		insertPrologue();
	}

	@Override
	public void visitInsn(int opcode) {
		switch (opcode) {
		case Opcodes.RETURN:
		case Opcodes.IRETURN:
		case Opcodes.LRETURN:
		case Opcodes.FRETURN:
		case Opcodes.DRETURN:
		case Opcodes.ARETURN:
			insertEpilogue();
			break;
		}

		super.visitInsn(opcode);
	}

	private void loadThisIntoArray() {

		// load the array into the stack
		mv.visitInsn(Opcodes.DUP);

		// load index 0
		mv.visitInsn(Opcodes.ICONST_0);

		// load "this"
		mv.visitVarInsn(Opcodes.ALOAD, 0);

		// store in the array
		mv.visitInsn(Opcodes.AASTORE);
	}

	protected void loadArgsIntoObjectArray() {

		//if (MetricsData.captureState()) {
		//	loadArgsIntoObjectArray();
		//} else {
		//	loadNullIntoStack();
		//}
		
		// invoke MetricsData to see whether or not to collect state, Z = boolean
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(MetricsData.class),
				MetricsData.CAPTURE_STATE_NAME, "()Z", false);

		// check if the result is false, is so, don't call
		// doloadArgsIntoObjectArray, insetad skip to doloadNull
		Label falseLabel = new Label();
		mv.visitJumpInsn(Opcodes.IFEQ, falseLabel);

		// load the method arguments into the stack
		doLoadArgsIntoObjectArray();

		// jump to after the else clause
		Label jumpAfterIfLabel = new Label();
		mv.visitJumpInsn(Opcodes.GOTO, jumpAfterIfLabel);

		mv.visitLabel(falseLabel);

		// mark no change to stack structure after the jump
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

		// capture state == false => load null instead
		doloadNull();

		//we're done with the if, signal a no change to stack
		mv.visitLabel(jumpAfterIfLabel);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
	}

	protected void doloadNull() {
		mv.visitInsn(Opcodes.ACONST_NULL);
	}

	protected void doLoadArgsIntoObjectArray() {

		////////////////////////////////////////////////////////////////////////////////////////////
		//
		// load onto stack -> new Object[4](this, arg1, arg2, arg3,..);
		//
		////////////////////////////////////////////////////////////////////////////////////////////

		int thisOffset;

		// is the method a static one? if so, we'll add another variable (this)
		if ((access & Opcodes.ACC_STATIC) == 0) {
			thisOffset = 1;
		} else {
			thisOffset = 0;
		}

		// extract the argument types for this method
		Type[] args = Type.getArgumentTypes(methodDesc);

		// create a new Object[] array, large enough to hold the method's
		// arguments
		mv.visitIntInsn(Opcodes.SIPUSH, thisOffset + args.length);

		// this will leave the new array on the stack
		mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object");

		// is this is not a static method, we'll need to manually load "this"
		// into the array
		if ((access & Opcodes.ACC_STATIC) == 0) {
			loadThisIntoArray();
		}

		for (int i = 0; i < args.length; i++) {
			Type arg = args[i];

			// load the Object[] array into the stack
			mv.visitInsn(Opcodes.DUP);

			// loads the index value into which the argument will be assigned
			mv.visitIntInsn(Opcodes.SIPUSH, thisOffset + i);

			// get the argument type (e.g. primitive, object, array, ..)

			int argSort = arg.getSort();

			if ((argSort == Type.OBJECT) || (argSort == Type.ARRAY)) {
				// if the argument is an object (single or array), load it as
				// such
				// in bytecode there's no difference between loading an object
				// or an array into the stack
				mv.visitVarInsn(Opcodes.ALOAD, thisOffset + i);
			} else {

				// use our utility function to map into the right boxing
				// instructions
				PrimitiveBoxingInsns boxingInsns = PrimitiveBoxingInsns.getByType(argSort);

				// loads the primitive using the correct opcode
				mv.visitVarInsn(boxingInsns.getStackOpcode(), thisOffset + i);

				// invoke the boxing method
				mv.visitMethodInsn(Opcodes.INVOKESTATIC, boxingInsns.getTypeName(), "valueOf",
						boxingInsns.getMethodReturn(), false);
			}

			// store the method argument into the new array
			mv.visitInsn(Opcodes.AASTORE);
		}

		// at this point the new array which was instantiated using ANEWARRAY is
		// still on the stack
		// it will be popped by the next call into adjust method.
	}

	protected abstract void insertPrologue();

	protected abstract void insertEpilogue();
}
