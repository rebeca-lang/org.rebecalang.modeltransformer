package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

public class ReturnInstructionBean extends InstructionBean {

	Object returnValue;

	public ReturnInstructionBean(Object returnValue) {
		super();
		this.returnValue = returnValue;
	}

	public Object getReturnValue() {
		return returnValue;
	}
	
	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}
	
	@Override
	public String toString() {
		return " return " + (returnValue == null ? "" : returnValue);
	}
}
