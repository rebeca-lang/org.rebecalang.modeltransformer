package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.util.Map;
import java.util.Map.Entry;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;

public class RebecInstantiationInstructionBean extends InstructionBean {
	private Type type;
	private Map<String, Object> bindings;
	private Map<String, Object> constructorParameters;
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Map<String, Object> getBindings() {
		return bindings;
	}
	public void setBindings(Map<String, Object> bindings) {
		this.bindings = bindings;
	}
	public Map<String, Object> getConstructorParameters() {
		return constructorParameters;
	}
	public void setConstructorParameters(Map<String, Object> constructorParameters) {
		this.constructorParameters = constructorParameters;
	}
	@Override
	public String toString() {
		String string = "new " + type.getTypeName() + "( ";
		for (Entry<String, Object> entry : bindings.entrySet()) {
			string += entry.getKey() + "->" + entry.getValue().toString() + ", ";
		}
		string += "):(";
		for (Entry<String, Object> entry : constructorParameters.entrySet()) {
			string += entry.getKey() + "->" + entry.getValue().toString() + ", ";
		}
		return string + ")";
	}

}