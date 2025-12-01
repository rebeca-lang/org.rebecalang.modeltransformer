package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;

public class DeclarationInstructionBean extends InstructionBean {

	String varName;
	Type type;
	
	public DeclarationInstructionBean(String varName, Type type) {
		super();
		this.varName = varName;
		this.type = type;
	}
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}

	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "declare " + varName;
	}
}
