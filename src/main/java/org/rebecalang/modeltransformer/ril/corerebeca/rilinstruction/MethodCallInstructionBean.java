package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.util.ArrayList;
import java.util.List;

public class MethodCallInstructionBean extends InstructionBean {

	private	Variable base;
	private String methodName;
	private List<Object> parameters;
	private List<String> parametersNames;
	private Variable functionCallResult;

	public MethodCallInstructionBean(Variable base, String methodName) {
		this(base, methodName, new ArrayList<Object>(), null);
	}

	public MethodCallInstructionBean(Variable base, String methodName, ArrayList<Object> parameters, Variable functionCallResult) {
		super();
		this.base = base;
		this.methodName = methodName;
		this.parameters = parameters;
		this.setFunctionCallResult(functionCallResult);
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

	public List<Object> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Object> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(Object parameter) {
		this.parameters.add(parameter);
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
