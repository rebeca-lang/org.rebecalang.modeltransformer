package org.rebecalang.modeltransformer.ril.corerebeca.translator.expressiontranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BinaryExpression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
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
public class BinaryExpressionTranslator extends AbstractExpressionTranslator {

	private final static Set<String> assignmentOperators = new HashSet<String>(
			Arrays.asList("+=", "-=", "*=", "/=", "%=", "&=", "|=", "="));

	@Autowired
	public BinaryExpressionTranslator(Rebeca2RILExpressionTranslatorContainer expressionTranslatorContainer) {
		super(expressionTranslatorContainer);
	}

	@Override
	public Object translate(Expression expression , ArrayList<InstructionBean> instructions) {
		BinaryExpression binaryExpression = (BinaryExpression) expression;
		String operator = binaryExpression.getOperator();
		Object leftSide = expressionTranslatorContainer.translate(binaryExpression.getLeft(), instructions);
		Object rightSide = expressionTranslatorContainer.translate(binaryExpression.getRight(), instructions);
		if (assignmentOperators.contains(operator)) {
			AssignmentInstructionBean assignmentInstruction;
			if (operator.equals("=")) {
				assignmentInstruction = new AssignmentInstructionBean(leftSide, rightSide, null, null);
			} else {
				assignmentInstruction = new AssignmentInstructionBean(leftSide,
						leftSide, rightSide, String.valueOf(operator.charAt(0)));
			}
			instructions.add(assignmentInstruction);				
		} else {
			Variable tempVariable = getTempVariable();
			instructions.add(new DeclarationInstructionBean(tempVariable.getVarName(), binaryExpression.getType()));
			AssignmentInstructionBean assignmentInstruction = new AssignmentInstructionBean(tempVariable, 
					leftSide, rightSide, operator);
			instructions.add(assignmentInstruction);
			return tempVariable;
		}
		
		return null;
	}

}
