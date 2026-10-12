package org.rebecalang.modeltransformer.ril;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class RILModel {
	Hashtable<String, ArrayList<InstructionBean>> transformedRILModel;
	
	public RILModel() {
		transformedRILModel = new Hashtable<String, ArrayList<InstructionBean>>();
	}
	
	public void addMethod(String computedMethodName, ArrayList<InstructionBean> instructions) {
		transformedRILModel.put(computedMethodName, instructions);
	}

	public Set<String> getMethodNames() {
		return transformedRILModel.keySet();
	}

	public ArrayList<InstructionBean> getInstructionList(String methodName) {
		return transformedRILModel.get(methodName);
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		for(String methodName : this.getMethodNames()) {
			System.out.println(methodName);
			int counter = 0;
			for(InstructionBean instruction : this.getInstructionList(methodName)) {
				result.append(counter++);
				result.append(":");
				result.append(instruction);
				result.append("\r\n");
			}
		}
		return result.toString();
	}
}
