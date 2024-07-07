package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.util.ArrayList;
import java.util.List;

public class MethodCallInstructionBean extends AbstractCallingInstructionBean {

	private List<String> parametersNames;
	private Variable functionCallResult;

	public MethodCallInstructionBean(Variable base, String methodName) {
		this(base, methodName, new ArrayList<Object>(), null);
	}

	public MethodCallInstructionBean(Variable base, String methodName, List<Object> parameters, Variable functionCallResult) {
		super(base, methodName, parameters);
		this.setFunctionCallResult(functionCallResult);
	}

	@Override
	public String toString() {
		String string = base + "." + methodName + "( ";
		for (Object parameter: parameters) {
			string += parameter.toString() + ", ";
		}
		if (functionCallResult != null)
			string = functionCallResult + " = " + string;
		return string + ")";
	}

	public Variable getFunctionCallResult() {
		return functionCallResult;
	}

	public void setFunctionCallResult(Variable functionCallResult) {
		this.functionCallResult = functionCallResult;
	}

	public List<String> getParametersNames() {
		return parametersNames;
	}

	public void setParametersNames(List<String> parametersNames) {
		this.parametersNames = parametersNames;
	}
	
}
