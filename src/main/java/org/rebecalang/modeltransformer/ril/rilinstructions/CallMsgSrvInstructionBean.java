package org.rebecalang.modeltransformer.ril.rilinstructions;

import java.util.ArrayList;

public class CallMsgSrvInstructionBean extends InstructionBean {

	Variable receiver;
	String msgsrvName;
	private ArrayList<Object> parameters;

	public CallMsgSrvInstructionBean(Variable receiver, String msgsrvName) {
		this(receiver, msgsrvName, new ArrayList<Object>());
	}

	public CallMsgSrvInstructionBean(Variable receiver, String msgsrvName, ArrayList<Object> parameters) {
		super();
		this.receiver = receiver;
		this.msgsrvName = msgsrvName;
		this.parameters = parameters;
	}
	// @Override
	// public void interpret(ActorState actorState, State globalState) {
	//
	// MessageSpecification msgSpec = new MessageSpecification(msgsrvName, null,
	// actorState);
	// ActorState receiverState = (ActorState)
	// actorState.retreiveVariableValue(receiver);
	// receiverState.addToQueue(msgSpec);
	// actorState.increasePC();
	//
	// }

	public Variable getReceiver() {
		return receiver;
	}

	public void setReceiver(Variable receiver) {
		this.receiver = receiver;
	}

	public String getMsgsrvName() {
		return msgsrvName;
	}

	public void setMsgsrvName(String msgsrvName) {
		this.msgsrvName = msgsrvName;
	}

	@Override
	public String toString() {
		String string = receiver + "." + msgsrvName + "( ";
		for (Object parameter: parameters) {
			string += parameter.toString() + ", ";
		}
		return string + ")";
	}

	public ArrayList<Object> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Object> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(Object parameter) {
		this.parameters.add(parameter);
	}
}
