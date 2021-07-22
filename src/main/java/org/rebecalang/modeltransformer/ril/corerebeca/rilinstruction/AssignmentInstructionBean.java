package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

public class AssignmentInstructionBean extends InstructionBean {

	Object leftVarName;

	Object firstOperand;
	Object secondOperand;

	String operator;

	public AssignmentInstructionBean(Object leftVarName, Object firstOperand, Object secondOperand, String operator) {
		super();
		this.leftVarName = leftVarName;
		this.firstOperand = firstOperand;
		this.secondOperand = secondOperand;
		this.operator = operator;
	}

//	public void interpret(ActorState actorState, State globalState) {
//		Object valueFirst = InstructionUtilities.getValue(firstOperand, actorState);
//		Object valueSecond = InstructionUtilities.getValue(secondOperand, actorState);
//		Object result = firstOperand;
//		if (operator != null) {
//			if (valueFirst instanceof ActorState) {
//				if (operator.equals("=="))
//					result = (((ActorState) valueFirst).getName().
//							equals(((ActorState) valueSecond).getName()));
//				else if (operator.equals("!="))
//					result = !(((ActorState) valueFirst).getName().
//							equals(((ActorState) valueSecond).getName()));
//				else
//					throw new RebecaRuntimeInterpreterException(
//							"this case should not happen!! should've been reported as an error by compiler!");
//			} else
//				result = SemanticCheckerUtils.evaluateConstantTerm(operator, null, valueFirst, valueSecond);
//		}
//		actorState.setVariableValue(((Variable) leftVarName).getVarName(), result);
//		actorState.increasePC();
//	}
	
	public Object getLeftVarName() {
		return leftVarName;
	}

	public void setLeftVarName(Object leftVarName) {
		this.leftVarName = leftVarName;
	}

	public Object getFirstOperand() {
		return firstOperand;
	}

	public void setFirstOperand(Object firstOperand) {
		this.firstOperand = firstOperand;
	}

	public Object getSecondOperand() {
		return secondOperand;
	}

	public void setSecondOperand(Object secondOperand) {
		this.secondOperand = secondOperand;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return leftVarName + " = " + firstOperand + (operator == null ? "" : (operator + secondOperand));
	}
}
