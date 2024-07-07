package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.util.ArrayList;
import java.util.List;

public class CallMsgSrvInstructionBean extends AbstractCallingInstructionBean {

	public CallMsgSrvInstructionBean(Variable receiver, String msgsrvName) {
		this(receiver, msgsrvName, new ArrayList<Object>());
	}

	public CallMsgSrvInstructionBean(Variable base, String methodName, List<Object> parameters) {
		super(base, methodName, parameters);
	}

	@Override
	public String toString() {
		String string = base + "." + methodName + "( ";
		for (Object parameter: parameters) {
			string += parameter.toString() + ", ";
		}
		return string + ")";
	}
}
