package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.util.Map;
import java.util.Map.Entry;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;

public class RebecInstantiationInstructionBean extends InstructionBean {
	private Type type;
	private Map<String, Object> bindings;

	private Variable resultTarget;
	
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
	public Variable getResultTarget() {
		return resultTarget;
	}
	public void setResultTarget(Variable resultTarget) {
		this.resultTarget = resultTarget;
	}
	
	@Override
	public String toString() {
		String string = resultTarget + "->new " + type.getTypeName() + "(";
		for (Entry<String, Object> entry : bindings.entrySet()) {
			string += entry.getKey() + "->" + entry.getValue().toString() + ", ";
		}
		return string + ")";
	}

}