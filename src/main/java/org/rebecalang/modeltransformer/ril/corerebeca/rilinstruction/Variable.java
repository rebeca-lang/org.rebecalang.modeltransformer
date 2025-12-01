package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Variable implements Serializable {
	
	String varName;
	Variable base;
	ArrayList<Object> indeces;

	
	public Variable(String varName) {
		this.varName = varName;
		indeces = new ArrayList<Object>();
	}

	public Variable(Variable base, String varName) {
		this(varName);
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
		String retValue = ((base == null) ? "" : (base.getVarName() + ".")) + varName;
		for(Object var : indeces)
			retValue += "[" + var + "]";
		return retValue;
	}
	public void addIndex(Object object) {
		indeces.add(object);
	}

	public ArrayList<Object> getIndeces() {
		return indeces;
	}
}
