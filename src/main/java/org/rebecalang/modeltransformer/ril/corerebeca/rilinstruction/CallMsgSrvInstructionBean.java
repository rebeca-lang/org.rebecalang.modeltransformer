package org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class CallMsgSrvInstructionBean extends AbstractCallingInstructionBean {

	public CallMsgSrvInstructionBean(Variable receiver, String msgsrvName) {
		this(receiver, msgsrvName, new TreeMap<String, Object>());
	}

	public CallMsgSrvInstructionBean(Variable base, String methodName, Map<String, Object> parameters) {
		super(base, methodName, parameters);
	}

	@Override
	public String toString() {
		String string = base + "." + methodName + "( ";
		for (Entry<String, Object> entry : parameters.entrySet()) {
			string += entry.getKey() + "->" + entry.getValue().toString() + ", ";
		}
		return string + ")";
	}
}
