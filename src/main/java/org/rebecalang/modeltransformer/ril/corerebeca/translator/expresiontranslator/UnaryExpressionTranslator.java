package org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.UnaryExpression;
import org.rebecalang.modeltransformer.ril.Rebeca2RILExpressionTranslatorContainer;
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
public class UnaryExpressionTranslator extends AbstractExpressionTranslator {

	@Autowired
	public UnaryExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(expressionTranslatorContainer);
	}

	@Override
	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {

		UnaryExpression unaryExpression = (UnaryExpression) expression;
		String operator = unaryExpression.getOperator();

		Object translatedExpression = expressionTranslatorContainer
				.translate(unaryExpression.getExpression(), instructions);

		Variable tempVariable = getTempVariable();
		instructions.add(new DeclarationInstructionBean(tempVariable.getVarName()));
		AssignmentInstructionBean assignmentInstruction = null;
			assignmentInstruction = new AssignmentInstructionBean(tempVariable, translatedExpression, null, 
					operator);

		instructions.add(assignmentInstruction);
		return tempVariable;
	}

}
