package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.util.List;

public abstract class AbstractCallingInstructionBean extends InstructionBean {
	protected Variable base;
	protected String methodName;
	protected List<Object> parameters;

	
	public AbstractCallingInstructionBean(Variable base, String methodName, List<Object> parameters) {
		super();
		this.base = base;
		this.methodName = methodName;
		this.parameters = parameters;
	}
	public Variable getBase() {
		return base;
	}
	public void setBase(Variable base) {
		this.base = base;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public List<Object> getParameters() {
		return parameters;
	}
	public void setParameters(List<Object> parameters) {
		this.parameters = parameters;
	}
	public void addParameter(Object parameter) {
		this.parameters.add(parameter);
	}

}