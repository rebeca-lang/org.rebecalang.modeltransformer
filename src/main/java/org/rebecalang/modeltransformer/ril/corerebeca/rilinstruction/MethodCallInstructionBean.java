package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MethodCallInstructionBean extends AbstractCallingInstructionBean {

	private Variable functionCallResult;

	public MethodCallInstructionBean(Variable base, String methodName) {
		this(base, methodName, new TreeMap<String, Object>(), null);
	}

	public MethodCallInstructionBean(Variable base, String methodName, Map<String, Object> parameters, Variable functionCallResult) {
		super(base, methodName, parameters);
		this.setFunctionCallResult(functionCallResult);
	}

	@Override
	public String toString() {
		String string = base + "." + methodName + "( ";
		for (Entry<String, Object> entry : parameters.entrySet()) {
			string += entry.getKey() + "->" + entry.getValue().toString() + ", ";
		}
		return string + ") -> " + (functionCallResult == null ? "" : " -> " + functionCallResult);
	}

	public Variable getFunctionCallResult() {
		return functionCallResult;
	}

	public void setFunctionCallResult(Variable functionCallResult) {
		this.functionCallResult = functionCallResult;
	}
}
