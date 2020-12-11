package org.rebecalang.modeltransformer.ril.corerebeca.translator.expresiontranslator;

import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Expression;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.utils.TypesUtilities;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;


public class LiteralStatementTranslator extends AbstractExpressionTranslator {

	@Override
	public Object translate(Expression expression, ArrayList<InstructionBean> instructions) {
		Literal literal = (Literal) expression;
		if(TypesUtilities.getInstance().canTypeCastTo(literal.getType(), TypesUtilities.INT_TYPE))
			return Integer.parseInt(literal.getLiteralValue());
		if(TypesUtilities.getInstance().canTypeCastTo(literal.getType(), TypesUtilities.DOUBLE_TYPE))
			return Double.parseDouble(literal.getLiteralValue());
		if(literal.getType() == TypesUtilities.BOOLEAN_TYPE)
			return Boolean.valueOf(literal.getLiteralValue());
		if(literal.getType() == TypesUtilities.STRING_TYPE)
			return String.valueOf(literal);
		return null;
	}

}
