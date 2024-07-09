package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.util.Map;

public abstract class AbstractCallingInstructionBean extends InstructionBean {
	protected Variable base;
	protected String methodName;
	protected Map<String, Object> parameters;

	
	public AbstractCallingInstructionBean(Variable base, String methodName, Map<String, Object> parameters) {
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
	public Map<String, Object> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	public void addParameter(String paramName, Object paramValue) {
		this.parameters.put(paramName, paramValue);
	}

}