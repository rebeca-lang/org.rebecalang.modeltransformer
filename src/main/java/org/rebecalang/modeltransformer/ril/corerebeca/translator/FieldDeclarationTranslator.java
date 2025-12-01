package org.rebecalang.modeltransformer.ril.corerebeca.translator;

import java.util.ArrayList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Statement;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.Rebeca2RILStatementTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FieldDeclarationTranslator extends AbstractStatementTranslator {

	@Autowired
	public FieldDeclarationTranslator(Rebeca2RILStatementTranslatorContainer statementTranslatorContainer,
			Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(statementTranslatorContainer, expressionTranslatorContainer);
	}

	@Override
	public void translate(Statement statement, ArrayList<InstructionBean> instructions) {

		FieldDeclaration fieldDeclaration = (FieldDeclaration) statement;
		List<VariableDeclarator> variableDeclarators = fieldDeclaration.getVariableDeclarators();
		for (VariableDeclarator vd : variableDeclarators) {
			instructions.add(new DeclarationInstructionBean(vd.getVariableName(), fieldDeclaration.getType()));
			if (vd.getVariableInitializer() != null) {
				if(vd.getVariableInitializer() instanceof ArrayVariableInitializer)
					throw new RuntimeException("Array initialization is not handled yet!");
				OrdinaryVariableInitializer variableInitializer = (OrdinaryVariableInitializer)(vd.getVariableInitializer());
				Object initializationResult = expressionTranslatorContainer.translate(variableInitializer.getValue(), instructions);
				instructions.add(new AssignmentInstructionBean(new Variable(vd.getVariableName()), initializationResult, null, null));
			}
		}
	}
}