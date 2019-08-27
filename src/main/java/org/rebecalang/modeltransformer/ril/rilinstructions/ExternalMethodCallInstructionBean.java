package org.rebecalang.modeltransformer.ril.rilinstructions;

import java.util.ArrayList;

public class ExternalMethodCallInstructionBean extends InstructionBean {

	private	Variable base;
	private String methodName;
	private ArrayList<Object> parameters;
	private Variable functionCallResult;

	public ExternalMethodCallInstructionBean(Variable base, String methodName) {
		this(base, methodName, new ArrayList<Object>(), null);
	}

	public ExternalMethodCallInstructionBean(Variable base, String methodName, ArrayList<Object> parameters, Variable functionCallResult) {
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
		return string + ")";
	}

	public ArrayList<Object> getParameters() {
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
}
