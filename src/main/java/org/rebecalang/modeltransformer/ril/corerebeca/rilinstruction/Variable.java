package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Variable implements Serializable {
	
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
