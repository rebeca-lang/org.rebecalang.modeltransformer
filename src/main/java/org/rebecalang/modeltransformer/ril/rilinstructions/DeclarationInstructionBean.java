package org.rebecalang.modeltransformer.ril.rilinstructions;

public class DeclarationInstructionBean extends InstructionBean {

	String varName;
	
	public DeclarationInstructionBean(String varName) {
		super();
		this.varName = varName;
	}

//	@Override
//	public void interpret(ActorState actorState, State globalState) {
//		actorState.addVariableToRecentScope(varName, 0);
//		actorState.increasePC();
//	}
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}

	@Override
	public String toString() {
		return "declare " + varName;
	}
}
