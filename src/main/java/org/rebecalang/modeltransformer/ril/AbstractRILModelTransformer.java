package org.rebecalang.modeltransformer.ril;

import java.util.ArrayList;
import java.util.Hashtable;

import org.rebecalang.modeltransformer.AbstractModelTransformer;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;

public abstract class AbstractRILModelTransformer extends AbstractModelTransformer {
	protected Hashtable<String, ArrayList<InstructionBean>> transformedModelList = new Hashtable<String, ArrayList<InstructionBean>>();
	
	public Hashtable<String, ArrayList<InstructionBean>> getTransformedModelList() {
		return transformedModelList;
	}
}
