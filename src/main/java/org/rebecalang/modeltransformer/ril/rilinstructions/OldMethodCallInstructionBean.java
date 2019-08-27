package org.rebecalang.modeltransformer.ril.rilinstructions;

public class OldMethodCallInstructionBean extends InstructionBean {

	String methodName;

	public OldMethodCallInstructionBean(String methodName) {
		super();
		this.methodName = methodName;
	}

	
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}



	@Override
	public String toString() {
		return "call method " + "<" + methodName + ">"; 

	}
}
