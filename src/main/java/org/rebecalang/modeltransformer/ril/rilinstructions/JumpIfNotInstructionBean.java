package org.rebecalang.modeltransformer.ril.rilinstructions;

public class JumpIfNotInstructionBean extends InstructionBean {

	Object condition;
	String methodName;
	Integer lineNumber;
	
	public final static int BREAK_JUMP_INDICATOR = Integer.MAX_VALUE;
	public final static int CONTINUE_JUMP_INDICATOR = Integer.MIN_VALUE;
	

	public JumpIfNotInstructionBean(Object condition, String methodName, int lineNumber) {
		super();
		this.condition = condition;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
	}

//	@Override
//	public void interpret(ActorState actorState, State globalState) {
//	
//		if (condition == null) {
//			actorState.setPC(methodName, lineNumber);
//			return;
//		}	
//		Object tempCond = condition;
//		if ((condition instanceof Variable)) {
//			tempCond = (Boolean) actorState.retreiveVariableValue((Variable) condition);
//		}
//		if (tempCond == Boolean.FALSE) {
//			actorState.setPC(methodName, lineNumber);
//		} else {
//			actorState.increasePC();
//		}
//
//	}

	public Object getCondition() {
		return condition;
	}

	public void setCondition(Object condition) {
		this.condition = condition;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public String toString() {
		return "jump if not (" + condition + ") " + "<" + methodName + lineNumber + ">"; 

	}
}
