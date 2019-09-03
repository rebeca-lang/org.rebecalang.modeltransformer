package org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction;

import java.util.ArrayList;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.CallMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class CallTimedMsgSrvInstructionBean extends CallMsgSrvInstructionBean {

	private Object after;
	private Object deadline;

	public CallTimedMsgSrvInstructionBean(Variable receiver, String msgsrvName, ArrayList<Object> parameters,
			Object after, Object deadline) {
		super(receiver, msgsrvName, parameters);
		this.after = after;
		this.deadline = deadline;
	}

	

	@Override
	public String toString() {
		String string = super.toString();
		Object deadlineValue = getDeadline();
		deadlineValue = (deadlineValue.equals(Integer.MAX_VALUE) ? "inf" : deadlineValue.toString());
		string += " after(" + getAfter() + ") deadline(" + deadlineValue + ")";
		return string;
	}


	public Object getAfter() {
		if (after == null)
			after = 0;
		return after;
	}

	public void setAfter(Object after) {
		this.after = after;
	}

	public Object getDeadline() {
		if (deadline == null)
			deadline = Integer.MAX_VALUE;
		return deadline;
	}

	public void setDeadline(Object deadline) {
		this.deadline = deadline;
	}
}