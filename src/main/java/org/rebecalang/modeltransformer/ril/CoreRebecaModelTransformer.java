package org.rebecalang.modeltransformer.ril;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ConstructorDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MsgsrvDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.modeltransformer.ril.rilinstructions.EndMethodInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.rilinstructions.InstructionBean;

public class CoreRebecaModelTransformer extends AbstractRILModelTransformer  {

	@Override
	public void transformModel() throws IOException {

		List<ReactiveClassDeclaration> reactiveClassDeclarations = this.rebecaModel.getRebecaCode().getReactiveClassDeclaration();
		for (ReactiveClassDeclaration rcd : reactiveClassDeclarations) {
			for(MsgsrvDeclaration msgsrv : rcd.getMsgsrvs()) {
				ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
				String computedMethodName = RILUtilities.computeMethodName(rcd, msgsrv);
				transformedModelList.put(computedMethodName, instructions);
				StatementTranslatorContainer.getInstance().prepare(rcd, computedMethodName);
				StatementTranslatorContainer.getInstance().translate(msgsrv.getBlock(), instructions);
				instructions.add(new EndMsgSrvInstructionBean());
			}
			for(ConstructorDeclaration constructorDeclaration : rcd.getConstructors()) {
				ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
				String computedMethodName = RILUtilities.computeMethodName(rcd, constructorDeclaration);
				transformedModelList.put(computedMethodName, instructions);
				StatementTranslatorContainer.getInstance().prepare(rcd, computedMethodName);
				StatementTranslatorContainer.getInstance().translate(constructorDeclaration.getBlock(), instructions);
				instructions.add(new EndMsgSrvInstructionBean());
			}
			for(MethodDeclaration methodDeclaration : rcd.getSynchMethods()) {
				ArrayList<InstructionBean> instructions = new ArrayList<InstructionBean>();
				String computedMethodName = RILUtilities.computeMethodName(rcd, methodDeclaration);
				transformedModelList.put(computedMethodName, instructions);
				StatementTranslatorContainer.getInstance().prepare(rcd, computedMethodName);
				StatementTranslatorContainer.getInstance().translate(methodDeclaration.getBlock(), instructions);
				instructions.add(new EndMethodInstructionBean());
			}
		}
	}

}
