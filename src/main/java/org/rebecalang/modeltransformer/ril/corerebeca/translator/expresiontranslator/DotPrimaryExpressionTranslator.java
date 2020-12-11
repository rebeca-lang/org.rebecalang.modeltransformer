package org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.DotPrimary;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.TermPrimary;
import org.rebecalang.modeltransformer.ril.ExpressionTranslatorContainer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class DotPrimaryExpressionTranslator extends AbstractExpressionTranslator {

	@Override
	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {

		DotPrimary dotPrimary = (DotPrimary) expression;
		Variable leftSide = (Variable) ExpressionTranslatorContainer.getInstance().translate(dotPrimary.getLeft(),
				instructions);
		TermPrimary rightSide = (TermPrimary) dotPrimary.getRight();
		TermPrimaryExpressionTranslator translator = 
				(TermPrimaryExpressionTranslator) ExpressionTranslatorContainer.getInstance().getTranslator(TermPrimary.class);
		return translator.translate(dotPrimary.getLeft().getType(), leftSide, rightSide, instructions);
	}

}
