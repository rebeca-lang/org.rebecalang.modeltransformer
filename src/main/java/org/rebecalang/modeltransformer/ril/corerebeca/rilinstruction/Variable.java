package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Variable implements Serializable {
	
	String varName;
	Variable base;

	
	public Variable(String varName) {
		this.varName = varName;
	}

	public Variable(Variable base, String varName) {
		this.varName = varName;
		this.base = base;
	}
	
	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public Variable getBase() {
		return base;
	}
	
	public void setBase(Variable base) {
		this.base = base;
	}
	
	@Override
	public String toString() {
		return ((base == null) ? "" : (base.getVarName() + ".")) + varName;
	}


}
