package org.rebecalang.modeltransformer.ril;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class RilUtils {
	public static void printMethodBodies(RILModel rilModel) {
		for(String methodName : rilModel.getMethodNames()) {
			System.out.println(methodName);
			int counter = 0;
			for(InstructionBean instruction : rilModel.getInstructionList(methodName)) {
				System.out.println("" + counter++ +":" + instruction);
			}
			System.out.println("...............................................");
		}

	}
}
