package com.takipi.samples.servprof.inst;

import java.util.HashMap;
import java.util.Map;

import com.takipi.samples.servprof.asm.Opcodes;
import com.takipi.samples.servprof.asm.Type;

public class PrimitiveBoxingInsns {
	private final String typeName;
	private final String methodReturn;
	private final int stackOpcode;

	private static Map<Integer, PrimitiveBoxingInsns> map;

	private PrimitiveBoxingInsns(String typeName, String methodReturn, int stackOpcode) {
		this.typeName = typeName;
		this.methodReturn = methodReturn;
		this.stackOpcode = stackOpcode;
	}

	public String getTypeName() {
		return typeName;
	}

	public int getStackOpcode() {
		return stackOpcode;
	}

	public String getMethodReturn() {
		return methodReturn;
	}

	public static PrimitiveBoxingInsns getByType(int typeSort) {
		return map.get(typeSort);
	}

	static {
		map = new HashMap<>();

		map.put(Type.BOOLEAN, new PrimitiveBoxingInsns("java/lang/Boolean", "(I)Ljava/lang/Boolean;", Opcodes.ILOAD));

		map.put(Type.BYTE, new PrimitiveBoxingInsns("java/lang/Byte", "(I)Ljava/lang/Byte;", Opcodes.ILOAD));

		map.put(Type.CHAR, new PrimitiveBoxingInsns("java/lang/Char", "(I)Ljava/lang/Char;", Opcodes.ILOAD));

		map.put(Type.SHORT, new PrimitiveBoxingInsns("java/lang/Short", "(I)Ljava/lang/Short;", Opcodes.ILOAD));

		map.put(Type.INT, new PrimitiveBoxingInsns("java/lang/Integer", "(I)Ljava/lang/Integer;", Opcodes.ILOAD));

		map.put(Type.FLOAT, new PrimitiveBoxingInsns("java/lang/Float", "(F)Ljava/lang/Float;", Opcodes.FLOAD));

		map.put(Type.LONG, new PrimitiveBoxingInsns("java/lang/Long", "(J)Ljava/lang/Long;", Opcodes.LLOAD));

		map.put(Type.DOUBLE, new PrimitiveBoxingInsns("java/lang/Double", "(D)Ljava/lang/Double;", Opcodes.DLOAD));

	}
}