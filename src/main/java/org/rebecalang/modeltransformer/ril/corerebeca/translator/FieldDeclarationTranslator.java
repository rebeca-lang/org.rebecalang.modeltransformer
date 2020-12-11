package org.rebecalang.modeltransformer.ril.corerebeca.translator;

import java.util.ArrayList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class FieldDeclarationTranslator extends AbstractStatementTranslator {

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {

		FieldDeclaration fieldDeclaration = (FieldDeclaration) statement;
		List<VariableDeclarator> variableDeclarators = fieldDeclaration.getVariableDeclarators();
		for (VariableDeclarator vd : variableDeclarators) {
			instructions.add(new DeclarationInstructionBean(vd.getVariableName()));
			if (vd.getVariableInitializer() != null) {
				if(vd.getVariableInitializer() instanceof ArrayVariableInitializer)
					throw new RuntimeException("Array initialization is not handled yet!");
				OrdinaryVariableInitializer variableInitializer = (OrdinaryVariableInitializer)(vd.getVariableInitializer());
				Object initializationResult = ExpressionTranslatorContainer.getInstance().translate(variableInitializer.getValue(), instructions);
				instructions.add(new AssignmentInstructionBean(new Variable(vd.getVariableName()), initializationResult, null, null));
			}

		}
	}
}