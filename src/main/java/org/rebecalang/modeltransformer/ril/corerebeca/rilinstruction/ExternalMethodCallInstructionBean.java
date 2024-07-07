package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.util.ArrayList;

public class ExternalMethodCallInstructionBean extends AbstractCallingInstructionBean {

	private Variable functionCallResult;

	public ExternalMethodCallInstructionBean(Variable base, String methodName) {
		this(base, methodName, new ArrayList<Object>(), null);
	}

	public ExternalMethodCallInstructionBean(Variable base, String methodName, ArrayList<Object> parameters, Variable functionCallResult) {
		super(base, methodName, parameters);
		this.setFunctionCallResult(functionCallResult);
	}

	@Override
	public String toString() {
		String string = base + "." + methodName + "( ";
		for (Object parameter: parameters) {
			string += parameter.toString() + ", ";
		}
		return string + ")";
	}

	public Variable getFunctionCallResult() {
		return functionCallResult;
	}

	public void setFunctionCallResult(Variable functionCallResult) {
		this.functionCallResult = functionCallResult;
	}
}
