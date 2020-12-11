package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

public class Variable {
	
	String varName;

	
	public Variable(String varName) {
		super();
		this.varName = varName;
	}

	
	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	@Override
	public String toString() {
		return varName;
	}


}
