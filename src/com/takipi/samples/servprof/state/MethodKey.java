package com.takipi.samples.servprof.state;

// class used to identify a cut point method
public class MethodKey {

	private final String className;
	private final String methodName;

	public MethodKey(String className, String methodName) {
		this.methodName = methodName;
		this.className = className;
	}

	public static MethodKey fromString(String methodKey)
	{
		//escape the $ meta char
		String[] parts = methodKey.split("\\$");
		return new MethodKey(parts[0], parts[1]);
	}
	
	@Override
	public String toString() {
		return className + "$" + methodName;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof MethodKey)) {
			return false;
		}

		MethodKey other = (MethodKey) (obj);

		if (!className.equals(other.className)) {
			return false;
		}

		if (!methodName.equals(other.methodName)) {
			return false;
		}

		return true;
	}
	
	@Override
	public int hashCode() {
		return className.hashCode() ^ methodName.hashCode();
	}
}
